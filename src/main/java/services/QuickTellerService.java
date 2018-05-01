package services;

import com.bw.payment.entity.*;
import com.bw.payment.enumeration.PaymentChannelConstant;
import com.bw.payment.enumeration.PaymentResponseStatusConstant;
import com.bw.payment.enumeration.PaymentTransactionStatus;
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
import pojo.Ticket;
import pojo.TransactionNotificationPojo;
import pojo.quickTeller.QTTransactionQueryResponse;
import pojo.quickTeller.QuickTellerTicket;
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

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final OkHttpClient client;
    private PaymentTransactionDao paymentTransactionDao;
    private NinjaProperties ninjaProperties;
    private ReverseRouter reverseRouter;
    private String SECRET_KEY;
    private String CLIENT_ID;
    private String QUICKTELLER_GET_TRANSACTION_BASEURL;

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
        TransactionNotificationPojo transactionNotificationPojo = new TransactionNotificationPojo();
        transactionNotificationPojo.setStatus(paymentTransaction.getPaymentTransactionStatus().getValue());
        transactionNotificationPojo.setTransactionId(paymentTransaction.getTransactionId());
        transactionNotificationPojo.setDatePaymentReceived(PaymentUtil.format(Timestamp.from(Instant.now()), Constants.ISO_DATE_TIME_FORMAT));
        transactionNotificationPojo.setReceiptNumber(res.getPaymentReference());
        transactionNotificationPojo.setAmountPaidInKobo(paymentTransaction.getAmountInKobo());
        transactionNotificationPojo.setPaymentProvider(paymentTransaction.getPaymentProvider().getValue());
        transactionNotificationPojo.setPaymentProviderTransactionId(paymentTransaction.getProviderTransactionReference());
        transactionNotificationPojo.setPaymentDate(res.getTransactionDate());
        transactionNotificationPojo.setPaymentChannelName(PaymentChannelConstant.QUICKTELLER.getValue());
        transactionNotificationPojo.setPaymentProviderPaymentReference(res.getPaymentReference());

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

            if (response.isSuccessful() && response.code() == 200) {
                String body = response.body().string();
                logger.info(body);
                QTTransactionQueryResponse transactionQueryResponse = PaymentUtil.fromJSON(body, QTTransactionQueryResponse.class);
                if (transactionQueryResponse == null) {
                    return;
                }
                if (transactionQueryResponse.getResponseCode().equalsIgnoreCase("00")) {
                    PaymentTransaction paymentTransaction = paymentTransactionDao.getUniqueRecordByProperty(PaymentTransaction.class,
                            "transactionId", reference.substring(4, reference.length()));
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

                    paymentTransaction.setPaymentTransactionStatus(PaymentTransactionStatus.SUCCESSFUL);
                    paymentTransaction.setLastUpdated(Timestamp.from(Instant.now()));

                    paymentTransactionDao.updateObject(paymentTransaction);

                    if (paymentTransaction.getNotifyOnStatusChange()) {
                        queueNotification(transactionQueryResponse, paymentTransaction);
                    }
                }
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
        quickTellerTicket.setAmountInKobo(paymentTransaction.getAmountInKobo());
        quickTellerTicket.setCustomerId(paymentTransaction.getTransactionId());

        Payer payer = paymentTransactionDao.getRecordById(Payer.class, paymentTransaction.getPayer().getId());
        quickTellerTicket.setPayerPhone(payer.getPhoneNumber());
        quickTellerTicket.setResponseUrl(baseUrl + notificationUrl);
        quickTellerTicket.setRequestReference(interswitchPrefix + paymentTransaction.getTransactionId());
        quickTellerTicket.setPayerEmail(payer.getEmail());

        return quickTellerTicket;

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
