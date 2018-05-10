package services;

import com.bw.payment.entity.*;
import com.bw.payment.enumeration.PaymentChannelConstant;
import com.bw.payment.enumeration.PaymentProviderConstant;
import com.bw.payment.enumeration.PaymentResponseStatusConstant;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import dao.MerchantDao;
import dao.PaymentTransactionDao;
import ninja.Context;
import ninja.utils.NinjaProperties;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojo.*;
import pojo.payDirect.customerValidation.request.CustomerInformationRequest;
import pojo.payDirect.customerValidation.response.Customer;
import pojo.payDirect.customerValidation.response.CustomerInformationResponse;
import pojo.payDirect.customerValidation.response.Customers;
import pojo.payDirect.paymentNotification.request.Payment;
import pojo.payDirect.paymentNotification.request.PaymentNotificationRequest;
import pojo.payDirect.paymentNotification.response.PaymentNotificationResponse;
import pojo.payDirect.paymentNotification.response.PaymentResponsePojo;
import pojo.payDirect.paymentNotification.response.Payments;
import services.sequence.NotificationIdSequence;
import utils.Constants;
import utils.PaymentUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

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
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    private OkHttpClient client;
    private PaymentTransactionDao paymentTransactionDao;
    private NinjaProperties ninjaProperties;
    private NotificationIdSequence notificationIdSequence;
    @Inject
    private MerchantDao merchantDao;

    @Inject
    public PayDirectService(OkHttpClient client, PaymentTransactionDao paymentTransactionDao, NinjaProperties ninjaProperties,
                            NotificationIdSequence notificationIdSequence) {
        this.paymentTransactionDao = paymentTransactionDao;
        this.ninjaProperties = ninjaProperties;
        this.notificationIdSequence = notificationIdSequence;
        this.client = PaymentUtil.getOkHttpClient(ninjaProperties);
    }

    public CustomerInformationResponse processCustomerValidationRequest(CustomerInformationRequest validationRequest, Context context) {
        CustomerInformationResponse customerInformationResponse = new CustomerInformationResponse();
        customerInformationResponse.setMerchantReference(validationRequest.getMerchantReference());

        Merchant merchant = paymentTransactionDao.getUniqueRecordByProperty(Merchant.class, "paydirectMerchantReference", validationRequest.getMerchantReference());

        if (merchant == null) {
            Customer customer = new Customer();
            customer.setFirstName("");
            customer.setCustReference(validationRequest.getCustReference());
            customer.setStatus(CUSTOMER_INVALID);
            customer.setStatusMessage("Invalid merchant reference");
            customer.setCustomerReferenceAlternate("");

            Customers customers = new Customers();
            customers.addCustomer(customer);

            customerInformationResponse.setCustomers(customers);
            return customerInformationResponse;
        }

        try {
            RequestBody body = RequestBody.create(JSON, PaymentUtil.toJSON(validationRequest));
            Request request = new Request.Builder().url(merchant.getLookupUrl()).post(body).build();
            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                String s = response.body().string();
                Type type = new TypeToken<ApiResponse<TransactionRequestPojo>>() {
                }.getType();
                if (response.code() == 200) {
                    ApiResponse<TransactionRequestPojo> r = PaymentUtil.fromJSON(s, type);

                    TransactionRequestPojo tr = r.getData();
                    Customer customer = new Customer();
                    customer.setFirstName(tr.getPayer().getFirstName());
                    customer.setAmount(PaymentUtil.getAmountInNaira(tr.getAmountInKobo()));
                    customer.setCustReference(validationRequest.getCustReference());
                    customer.setStatus(0);

                    Customers customers = new Customers();
                    customers.addCustomer(customer);

                    customerInformationResponse.setCustomers(customers);
                    return customerInformationResponse;

                }
            } else if (response.code() == 404) {
                Customer customer = new Customer();
                customer.setFirstName("");
                customer.setCustReference(validationRequest.getCustReference());
                customer.setStatus(CUSTOMER_INVALID);
                customer.setStatusMessage("Invalid transaction");
                customer.setCustomerReferenceAlternate("");

                Customers customers = new Customers();
                customers.addCustomer(customer);

                customerInformationResponse.setCustomers(customers);
                return customerInformationResponse;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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
        for (Payment payment : request.getPayments().getPayment()) {
//            if is duplicate notification
            if (paymentTransactionDao.isDuplicateNotification(payment) && paymentTransactionDao.isProcessed(payment)) {
                logger.info("<=== duplicate notification");
                responsePojo.setPaymentLogId(payment.getPaymentLogId());
                responsePojo.setStatus(NOTIFICATION_RECEIVED);
                responsePojo.setStatusMessage("Duplicate notification");

                Payments payments = new Payments();
                payments.addPayment(responsePojo);

                paymentNotificationResponsePojo.setPayments(payments);
                break;
            }

            TransactionRequestPojo transactionRequestPojo = new TransactionRequestPojo();
            transactionRequestPojo.setAmountInKobo(PaymentUtil.getAmountInKobo(payment.getAmount()));
            transactionRequestPojo.setNotifyOnStatusChange(false);
            transactionRequestPojo.setNotificationUrl("");
            transactionRequestPojo.setPaymentProvider(PaymentProviderConstant.INTERSWITCH.getValue());
            transactionRequestPojo.setPaymentChannel(PaymentChannelConstant.PAYDIRECT.getValue());

            try {
                PayerPojo payerPojo = new PayerPojo();
                payerPojo.setFirstName(payment.getCustomerName());
                payerPojo.setLastName("");
                payerPojo.setEmail("");
                payerPojo.setPhoneNumber(payment.getCustomerPhoneNumber());

                transactionRequestPojo.setPayer(payerPojo);
            } catch (Exception e) {
                e.printStackTrace();
            }

            PaymentTransaction paymentTransaction = paymentTransactionDao.createTransaction(transactionRequestPojo, null, null);

//            check if transaction exists
            if (paymentTransaction == null) {
                responsePojo.setPaymentLogId(payment.getPaymentLogId());
                responsePojo.setStatus(NOTIFICATION_REJECTED);
                responsePojo.setStatusMessage("Invalid transaction");

                Payments payments = new Payments();
                payments.addPayment(responsePojo);

                paymentNotificationResponsePojo.setPayments(payments);
                savePaymentNotificationRequest(payment, request, false, true, PaymentResponseStatusConstant.REJECTED,
                        responsePojo.getStatusMessage());
                break;
            }

//            check if the transaction has the right provider
            if (!paymentTransaction.getPaymentProvider().equals(PaymentProviderConstant.INTERSWITCH)) {
                responsePojo.setPaymentLogId(payment.getPaymentLogId());
                responsePojo.setStatus(NOTIFICATION_REJECTED);
                responsePojo.setStatusMessage("Invalid provider");

                Payments payments = new Payments();
                payments.addPayment(responsePojo);

                paymentNotificationResponsePojo.setPayments(payments);
                savePaymentNotificationRequest(payment, request, false, true, PaymentResponseStatusConstant.REJECTED,
                        responsePojo.getStatusMessage());
                break;
            }

            Date dateOfPayment = null;
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(Constants.INTERSWITCH_DATE_FORMAT);
                dateOfPayment = sdf.parse(payment.getPaymentDate());
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (dateOfPayment == null || dateOfPayment.after(new Date())) {
                responsePojo.setPaymentLogId(payment.getPaymentLogId());
                responsePojo.setStatus(NOTIFICATION_REJECTED);
                responsePojo.setStatusMessage("Invalid payment date");

                Payments payments = new Payments();
                payments.addPayment(responsePojo);

                paymentNotificationResponsePojo.setPayments(payments);
                savePaymentNotificationRequest(payment, request, false, true, PaymentResponseStatusConstant.REJECTED,
                        responsePojo.getStatusMessage());
                break;
            }

            saveCurrentPaymentTransactionState(paymentTransaction);

            paymentTransaction.setProviderTransactionReference(payment.getCustReference());
            paymentTransaction.setAmountPaidInKobo(paymentTransaction.getAmountPaidInKobo() + PaymentUtil.getAmountInKobo(payment.getAmount()));
            paymentTransactionDao.updateObject(paymentTransaction);

            queueNotification(payment, paymentTransaction);

            responsePojo.setPaymentLogId(payment.getPaymentLogId());
            responsePojo.setStatus(NOTIFICATION_RECEIVED);

            Payments payments = new Payments();
            payments.addPayment(responsePojo);

            paymentNotificationResponsePojo.setPayments(payments);

            savePaymentNotificationRequest(payment, request, false, true, PaymentResponseStatusConstant.ACCEPTED,
                    responsePojo.getStatusMessage());
        }
        return paymentNotificationResponsePojo;
    }

    private boolean isExpectedAmount(Payment paymentPojo, PaymentTransaction paymentTransaction) {
        if (paymentTransaction.getAmountInKobo().equals(getAmountInKobo(paymentPojo.getAmount()))) {
            return true;
        }

        if (paymentPojo.getCustReference().startsWith("EDORPX") || paymentPojo.getCustReference().startsWith("AB") ||
                paymentPojo.getCustReference().startsWith("ECBS")) {
            return paymentPojo.getAmount().longValue() > 0;
        }
        return false;
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
        Merchant merchant = paymentTransactionDao.getRecordById(Merchant.class, paymentTransaction.getMerchant().getId());
        TransactionNotificationPojo transactionNotificationPojo = new TransactionNotificationPojo();
        transactionNotificationPojo.setStatus(paymentTransaction.getPaymentTransactionStatus().getValue());
        transactionNotificationPojo.setTransactionId(paymentTransaction.getTransactionId());
        transactionNotificationPojo.setDatePaymentReceived(PaymentUtil.format(Timestamp.from(Instant.now()), Constants.ISO_DATE_TIME_FORMAT));
        transactionNotificationPojo.setReceiptNumber(paymentPojo.getReceiptNo());
        transactionNotificationPojo.setAmountPaidInKobo(PaymentUtil.getAmountInKobo(paymentPojo.getAmount()));
        transactionNotificationPojo.setPaymentProvider(paymentTransaction.getPaymentProvider().getValue() + "_" + paymentTransaction.getPaymentChannel().getValue());
        transactionNotificationPojo.setPaymentProviderTransactionId(paymentTransaction.getProviderTransactionReference());
        transactionNotificationPojo.setPaymentDate(paymentPojo.getPaymentDate());
        transactionNotificationPojo.setSettlementDate(paymentPojo.getSettlementDate());
        transactionNotificationPojo.setPaymentChannelName(paymentPojo.getChannelName());
        transactionNotificationPojo.setPaymentProviderPaymentReference(paymentPojo.getPaymentReference());
        transactionNotificationPojo.setPaymentMethod(paymentPojo.getPaymentMethod());
//        transactionNotificationPojo.setNotificationId(notificationIdSequence.getNext());

        PayerPojo payerPojo = paymentTransactionDao.getPayerAsPojo(paymentTransaction.getPayer().getId());
        transactionNotificationPojo.setPayer(payerPojo);

        NotificationQueue notificationQueue = new NotificationQueue();
        notificationQueue.setMessageInJson(PaymentUtil.toJSON(transactionNotificationPojo));
        notificationQueue.setNotificationUrl(merchant.getNotificationUrl());
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
