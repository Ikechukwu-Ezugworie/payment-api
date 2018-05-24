package services;

import com.bw.payment.entity.*;
import com.bw.payment.enumeration.PaymentChannelConstant;
import com.bw.payment.enumeration.PaymentResponseStatusConstant;
import com.bw.payment.enumeration.PaymentTransactionStatus;
import com.bw.payment.service.PaymentService;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import controllers.QuickTellerController;
import dao.PaymentTransactionDao;
import ninja.ReverseRouter;
import ninja.utils.NinjaProperties;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojo.PayerPojo;
import pojo.Ticket;
import pojo.TransactionNotificationPojo;
import pojo.quickTeller.QTTransactionQueryResponse;
import pojo.quickTeller.QuickTellerTicket;
import services.sequence.NotificationIdSequence;
import utils.Constants;
import utils.PaymentUtil;

import java.sql.Timestamp;
import java.time.Instant;

/**
 * CREATED BY GIBAH
 */
public class QuickTellerService {
    public static final int CUSTOMER_VALID = 0;
    public static final int CUSTOMER_INVALID = 1;
    public static final int CUSTOMER_EXPIRED = 2;

    public static final int NOTIFICATION_RECEIVED = 0;
    public static final int NOTIFICATION_REJECTED = 1;

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final String TRANSACTION_APPROVED = "00";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final OkHttpClient client;
    private PaymentTransactionDao paymentTransactionDao;
    private NinjaProperties ninjaProperties;
    private ReverseRouter reverseRouter;
    private String SECRET_KEY;
    private String CLIENT_ID;
    private String QUICKTELLER_GET_TRANSACTION_BASEURL;

    @Inject
    private PaymentTransactionService paymentTransactionService;
    @Inject
    private NotificationIdSequence notificationIdSequence;

    @Inject
    public QuickTellerService(OkHttpClient client, PaymentTransactionDao paymentTransactionDao, NinjaProperties ninjaProperties, ReverseRouter reverseRouter) {
        this.paymentTransactionDao = paymentTransactionDao;
        this.ninjaProperties = ninjaProperties;
        this.reverseRouter = reverseRouter;
        this.client = PaymentUtil.getOkHttpClient(ninjaProperties);
        this.SECRET_KEY = paymentTransactionDao.getSettingsValue(Constants.QUICK_TELLER_SECRET_KEY, "E9300DJLXKJLQJ2993N1190023", true);
        this.CLIENT_ID = paymentTransactionDao.getSettingsValue(Constants.SETTING_QUICKTELLER_CLIENTID, "localhost", true);
        this.QUICKTELLER_GET_TRANSACTION_BASEURL = paymentTransactionDao.getSettingsValue(Constants.QUICKTELLER_GET_TRANSACTION_BASEURL, "https://pwq.sandbox.interswitchng.com/api/v2/transaction/",
                true);
    }

    private void queueNotification(QTTransactionQueryResponse res, PaymentTransaction paymentTransaction) {
        paymentTransactionDao.saveObject(generateNotification(res, paymentTransaction));
    }

