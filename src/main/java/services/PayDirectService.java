package services;

import com.bw.payment.entity.*;
import com.bw.payment.enumeration.GenericStatusConstant;
import com.bw.payment.enumeration.PaymentProviderConstant;
import com.bw.payment.enumeration.PaymentResponseStatusConstant;
import com.bw.payment.enumeration.PaymentTransactionStatus;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import dao.PaymentTransactionDao;
import ninja.Context;
import ninja.utils.NinjaProperties;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojo.MerchantPaymentValidationPojo;
import pojo.TransactionNotificationPojo;
import pojo.payDirect.customerValidation.request.CustomerInformationRequest;
import pojo.payDirect.customerValidation.response.Customer;
import pojo.payDirect.customerValidation.response.CustomerInformationResponse;
import pojo.payDirect.customerValidation.response.Customers;
import pojo.payDirect.customerValidation.response.PaymentItems;
import pojo.payDirect.paymentNotification.request.Payment;
import pojo.payDirect.paymentNotification.request.PaymentNotificationRequest;
import pojo.payDirect.paymentNotification.response.PaymentNotificationResponse;
import pojo.payDirect.paymentNotification.response.PaymentResponsePojo;
import pojo.payDirect.paymentNotification.response.Payments;
import utils.Constants;
import utils.PaymentUtil;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static utils.PaymentUtil.getAmountInKobo;

/**
 * CREATED BY GIBAH
 */
public class PayDirectService {
    public static final int CUSTOMER_VALID = 0;
    public static final int CUSTOMER_INVALID = 1;
    public static final int CUSTOMER_EXPIRED = 2;

    public static final int NOTIFICATION_RECEIVED = 0;
    public static final int NOTIFICATION_REJECTED = 1;

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");


    private final OkHttpClient client = new OkHttpClient();
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Inject
    PaymentTransactionDao paymentTransactionDao;
    @Inject
    NinjaProperties ninjaProperties;

    public CustomerInformationResponse processCustomerValidationRequest(CustomerInformationRequest request, Context context) {
        CustomerInformationResponse customerInformationResponse = new CustomerInformationResponse();
        customerInformationResponse.setMerchantReference(request.getMerchantReference());

        PaymentTransaction paymentTransaction = paymentTransactionDao.getUniqueRecordByProperty(PaymentTransaction.class, "transactionId",
                String.valueOf(request.getCustReference()));
        if (paymentTransaction == null) {
            Customer customer = new Customer();
            customer.setFirstName("");
            customer.setCustReference(request.getCustReference());
            customer.setStatus(1);
            customer.setStatusMessage("Invalid transaction");
            customer.setCustomerReferenceAlternate("");

            Customers customers = new Customers();
            customers.addCustomer(customer);

            customerInformationResponse.setCustomers(customers);
            return customerInformationResponse;
        }

        if (!paymentTransaction.getPaymentProvider().equals(PaymentProviderConstant.INTERSWITCH)) {
            Customer customer = new Customer();
            customer.setFirstName("");
            customer.setCustReference(request.getCustReference());
            customer.setStatus(1);
            customer.setStatusMessage("Invalid payment provider");
            customer.setCustomerReferenceAlternate("");

            Customers customers = new Customers();
            customers.addCustomer(customer);

            customerInformationResponse.setCustomers(customers);
            return customerInformationResponse;
        }

        if (ninjaProperties.isProd()) {
            Merchant merchant = paymentTransactionDao.getMerchantByMerchantId(request.getMerchantReference(), PaymentProviderConstant.INTERSWITCH);
            if (merchant == null) {
                Customer customer = new Customer();
                customer.setFirstName("");
                customer.setCustReference(request.getCustReference());
                customer.setStatus(1);
                customer.setStatusMessage("Merchant not found");
                customer.setCustomerReferenceAlternate("");

                Customers customers = new Customers();
                customers.addCustomer(customer);

                customerInformationResponse.setCustomers(customers);
                return customerInformationResponse;
            }

            if (!paymentTransaction.getMerchant().getId().equals(merchant.getId())) {
                Customer customer = new Customer();
                customer.setFirstName("");
                customer.setCustReference(request.getCustReference());
                customer.setStatus(1);
                customer.setStatusMessage("Merchant does not match");
                customer.setCustomerReferenceAlternate("");

                Customers customers = new Customers();
                customers.addCustomer(customer);

                customerInformationResponse.setCustomers(customers);
                return customerInformationResponse;
            }
        }

        Payer payer = paymentTransactionDao.getRecordById(Payer.class, paymentTransaction.getPayer().getId());
        List<Item> items = paymentTransactionDao.getPaymentTransactionItems(paymentTransaction.getId(), GenericStatusConstant.ACTIVE);

        Customer customer = new Customer();
        customer.setFirstName(payer.getFirstName());
        customer.setLastName(payer.getLastName());
        customer.setEmail(payer.getEmail());
        customer.setPhone(payer.getPhoneNumber());
        customer.setAmount(PaymentUtil.getAmountInNaira(paymentTransaction.getAmountInKobo()));
        customer.setCustReference(paymentTransaction.getTransactionId());
        customer.setStatus(0);

        PaymentItems customerPaymentItems = new PaymentItems();
        for (Item item : items) {
            pojo.payDirect.customerValidation.response.Item paymentItem = new pojo.payDirect.customerValidation.response.Item();
            paymentItem.setProductName(item.getName());
            paymentItem.setProductCode(item.getItemId());
            paymentItem.setQuantity(item.getQuantity().longValue());
            paymentItem.setPrice(PaymentUtil.getAmountInNaira(item.getPriceInKobo()));
            paymentItem.setSubtotal(PaymentUtil.getAmountInNaira(item.getSubTotalInKobo()));
            if (item.getTaxInKobo() != null) {
                paymentItem.setTax(PaymentUtil.getAmountInNaira(item.getTaxInKobo()));
            }
            paymentItem.setTotal(PaymentUtil.getAmountInNaira(item.getTotalInKobo()));

            customerPaymentItems.addItem(paymentItem);
        }

        if (customerPaymentItems.getItems() != null) {
            customer.setPaymentItems(customerPaymentItems);
        }

        Customers customers = new Customers();
        customers.addCustomer(customer);

        customerInformationResponse.setCustomers(customers);
        return customerInformationResponse;
    }

