package services;

import com.bw.payment.entity.*;
import com.bw.payment.enumeration.PaymentChannelConstant;
import com.bw.payment.enumeration.PaymentProviderConstant;
import com.bw.payment.enumeration.PaymentResponseStatusConstant;
import com.bw.payment.enumeration.PaymentTransactionStatus;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import dao.MerchantDao;
import dao.PaymentTransactionDao;
import ninja.Context;
import ninja.utils.NinjaProperties;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojo.*;
import pojo.payDirect.customerValidation.EndSystemCustomerValidationResponse;
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
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;

import static utils.PaymentUtil.getAmountInKobo;

/**
 * CREATED BY GIBAH
 */
@Singleton
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
    private MerchantDao merchantDao;
    private PaymentTransactionService paymentTransactionService;
    private NotificationService notificationService;

    @Inject
    public PayDirectService(OkHttpClient client, PaymentTransactionDao paymentTransactionDao, NinjaProperties ninjaProperties,
                            NotificationIdSequence notificationIdSequence, MerchantDao merchantDao,
                            PaymentTransactionService paymentTransactionService, NotificationService notificationService) {
        this.paymentTransactionDao = paymentTransactionDao;
        this.ninjaProperties = ninjaProperties;
        this.notificationIdSequence = notificationIdSequence;
        this.client = PaymentUtil.getOkHttpClient(ninjaProperties);
        this.merchantDao = merchantDao;
        this.paymentTransactionService = paymentTransactionService;
        this.notificationService = notificationService;
    }

    public CustomerInformationResponse processCustomerValidationRequest(CustomerInformationRequest validationRequest, Context context) {
        CustomerInformationResponse customerInformationResponse = new CustomerInformationResponse();
        customerInformationResponse.setMerchantReference(validationRequest.getMerchantReference());

//        Merchant merchant = paymentTransactionDao.getUniqueRecordByProperty(Merchant.class, "paydirectMerchantReference", validationRequest.getMerchantReference());
        Merchant merchant = paymentTransactionDao.getMerchant(validationRequest.getMerchantReference());

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
            System.out.println("<== VALIDATING REQUEST:: :: " + PaymentUtil.toJSON(validationRequest));

            RequestBody body = RequestBody.create(JSON, PaymentUtil.toJSON(validationRequest));
            Request request = new Request.Builder().url(merchant.getLookupUrl()).post(body).build();
            Response response = client.newCall(request).execute();

            String s = null;
            try {
                s = response.body().string();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            logger.info("<== customer validation to url {} responded with code {} and body {}", merchant.getLookupUrl(), response.code(), s);

            if (response.isSuccessful()) {
                Type type = new TypeToken<ApiResponse<EndSystemCustomerValidationResponse>>() {
                }.getType();
                if (response.code() == 200) {
                    ApiResponse<EndSystemCustomerValidationResponse> r = PaymentUtil.fromJSON(s, type);

                    EndSystemCustomerValidationResponse tr = r.getData();

                    if (!tr.getPaymentStatus().equals(EndSystemCustomerValidationResponse.PaymentStatus.PAID)) {
                        Customer customer = new Customer();
                        customer.setFirstName(tr.getPayer().getFirstName());
                        customer.setAmount(PaymentUtil.getAmountInNaira(tr.getAmountInKobo()));
                        customer.setCustReference(validationRequest.getCustReference());
                        customer.setStatus(0);

                        Customers customers = new Customers();
                        customers.addCustomer(customer);

                        customerInformationResponse.setCustomers(customers);
                        return customerInformationResponse;
                    } else {
                        Customer customer = new Customer();
                        customer.setFirstName("");
                        customer.setCustReference(validationRequest.getCustReference());
                        customer.setStatus(CUSTOMER_INVALID);
                        customer.setStatusMessage("This reference has already been fully paid for.");
                        customer.setCustomerReferenceAlternate("");

                        Customers customers = new Customers();
                        customers.addCustomer(customer);

                        customerInformationResponse.setCustomers(customers);
                        return customerInformationResponse;
                    }

                }
            } else if (response.code() == 404) {
                Customer customer = new Customer();
                customer.setFirstName("");
                customer.setCustReference(validationRequest.getCustReference() == null ? "" : validationRequest.getCustReference());
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
    public synchronized PaymentNotificationResponse processPaymentNotification(PaymentNotificationRequest request, RawDump rawDump, Context context) {
        PaymentNotificationResponse paymentNotificationResponsePojo = new PaymentNotificationResponse();
        Payments payments = new Payments();
        paymentNotificationResponsePojo.setPayments(payments);

        if (request.getPayments() == null) {
            return null;
        }

        logger.info("<=== payments size = " + request.getPayments().getPayment().size());
        for (Payment payment : request.getPayments().getPayment()) {
//            if is duplicate notification
            if (paymentTransactionDao.isDuplicateNotificationAndAccepted(payment)) {
                logger.info("<=== duplicate notification");
                PaymentResponsePojo responsePojo = new PaymentResponsePojo();
                responsePojo.setPaymentLogId(payment.getPaymentLogId());
                responsePojo.setStatus(NOTIFICATION_RECEIVED);
                responsePojo.setStatusMessage("Duplicate notification");

                paymentNotificationResponsePojo.getPayments().addPayment(responsePojo);

                savePaymentNotificationRequest(payment, request, false, true, PaymentResponseStatusConstant.REJECTED,
                        responsePojo.getStatusMessage());
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

                String lName = PaymentUtil.getLastNameFromFullName(payment.getCustomerName());
                if (StringUtils.isBlank(payment.getCustomerName())) {
                    logger.info("<=== customer name is null");
                }
                logger.info("<==== customer name : " + payment.getCustomerName());
                payerPojo.setFirstName(payment.getCustomerName());
                payerPojo.setLastName("");
                payerPojo.setEmail("");
                payerPojo.setAddress(payment.getCustomerAddress());
                payerPojo.setPhoneNumber(payment.getCustomerPhoneNumber());

                transactionRequestPojo.setPayer(payerPojo);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (payment.getReversal()) {
                PaymentTransaction trReversal = paymentTransactionDao.getPaymentTransactionForReversal(payment);

                if (trReversal == null) {
                    PaymentResponsePojo responsePojo = new PaymentResponsePojo();
                    responsePojo.setPaymentLogId(payment.getPaymentLogId());
                    responsePojo.setStatus(NOTIFICATION_REJECTED);
                    responsePojo.setStatusMessage("Transaction to be reversed does not exist");

                    paymentNotificationResponsePojo.getPayments().addPayment(responsePojo);

                    savePaymentNotificationRequest(payment, request, false, true, PaymentResponseStatusConstant.REJECTED,
                            responsePojo.getStatusMessage());
                    break;
                }

                saveCurrentPaymentTransactionState(trReversal);

                trReversal.setPaymentTransactionStatus(PaymentTransactionStatus.CANCELED);

                queueNotification(payment, trReversal, rawDump == null ? null : rawDump.getRequest());

                paymentTransactionDao.updateObject(trReversal);

                logger.info("<=== Payment reversed");
                PaymentResponsePojo responsePojo = new PaymentResponsePojo();
                responsePojo.setPaymentLogId(payment.getPaymentLogId());
                responsePojo.setStatus(NOTIFICATION_RECEIVED);
                responsePojo.setStatusMessage("Transaction reversal successful");

                paymentNotificationResponsePojo.getPayments().addPayment(responsePojo);

                savePaymentNotificationRequest(payment, request, false, true, PaymentResponseStatusConstant.ACCEPTED,
                        responsePojo.getStatusMessage());

                break;
            }

            if (payment.getAmount().compareTo(new BigDecimal(0)) < 1) {
                PaymentResponsePojo responsePojo = new PaymentResponsePojo();
                responsePojo.setPaymentLogId(payment.getPaymentLogId());
                responsePojo.setStatus(NOTIFICATION_REJECTED);
                responsePojo.setStatusMessage("Invalid amount");

                paymentNotificationResponsePojo.getPayments().addPayment(responsePojo);

                savePaymentNotificationRequest(payment, request, false, true, PaymentResponseStatusConstant.REJECTED,
                        responsePojo.getStatusMessage());
                break;
            }

//            CustomerInformationRequest customerInformationRequest = new CustomerInformationRequest();
//            customerInformationRequest.setCustReference(payment.getCustReference());
//            customerInformationRequest.setAmount(payment.getAmount());
//
//            CustomerInformationResponse customerInformationResponse = processCustomerValidationRequest(customerInformationRequest, context);
//            if (customerInformationResponse == null || customerInformationResponse.getCustomers().getCustomers().get(0).getStatus() == CUSTOMER_INVALID) {
//                PaymentResponsePojo responsePojo = new PaymentResponsePojo();
//                responsePojo.setPaymentLogId(payment.getPaymentLogId());
//                responsePojo.setStatus(NOTIFICATION_REJECTED);
//                responsePojo.setStatusMessage("Invalid customer");
//
//                paymentNotificationResponsePojo.getPayments().addPayment(responsePojo);
//
//                savePaymentNotificationRequest(payment, request, false, true, PaymentResponseStatusConstant.REJECTED,
//                        responsePojo.getStatusMessage());
//                break;
//            }

            PaymentTransaction paymentTransaction = paymentTransactionDao.createTransaction(transactionRequestPojo, null, null);
            if (rawDump != null) {
                rawDump.setPaymentTransaction(paymentTransaction);
                paymentTransactionService.dump(rawDump);
            }

//           check if transaction exists
            if (paymentTransaction == null) {
                PaymentResponsePojo responsePojo = new PaymentResponsePojo();
                responsePojo.setPaymentLogId(payment.getPaymentLogId());
                responsePojo.setStatus(NOTIFICATION_REJECTED);
                responsePojo.setStatusMessage("Invalid transaction");

                paymentNotificationResponsePojo.getPayments().addPayment(responsePojo);

                savePaymentNotificationRequest(payment, request, false, true, PaymentResponseStatusConstant.REJECTED,
                        responsePojo.getStatusMessage());
                break;
            }

            saveCurrentPaymentTransactionState(paymentTransaction);

            paymentTransaction.setPaymentTransactionStatus(PaymentTransactionStatus.SUCCESSFUL);
            paymentTransaction.setCustomerTransactionReference(payment.getCustReference());
            paymentTransaction.setProviderTransactionReference(payment.getPaymentReference());
            paymentTransaction.setAmountPaidInKobo(PaymentUtil.getAmountInKobo(payment.getAmount()));
            paymentTransactionDao.updateObject(paymentTransaction);

            queueNotification(payment, paymentTransaction, rawDump == null ? null : rawDump.getRequest());

            PaymentResponsePojo responsePojo = new PaymentResponsePojo();
            responsePojo.setPaymentLogId(payment.getPaymentLogId());
            responsePojo.setStatus(NOTIFICATION_RECEIVED);

            paymentNotificationResponsePojo.getPayments().addPayment(responsePojo);

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
        paymentResponseLog.setPaymentTransaction(paymentTransactionDao.getPaymentTransactionByPaymentProviderReference(paymentPojo.getPaymentReference()));
        paymentResponseLog.setValidated(wasValidated);
        paymentResponseLog.setProcessed(wasProcessed);
        paymentResponseLog.setStatus(status);
        paymentResponseLog.setReason(reasonForOutcome);

        paymentTransactionDao.saveObject(paymentResponseLog);
        notificationService.sendPaymentNotification(20);
    }

    private void queueNotification(Payment paymentPojo, PaymentTransaction paymentTransaction, String paymentNotificationJson) {
        Merchant merchant = paymentTransactionDao.getRecordById(Merchant.class, paymentTransaction.getMerchant().getId());
        TransactionNotificationPojo<String> transactionNotificationPojo = new TransactionNotificationPojo<>();
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
        transactionNotificationPojo.setPaymentProviderPaymentReference(paymentTransaction.getProviderTransactionReference());
        transactionNotificationPojo.setPaymentMethod(paymentPojo.getPaymentMethod());
        transactionNotificationPojo.setDescription(paymentPojo.getPaymentItems().getPaymentItems().get(0).getCategoryName());
        transactionNotificationPojo.setNotificationId(notificationIdSequence.getNext());
        transactionNotificationPojo.setCustomerTransactionReference(paymentTransaction.getCustomerTransactionReference());
        transactionNotificationPojo.setMerchantTransactionReference(paymentTransaction.getMerchantTransactionReferenceId());
        transactionNotificationPojo.setActualNotification(XML.toJSONObject(paymentNotificationJson).toString());

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

    public String getDefaultMerchantReference() {
        return merchantDao.getFirstMerchantReference().orElse("6405");
    }
}