    private NotificationQueue generateNotification(QTTransactionQueryResponse res, PaymentTransaction paymentTransaction) {
        Merchant merchant = paymentTransactionDao.getRecordById(Merchant.class, paymentTransaction.getMerchant().getId());
        System.out.println("<=== "+merchant.getName()+" : "+merchant.getNotificationUrl());

        TransactionNotificationPojo transactionNotificationPojo = new TransactionNotificationPojo();
        transactionNotificationPojo.setStatus(paymentTransaction.getPaymentTransactionStatus().getValue());
        transactionNotificationPojo.setDescription(res.getResponseDescription());
        transactionNotificationPojo.setTransactionId(paymentTransaction.getTransactionId());
        transactionNotificationPojo.setDatePaymentReceived(PaymentUtil.format(Timestamp.from(Instant.now()), Constants.ISO_DATE_TIME_FORMAT));
        transactionNotificationPojo.setReceiptNumber(res.getPaymentReference());
        transactionNotificationPojo.setAmountPaidInKobo(paymentTransaction.getAmountPaidInKobo());
        transactionNotificationPojo.setPaymentProvider(paymentTransaction.getPaymentProvider().getValue());
        transactionNotificationPojo.setPaymentProviderTransactionId(paymentTransaction.getProviderTransactionReference());
        transactionNotificationPojo.setPaymentDate(res.getTransactionDate());
        transactionNotificationPojo.setPaymentChannelName(PaymentChannelConstant.QUICKTELLER.getValue());
        transactionNotificationPojo.setPaymentProviderPaymentReference(res.getPaymentReference());
        transactionNotificationPojo.setNotificationId(notificationIdSequence.getNext());
        System.out.println("<=== XXXXXXXXXX"+ paymentTransaction.getCustomerTransactionReference());
        transactionNotificationPojo.setCustomerTransactionReference(paymentTransaction.getCustomerTransactionReference());
        transactionNotificationPojo.setMerchantTransactionReference(paymentTransaction.getMerchantTransactionReferenceId());

        PayerPojo payerPojo=new PayerPojo();
        payerPojo.setFirstName(paymentTransaction.getPayer().getFirstName());
        payerPojo.setLastName(paymentTransaction.getPayer().getLastName());
        payerPojo.setEmail(paymentTransaction.getPayer().getEmail());
        payerPojo.setPhoneNumber(paymentTransaction.getPayer().getPhoneNumber());
//        payerPojo.setAddress(paymentTransaction.getPayer().getetAPhoneNumber());
        transactionNotificationPojo.setPayer(payerPojo);

        NotificationQueue notificationQueue = new NotificationQueue();
        notificationQueue.setMessageInJson(PaymentUtil.toJSON(transactionNotificationPojo));
        notificationQueue.setNotificationUrl(merchant.getNotificationUrl());
        notificationQueue.setNotificationSent(false);
        notificationQueue.setDateCreated(Timestamp.from(Instant.now()));
        notificationQueue.setPaymentTransaction(paymentTransaction);

        return notificationQueue;
    }

    private void saveCurrentPaymentTransactionState(PaymentTransaction paymentTransaction) {
        PaymentTransactionStateLog paymentTransactionStateLog = new PaymentTransactionStateLog();

        paymentTransactionStateLog.setStateDump(PaymentUtil.toJSONWithAdaptor(paymentTransaction));
        paymentTransactionStateLog.setDateCreated(Timestamp.from(Instant.now()));
        paymentTransactionStateLog.setPaymentTransaction(paymentTransaction);

        paymentTransactionDao.saveObject(paymentTransactionStateLog);
    }

    public static void main(String[] args) {
        QuickTellerTicket quickTellerTicket = new QuickTellerTicket();
        quickTellerTicket.setPaymentCode("12");
        quickTellerTicket.setCustomerId("12");
        quickTellerTicket.setPayerPhone("12");
        quickTellerTicket.setResponseUrl("12");
        quickTellerTicket.setRequestReference("12");
        quickTellerTicket.setPayerEmail("em");
        quickTellerTicket.setAmountInKobo(111L);
        quickTellerTicket.setResponseUrl("th");
        quickTellerTicket.setPayerName("name");
        quickTellerTicket.setPayerPhone("phone");

        System.out.println(new Gson().toJson(quickTellerTicket));

        Ticket ticket = quickTellerTicket;

        System.out.println(new Gson().toJson(ticket));

    }