    @Transactional
    public PaymentNotificationResponse processPaymentNotification(PaymentNotificationRequest request, Context context) {
        PaymentNotificationResponse paymentNotificationResponsePojo = new PaymentNotificationResponse();
//

        PaymentResponsePojo responsePojo = new PaymentResponsePojo();
        if (request.getPayments() == null) {
            return null;
        }

        logger.info("<=== payments size = " + request.getPayments().getPayment().size());
        for (Payment paymentPojo : request.getPayments().getPayment()) {
//            if is duplicate notification
            if (paymentTransactionDao.isDuplicateNotification(paymentPojo) && paymentTransactionDao.isProcessed(paymentPojo)) {
                logger.info("<=== duplicate notification");
                responsePojo.setPaymentLogId(paymentPojo.getPaymentLogId());
                responsePojo.setStatus(NOTIFICATION_RECEIVED);
                responsePojo.setStatusMessage("Duplicate notification");

                Payments payments = new Payments();
                payments.addPayment(responsePojo);

                paymentNotificationResponsePojo.setPayments(payments);
                break;
            }

            PaymentTransaction paymentTransaction = paymentTransactionDao.getUniqueRecordByProperty(PaymentTransaction.class, "transactionId",
                    paymentPojo.getCustReference());

//            check if transaction exists
            if (paymentTransaction == null) {
                responsePojo.setPaymentLogId(paymentPojo.getPaymentLogId());
                responsePojo.setStatus(NOTIFICATION_REJECTED);
                responsePojo.setStatusMessage("Invalid transaction");

                Payments payments = new Payments();
                payments.addPayment(responsePojo);

                paymentNotificationResponsePojo.setPayments(payments);
                savePaymentNotificationRequest(paymentPojo, request, false, true, PaymentResponseStatusConstant.REJECTED,
                        responsePojo.getStatusMessage());
                break;
            }

//            check if the transaction has the right provider
            if (!paymentTransaction.getPaymentProvider().equals(PaymentProviderConstant.INTERSWITCH)) {
                responsePojo.setPaymentLogId(paymentPojo.getPaymentLogId());
                responsePojo.setStatus(NOTIFICATION_REJECTED);
                responsePojo.setStatusMessage("Invalid provider");

                Payments payments = new Payments();
                payments.addPayment(responsePojo);

                paymentNotificationResponsePojo.setPayments(payments);
                savePaymentNotificationRequest(paymentPojo, request, false, true, PaymentResponseStatusConstant.REJECTED,
                        responsePojo.getStatusMessage());
                break;
            }

            Date dateOfPayment = null;
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(Constants.INTERSWITCH_DATE_FORMAT);
                dateOfPayment = sdf.parse(paymentPojo.getPaymentDate());
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (dateOfPayment == null || dateOfPayment.after(new Date())) {
                responsePojo.setPaymentLogId(paymentPojo.getPaymentLogId());
                responsePojo.setStatus(NOTIFICATION_REJECTED);
                responsePojo.setStatusMessage("Invalid payment date");

                Payments payments = new Payments();
                payments.addPayment(responsePojo);

                paymentNotificationResponsePojo.setPayments(payments);
                savePaymentNotificationRequest(paymentPojo, request, false, true, PaymentResponseStatusConstant.REJECTED,
                        responsePojo.getStatusMessage());
                break;
            }

            if (paymentTransaction.getValidateTransaction()) {
                MerchantPaymentValidationPojo val = validatePaymentWithMerchant(paymentPojo, paymentTransaction.getTransactionValidationUrl());
                if (val == null) {
                    return null;
                } else if (!val.isValid()) {
                    responsePojo.setPaymentLogId(paymentPojo.getPaymentLogId());
                    responsePojo.setStatus(NOTIFICATION_REJECTED);
                    responsePojo.setStatusMessage(val.getReason());

                    Payments payments = new Payments();
                    payments.addPayment(responsePojo);

                    paymentNotificationResponsePojo.setPayments(payments);
                    savePaymentNotificationRequest(paymentPojo, request, false, true, PaymentResponseStatusConstant.REJECTED,
                            responsePojo.getStatusMessage());
                    break;
                }
            }

//            if its a reversal request
            if (paymentPojo.getReversal()) {
//                check if its the right amount
//                if (paymentTransaction.getAmountInKobo().equals(getAmountInKobo(paymentPojo.getAmount().abs()))) {


                saveCurrentPaymentTransactionState(paymentTransaction);

                paymentTransaction.setPaymentTransactionStatus(PaymentTransactionStatus.PENDING);

                paymentTransactionDao.updateObject(paymentTransaction);

                if (paymentTransaction.getNotifyOnStatusChange()) {
                    queueNotification(paymentPojo, paymentTransaction);
                }

                logger.info("<=== Payment reversed");
                responsePojo.setPaymentLogId(paymentPojo.getPaymentLogId());
                responsePojo.setStatus(NOTIFICATION_RECEIVED);

                Payments payments = new Payments();
                payments.addPayment(responsePojo);

                paymentNotificationResponsePojo.setPayments(payments);

                savePaymentNotificationRequest(paymentPojo, request, false, true, PaymentResponseStatusConstant.ACCEPTED,
                        responsePojo.getStatusMessage());
//                } else {
//
//                    responsePojo.setPaymentLogId(paymentPojo.getPaymentLogId());
//                    responsePojo.setStatus(NOTIFICATION_REJECTED);
//                    responsePojo.setStatusMessage(String.format("Invalid amount. Expected N%s", PaymentUtil.getAmountInNaira(paymentTransaction.getAmountInKobo())));
//                    Payments payments = new Payments();
//                    payments.addPayment(responsePojo);
//
//                    paymentNotificationResponsePojo.setPayments(payments);
//
//                    savePaymentNotificationRequest(paymentPojo, request, false, true, PaymentResponseStatusConstant.REJECTED,
//                            responsePojo.getStatusMessage());
//                }

                break;
            }

            if (!paymentTransaction.getAmountInKobo().equals(getAmountInKobo(paymentPojo.getAmount()))) {
                responsePojo.setStatusMessage(String.format(Locale.ENGLISH, "Invalid amount. Expected N%,.2f",
                        PaymentUtil.getAmountInNaira(paymentTransaction.getAmountInKobo())));
                responsePojo.setPaymentLogId(paymentPojo.getPaymentLogId());
                responsePojo.setStatus(NOTIFICATION_REJECTED);

                Payments payments = new Payments();
                payments.addPayment(responsePojo);

                paymentNotificationResponsePojo.setPayments(payments);

                savePaymentNotificationRequest(paymentPojo, request, false, true, PaymentResponseStatusConstant.REJECTED,
                        responsePojo.getStatusMessage());

                break;
            }

            saveCurrentPaymentTransactionState(paymentTransaction);

            logger.info("<=== Payment success");
            paymentTransaction.setPaymentTransactionStatus(PaymentTransactionStatus.SUCCESSFUL);
            paymentTransactionDao.updateObject(paymentTransaction);

            if (paymentTransaction.getNotifyOnStatusChange()) {
                queueNotification(paymentPojo, paymentTransaction);
            }

            responsePojo.setPaymentLogId(paymentPojo.getPaymentLogId());
            responsePojo.setStatus(NOTIFICATION_RECEIVED);

            Payments payments = new Payments();
            payments.addPayment(responsePojo);

            paymentNotificationResponsePojo.setPayments(payments);

            savePaymentNotificationRequest(paymentPojo, request, false, true, PaymentResponseStatusConstant.ACCEPTED,
                    responsePojo.getStatusMessage());
        }
        return paymentNotificationResponsePojo;
    }

