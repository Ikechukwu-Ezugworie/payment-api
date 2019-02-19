package services;

import com.bw.payment.entity.*;
import com.bw.payment.enumeration.GenericStatusConstant;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import controllers.WebPayController;
import dao.PaymentTransactionDao;
import ninja.utils.NinjaProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ninja.Context;
import ninja.ReverseRouter;
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

    private Logger logger= LoggerFactory.getLogger(WebPayService.class);

    private PaymentTransactionDao paymentTransactionDao;
    private WebPayApi webPayApi;
    private Merchant merchant;
    private PaymentService paymentService;
    private NotificationIdSequence notificationIdSequence;
    private ReverseRouter reverseRouter;
    private NinjaProperties ninjaProperties;

    @Inject
    public WebPayService(PaymentTransactionDao paymentTransactionDao, WebPayApi webPayApi,
                         PaymentService paymentService, NotificationIdSequence notificationIdSequence, ReverseRouter reverseRouter, NinjaProperties ninjaProperties) {
        this.paymentTransactionDao = paymentTransactionDao;
        this.webPayApi = webPayApi;
        this.paymentService = paymentService;
        merchant = paymentService.getMerchant();
        this.notificationIdSequence = notificationIdSequence;
        this.reverseRouter = reverseRouter;
        this.ninjaProperties = ninjaProperties;
    }

    public WebPayTransactionRequestPojo createWebPayRequest(PaymentTransaction paymentTransaction, Context context) {
        WebPayTransactionRequestPojo webPayTransactionRequestPojo = new WebPayTransactionRequestPojo();
        webPayTransactionRequestPojo.setAmount(paymentTransaction.getAmountInKobo());
        webPayTransactionRequestPojo.setCustomerId(paymentTransaction.getCustomerTransactionReference());
        webPayTransactionRequestPojo.setTransactionReference(paymentTransaction.getTransactionId());
        for (Item paymentTransactionItem : paymentTransactionDao.getPaymentTransactionItems(paymentTransaction.getId(), GenericStatusConstant.ACTIVE)) {
            webPayTransactionRequestPojo.setPaymentItemId(Integer.valueOf(paymentTransactionItem.getItemId()));
        }
        webPayTransactionRequestPojo.setProductId(Integer.valueOf(paymentTransaction.getServiceTypeId()));
        webPayTransactionRequestPojo.setSiteRedirectUrl(reverseRouter.with(WebPayController::paymentCompleted).absolute(context).build());
        if (paymentTransaction.getPayer() != null) {
            Payer payer = paymentTransactionDao.getRecordById(Payer.class, paymentTransaction.getPayer().getId());
            webPayTransactionRequestPojo.setCustomerName(PaymentUtil.getFormattedFullName(payer.getFirstName(), payer.getLastName()));
        }

        String mac = paymentService.getWebPayCredentials(merchant).getMacKey();

        logger.info("mac key is " + mac);
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
        String mac = paymentService.getWebPayCredentials(merchant).getMacKey();
        String message = paymentTransaction.getServiceTypeId() +paymentTransaction.getTransactionId() +  mac;
        String hash = PaymentUtil.getHash(message, Constants.SHA_512_ALGORITHM_NAME);
        logger.info("===> hash {} for message {}",hash,message);
        retrofit2.Call<WebPayPaymentDataDto> transactionStatus = webPayApi.getTransactionStatus(paymentTransaction.getServiceTypeId(),
                paymentTransaction.getAmountInKobo(), paymentTransaction.getTransactionId(), hash);
        logger.info("===> Verifying payment from ISW ::: {} ::: {}",transactionStatus.request().url(),paymentTransaction.getCustomerTransactionReference());
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

//    public PaymentTransaction processPaymentResponse(WebPayTransactionResponsePojo data) {
//        PaymentTransaction paymentTransaction=paymentTransactionDao.getByTransactionId(data.getTxnref());
//        paymentTransaction.setProviderTransactionReference(data.getPayRef());
//    }
}