    @Transactional
    public void updateTransactionStatus(String reference) {
        String hash = PaymentUtil.generateHashValue(reference + SECRET_KEY, "SHA-512");
        String url = QUICKTELLER_GET_TRANSACTION_BASEURL + reference + "?isRequestRef=true";
        try {
            Request request = new Request.Builder().url(url).get()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("clientid", CLIENT_ID)
                    .header("Hash", hash)
                    .build();
            Response response = client.newCall(request).execute();
            System.out.println("<=== qt response code "+ response.code());
            if (response.isSuccessful() && response.code() == 200) {
                String body = response.body().string();
//                logger.info(body);
                QTTransactionQueryResponse transactionQueryResponse = PaymentUtil.fromJSON(body, QTTransactionQueryResponse.class);
                if (transactionQueryResponse == null) {
                    return;
                }
                if (transactionQueryResponse.getResponseCode().equalsIgnoreCase(TRANSACTION_APPROVED)) {
                    PaymentTransaction paymentTransaction = paymentTransactionDao.getUniqueRecordByProperty(PaymentTransaction.class,
                            "providerTransactionReference", reference);
                    logStatusCheckResponse(transactionQueryResponse, paymentTransaction);
                    if (paymentTransaction == null) {
                        return;
                    }

                    if (paymentTransaction.getPaymentTransactionStatus().equals(PaymentTransactionStatus.SUCCESSFUL)) {
                        return;
                    }

                    if (!paymentTransaction.getAmountInKobo().equals(transactionQueryResponse.getAmount())) {
                        return;
                    }

                    saveCurrentPaymentTransactionState(paymentTransaction);

                    paymentTransaction.setAmountPaidInKobo(transactionQueryResponse.getAmount());
                    paymentTransaction.setPaymentTransactionStatus(PaymentTransactionStatus.SUCCESSFUL);
                    paymentTransaction.setLastUpdated(Timestamp.from(Instant.now()));

                    paymentTransactionDao.updateObject(paymentTransaction);
                    generateNotification(transactionQueryResponse, paymentTransaction);
                    paymentTransactionService.doNotification(generateNotification(transactionQueryResponse, paymentTransaction));
                }
            }else{
                System.out.println("<=== qt response");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Ticket generateTicket(PaymentTransaction paymentTransaction, Merchant merchant) {
        String qtPaymentCode = paymentTransactionDao.getSettingsValue(Constants.QUICK_TELLER_MERCHANT_PAYMENT_CODE, "95101", true);
        String interswitchPrefix = paymentTransactionDao.getSettingsValue(Constants.QUICK_TELLER_INTERSWITCH_PREFIX, "9999", true);
        String baseUrl = paymentTransactionDao.getSettingsValue(Constants.BASE_URL, "http://localhost:8880", true);
        String notificationUrl = reverseRouter.with(QuickTellerController::doQuickTellerNotification)
                .build();

        QuickTellerTicket quickTellerTicket = new QuickTellerTicket();
        quickTellerTicket.setPaymentCode(qtPaymentCode);
        quickTellerTicket.setTransactionId(paymentTransaction.getTransactionId());
        quickTellerTicket.setAmountInKobo(paymentTransaction.getAmountInKobo());
        quickTellerTicket.setCustomerId(paymentTransaction.getTransactionId());

        Payer payer = paymentTransactionDao.getRecordById(Payer.class, paymentTransaction.getPayer().getId());
        quickTellerTicket.setPayerPhone(payer.getPhoneNumber());
        quickTellerTicket.setResponseUrl(baseUrl + notificationUrl);
        quickTellerTicket.setRequestReference(generateTicketId(interswitchPrefix));
        quickTellerTicket.setPayerEmail(payer.getEmail());

        paymentTransaction.setProviderTransactionReference(quickTellerTicket.getRequestReference());

        paymentTransactionDao.updateObject(paymentTransaction);

        return quickTellerTicket;

    }

    private String generateTicketId(String interswitchPrefix) {
        String ticketId = paymentTransactionDao.getTicketId();
        return interswitchPrefix + ticketId;
    }

    private void logStatusCheckResponse(QTTransactionQueryResponse transactionQueryResponse, PaymentTransaction paymentTransaction) {
        PaymentResponseLog paymentResponseLog = new PaymentResponseLog();
        paymentResponseLog.setPaymentReference(transactionQueryResponse.getPaymentReference());
        paymentResponseLog.setAmountInKobo(transactionQueryResponse.getAmount());
        paymentResponseLog.setResponseDump(PaymentUtil.toJSON(transactionQueryResponse));
        paymentResponseLog.setDateCreated(Timestamp.from(Instant.now()));
        paymentResponseLog.setPaymentTransaction(paymentTransaction);
        paymentResponseLog.setValidated(false);
        paymentResponseLog.setProcessed(true);
        paymentResponseLog.setStatus(PaymentResponseStatusConstant.ACCEPTED);
        paymentResponseLog.setReason("Success payment");

        paymentTransactionDao.saveObject(paymentResponseLog);

    }
}
