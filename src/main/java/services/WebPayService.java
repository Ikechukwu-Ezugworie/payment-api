package services;

import com.bw.payment.entity.*;
import com.bw.payment.enumeration.GenericStatusConstant;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import dao.PaymentTransactionDao;
import pojo.PayerPojo;
import pojo.TransactionNotificationPojo;
import pojo.webPay.WebPayPaymentDataDto;
import pojo.webPay.WebPayTransactionRequestPojo;
import retrofit2.Response;
import services.api.WebPayApi;
import services.sequence.NotificationIdSequence;
import utils.Constants;
import utils.PaymentUtil;

import java.sql.Timestamp;
import java.time.Instant;

/*
 * Created by Gibah Joseph on Feb, 2019
 */
public class WebPayService {
    private static String WEBPAY_PAYMENT_REQUEST_URL = "WEBPAY_PAYMENT_REQUEST_URL";
    private PaymentTransactionDao paymentTransactionDao;
    private WebPayApi webPayApi;
    private WebPayServiceCredentials webPayServiceCredentials;
    private Merchant merchant;
    private PaymentService paymentService;
    private NotificationIdSequence notificationIdSequence;

    @Inject
    public WebPayService(PaymentTransactionDao paymentTransactionDao, WebPayApi webPayApi,
                         PaymentService paymentService, NotificationIdSequence notificationIdSequence) {
        this.paymentTransactionDao = paymentTransactionDao;
        this.webPayApi = webPayApi;
        this.paymentService = paymentService;
        merchant = paymentService.getMerchant();
        webPayServiceCredentials = paymentService.getWebPayCredentials(merchant);
        this.notificationIdSequence = notificationIdSequence;
    }

    public WebPayTransactionRequestPojo createWebPayRequest(PaymentTransaction paymentTransaction) {
        WebPayTransactionRequestPojo webPayTransactionRequestPojo = new WebPayTransactionRequestPojo();
        webPayTransactionRequestPojo.setAmount(paymentTransaction.getAmountInKobo());
        webPayTransactionRequestPojo.setCustomerId(paymentTransaction.getCustomerTransactionReference());
        webPayTransactionRequestPojo.setTransactionReference(paymentTransaction.getTransactionId());
        for (Item paymentTransactionItem : paymentTransactionDao.getPaymentTransactionItems(paymentTransaction.getId(), GenericStatusConstant.ACTIVE)) {
            webPayTransactionRequestPojo.setPaymentItemId(Integer.valueOf(paymentTransactionItem.getItemId()));
        }
        webPayTransactionRequestPojo.setProductId(Integer.valueOf(paymentTransaction.getServiceTypeId()));
        webPayTransactionRequestPojo.setSiteRedirectUrl(paymentTransactionDao.getSettingsValue(Constants.WEB_PAY_REDIRECT_URL_SETTINGS_KEY, "http://localhost:8080/payments/webpay/", true));
//        webPayTransactionRequestPojo.setCustomerIdDescription("");
        if (paymentTransaction.getPayer() != null) {
            Payer payer = paymentTransactionDao.getRecordById(Payer.class, paymentTransaction.getPayer().getId());
            webPayTransactionRequestPojo.setCustomerName(PaymentUtil.getFormattedFullName(payer.getFirstName(), payer.getLastName()));
        }

        String mac = webPayServiceCredentials.getMacKey();
        webPayTransactionRequestPojo.computeHash(mac);

        return webPayTransactionRequestPojo;

    }

    @Transactional
    public void queueNotification(WebPayPaymentDataDto paymentPojo, PaymentTransaction paymentTransaction) {
        Merchant merchant = paymentTransactionDao.getRecordById(Merchant.class, paymentTransaction.getMerchant().getId());
        TransactionNotificationPojo<WebPayPaymentDataDto> transactionNotificationPojo = new TransactionNotificationPojo<>();
        transactionNotificationPojo.setStatus(paymentTransaction.getPaymentTransactionStatus().getValue());
        transactionNotificationPojo.setTransactionId(paymentTransaction.getTransactionId());
        transactionNotificationPojo.setDatePaymentReceived(PaymentUtil.format(Timestamp.from(Instant.now()), Constants.ISO_DATE_TIME_FORMAT));
        transactionNotificationPojo.setReceiptNumber(paymentPojo.getRetrievalReferenceNumber());
        transactionNotificationPojo.setAmountPaidInKobo(paymentPojo.getAmount());
        transactionNotificationPojo.setPaymentProvider(paymentTransaction.getPaymentProvider().getValue() + "_" + paymentTransaction.getPaymentChannel().getValue());
        transactionNotificationPojo.setPaymentProviderTransactionId(paymentTransaction.getProviderTransactionReference());
        transactionNotificationPojo.setPaymentDate(paymentPojo.getTransactionDate());
//        transactionNotificationPojo.setSettlementDate(paymentPojo.getSettlementDate());
        transactionNotificationPojo.setPaymentChannelName(paymentTransaction.getPaymentChannel().getValue());
        transactionNotificationPojo.setPaymentProviderPaymentReference(paymentPojo.getPaymentReference());
        transactionNotificationPojo.setPaymentMethod("CARD");
        transactionNotificationPojo.setDescription(paymentPojo.getResponseDescription());
        transactionNotificationPojo.setNotificationId(notificationIdSequence.getNext());
        transactionNotificationPojo.setCustomerTransactionReference(paymentTransaction.getCustomerTransactionReference());
        transactionNotificationPojo.setMerchantTransactionReference(paymentTransaction.getMerchantTransactionReferenceId());
        transactionNotificationPojo.setActualNotification(paymentPojo);

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

    public WebPayPaymentDataDto getPaymentData(PaymentTransaction paymentTransaction) {
        String mac = webPayServiceCredentials.getMacKey();
        String message = paymentTransaction.getServiceTypeId() + paymentTransaction.getAmountInKobo() + mac;
        retrofit2.Call<WebPayPaymentDataDto> transactionStatus = webPayApi.getTransactionStatus(paymentTransaction.getServiceTypeId(),
                paymentTransaction.getAmountInKobo(), paymentTransaction.getTransactionId(), PaymentUtil.getHash(message, Constants.SHA_512_ALGORITHM_NAME));
        try {
            Response<WebPayPaymentDataDto> response = transactionStatus.execute();
            if (response.code() == 200) {
                return response.body();
            }
            throw new IllegalArgumentException(response.code() + " : " + response.message());
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e);
        }
    }
}
