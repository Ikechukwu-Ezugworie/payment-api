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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

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

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Inject
    PaymentTransactionDao paymentTransactionDao;

    @Transactional
    public PaymentNotificationResponse processPaymentNotification(PaymentNotificationRequest request, Context context) {
        PaymentNotificationResponse paymentNotificationResponsePojo = new PaymentNotificationResponse();
//

        PaymentResponsePojo responsePojo = new PaymentResponsePojo();
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

//            if its a reversal request
            if (paymentPojo.getReversal()) {
//                check if its the right amount
                if (paymentTransaction.getAmountInKobo().equals(getAmountInKobo(paymentPojo.getAmount().abs()))) {


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
                } else {

                    responsePojo.setPaymentLogId(paymentPojo.getPaymentLogId());
                    responsePojo.setStatus(NOTIFICATION_REJECTED);
                    responsePojo.setStatusMessage(String.format("Invalid amount. Expected N%s", PaymentUtil.koboToNaira(paymentTransaction.getAmountInKobo())));
                    Payments payments = new Payments();
                    payments.addPayment(responsePojo);

                    paymentNotificationResponsePojo.setPayments(payments);

                    savePaymentNotificationRequest(paymentPojo, request, false, true, PaymentResponseStatusConstant.REJECTED,
                            responsePojo.getStatusMessage());
                }

                break;
            }

            if (paymentTransaction.getAmountInKobo().equals(getAmountInKobo(paymentPojo.getAmount()))) {
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

                break;
            } else {
                responsePojo.setStatusMessage(String.format("Invalid amount. Expected N%s", PaymentUtil.koboToNaira(paymentTransaction.getAmountInKobo())));
            }

            responsePojo.setPaymentLogId(paymentPojo.getPaymentLogId());
            responsePojo.setStatus(NOTIFICATION_REJECTED);

            Payments payments = new Payments();
            payments.addPayment(responsePojo);

            paymentNotificationResponsePojo.setPayments(payments);

            savePaymentNotificationRequest(paymentPojo, request, false, true, PaymentResponseStatusConstant.REJECTED,
                    responsePojo.getStatusMessage());
        }
        return paymentNotificationResponsePojo;
    }


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
            paymentItem.setTax(PaymentUtil.getAmountInNaira(item.getTaxInKobo()));
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