    public MerchantPaymentValidationPojo validatePaymentWithMerchant(Object payload, String url) {
        try {
            RequestBody body = RequestBody.create(JSON, PaymentUtil.toJSON(payload));
            Request request = new Request.Builder().url(url).post(body).build();
            Response response = client.newCall(request).execute();

            if (response.isSuccessful() && response.body() != null) {
                MerchantPaymentValidationPojo res = PaymentUtil.fromJSON(response.body().string(), MerchantPaymentValidationPojo.class);
                return res;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void savePaymentNotificationRequest(Payment paymentPojo, PaymentNotificationRequest request,
                                               boolean wasValidated, boolean wasProcessed, PaymentResponseStatusConstant status,
                                               String reasonForOutcome) {
        PaymentResponseLog paymentResponseLog = new PaymentResponseLog();
        paymentResponseLog.setRecieptNumber(paymentPojo.getReceiptNo());
        paymentResponseLog.setPaymentReference(paymentPojo.getPaymentReference());
        paymentResponseLog.setAmountInKobo(PaymentUtil.getAmountInKobo(paymentPojo.getAmount()));
        paymentResponseLog.setPaymentLogId(String.valueOf(paymentPojo.getPaymentLogId()));
        paymentResponseLog.setResponseDump(PaymentUtil.toJSON(request));
        paymentResponseLog.setDateCreated(Timestamp.from(Instant.now()));
        paymentResponseLog.setPaymentTransaction(paymentTransactionDao.getUniqueRecordByProperty(PaymentTransaction.class, "transactionId",
                paymentPojo.getCustReference()));
        paymentResponseLog.setValidated(wasValidated);
        paymentResponseLog.setProcessed(wasProcessed);
        paymentResponseLog.setStatus(status);
        paymentResponseLog.setReason(reasonForOutcome);

        paymentTransactionDao.saveObject(paymentResponseLog);

    }

    private void queueNotification(Payment paymentPojo, PaymentTransaction paymentTransaction) {
        TransactionNotificationPojo transactionNotificationPojo = new TransactionNotificationPojo();
        transactionNotificationPojo.setStatus(paymentTransaction.getPaymentTransactionStatus().getValue());
        transactionNotificationPojo.setTransactionId(paymentTransaction.getTransactionId());
        transactionNotificationPojo.setDatePaymentReceived(PaymentUtil.format(Timestamp.from(Instant.now()), Constants.ISO_DATE_TIME_FORMAT));
        transactionNotificationPojo.setReceiptNumber(paymentPojo.getReceiptNo());
        transactionNotificationPojo.setAmountPaidInKobo(PaymentUtil.getAmountInKobo(paymentPojo.getAmount()));
        transactionNotificationPojo.setPaymentProvider(paymentTransaction.getPaymentProvider().getValue());
        transactionNotificationPojo.setPaymentProviderTransactionId(String.valueOf(paymentPojo.getPaymentLogId()));
        transactionNotificationPojo.setPaymentDate(paymentPojo.getPaymentDate());
        transactionNotificationPojo.setSettlementDate(paymentPojo.getSettlementDate());
        transactionNotificationPojo.setPaymentChannelName(paymentPojo.getChannelName());
        transactionNotificationPojo.setPaymentProviderPaymentReference(paymentPojo.getPaymentReference());
        transactionNotificationPojo.setPaymentMethod(paymentPojo.getPaymentMethod());

        NotificationQueue notificationQueue = new NotificationQueue();
        notificationQueue.setMessageInJson(PaymentUtil.toJSON(transactionNotificationPojo));
        notificationQueue.setNotificationUrl(paymentTransaction.getNotificationUrl());
        notificationQueue.setNotificationSent(false);
        notificationQueue.setDateCreated(Timestamp.from(Instant.now()));
        notificationQueue.setPaymentTransaction(paymentTransaction);

        paymentTransactionDao.saveObject(notificationQueue);
    }

    private void saveCurrentPaymentTransactionState(PaymentTransaction paymentTransaction) {
        PaymentTransactionStateLog paymentTransactionStateLog = new PaymentTransactionStateLog();

        paymentTransactionStateLog.setStateDump(PaymentUtil.toJSONWithAdaptor(paymentTransaction));
        paymentTransactionStateLog.setDateCreated(Timestamp.from(Instant.now()));
        paymentTransactionStateLog.setPaymentTransaction(paymentTransaction);

        paymentTransactionDao.saveObject(paymentTransactionStateLog);
    }
}
