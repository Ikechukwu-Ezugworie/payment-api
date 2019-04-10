package services;

import com.bw.payment.entity.*;
import com.bw.payment.enumeration.GenericStatusConstant;
import com.bw.payment.enumeration.PaymentProviderConstant;
import com.bw.payment.enumeration.PaymentTransactionStatus;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import controllers.WebPayController;
import dao.PaymentTransactionDao;
import ninja.Context;
import ninja.ReverseRouter;
import ninja.jpa.UnitOfWork;
import ninja.utils.NinjaProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojo.PayerPojo;
import pojo.TransactionNotificationPojo;
import pojo.flutterWave.*;
import pojo.webPay.WebPayPaymentDataDto;
import pojo.webPay.WebPayTransactionRequestPojo;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import services.api.FlutterWaveApi;
import services.api.WebPayApi;
import services.sequence.NotificationIdSequence;
import utils.Constants;
import utils.PaymentUtil;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;

/*
 * Created by Gibah Joseph on Feb, 2019
 */
public class FlutterWaveService {
    private static String WEBPAY_PAYMENT_REQUEST_URL = "WEBPAY_PAYMENT_REQUEST_URL";

    private Logger logger = LoggerFactory.getLogger(FlutterWaveService.class);

    private PaymentTransactionDao paymentTransactionDao;
    private WebPayApi webPayApi;
    private Merchant merchant;
    private PaymentService paymentService;
    private NotificationIdSequence notificationIdSequence;
    private ReverseRouter reverseRouter;
    private NotificationService notificationService;
    private NinjaProperties ninjaProperties;
    private TransactionTemplate transactionTemplate;

    @Inject
    public FlutterWaveService(PaymentTransactionDao paymentTransactionDao, WebPayApi webPayApi,
                              PaymentService paymentService, NotificationIdSequence notificationIdSequence, ReverseRouter reverseRouter, NotificationService notificationService, NinjaProperties ninjaProperties) {
        this.paymentTransactionDao = paymentTransactionDao;
        this.webPayApi = webPayApi;
        this.paymentService = paymentService;
        this.merchant = paymentService.getMerchant();
        this.notificationIdSequence = notificationIdSequence;
        this.reverseRouter = reverseRouter;
        this.notificationService = notificationService;
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
    public void queueNotification(FWTransactionResponseDto paymentPojo, PaymentTransaction paymentTransaction) {
        Merchant merchant = paymentTransactionDao.getRecordById(Merchant.class, paymentTransaction.getMerchant().getId());
        FWTransactionDto fwTransactionDto = paymentPojo.getData().getFWTransactionDto();
        TransactionNotificationPojo<FWTransactionResponseDto> transactionNotificationPojo = new TransactionNotificationPojo<>();
        transactionNotificationPojo.setStatus(paymentTransaction.getPaymentTransactionStatus().getValue());
        transactionNotificationPojo.setTransactionId(paymentTransaction.getTransactionId());
        transactionNotificationPojo.setDatePaymentReceived(PaymentUtil.format(Timestamp.from(Instant.now()), Constants.ISO_DATE_TIME_FORMAT));
        transactionNotificationPojo.setReceiptNumber(fwTransactionDto.getOrderRef());
        transactionNotificationPojo.setAmountPaidInKobo(fwTransactionDto.getAmount());
        transactionNotificationPojo.setPaymentProvider(paymentTransaction.getPaymentProvider().getValue() + "_" + paymentTransaction.getPaymentChannel().getValue());
        transactionNotificationPojo.setPaymentProviderTransactionId(paymentTransaction.getProviderTransactionReference());
        transactionNotificationPojo.setPaymentDate(fwTransactionDto.getCreatedAt());
//        transactionNotificationPojo.setSettlementDate(paymentPojo.getSettlementDate());
        transactionNotificationPojo.setPaymentChannelName(paymentTransaction.getPaymentChannel().getValue());
        transactionNotificationPojo.setPaymentProviderPaymentReference(fwTransactionDto.getFlwRef());
        transactionNotificationPojo.setPaymentMethod("CARD");
        transactionNotificationPojo.setDescription("");
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

    public FWTransactionResponseDto getPaymentData(PaymentTransaction paymentTransaction) {
        FWPaymentVerificationRequestDto paymentVerificationRequestDto = new FWPaymentVerificationRequestDto();
        paymentVerificationRequestDto.setTransactionReference(paymentTransaction.getTransactionId());
        paymentVerificationRequestDto.setSecretKey(paymentService.getFlutterWaveServiceCredential(null).getSecretKey());
        Call<FWTransactionResponseDto> transactionStatus = getApiCaller().getTransactionStatus(paymentVerificationRequestDto);
        logger.info("===> Verifying payment from FW ::: {} ::: {}", transactionStatus.request().url(), paymentTransaction.getCustomerTransactionReference());
        try {
            Response<FWTransactionResponseDto> response = transactionStatus.execute();
            if (response.code() == 200) {
                return response.body();
            }
            throw new IllegalArgumentException(response.code() + " : " + response.message());
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e);
        }
    }

    @Transactional
    public PaymentTransaction processPaymentData(PaymentTransaction paymentTransaction, FWTransactionResponseDto fwTransactionResponseDto) {
        return processPaymentData(paymentTransaction, fwTransactionResponseDto, true);
    }

    @Transactional
    public PaymentTransaction processPaymentData(PaymentTransaction paymentTransaction, FWTransactionResponseDto fwTransactionResponseDto, boolean notify) {
        if (fwTransactionResponseDto.getStatus().equalsIgnoreCase("successful")) {
            paymentTransaction.setAmountPaidInKobo(fwTransactionResponseDto.getData().getFWTransactionDto().getAmount());
            if(paymentTransaction.getAmountPaidInKobo()>=paymentTransaction.getAmountInKobo()){
                paymentTransaction.setPaymentTransactionStatus(PaymentTransactionStatus.SUCCESSFUL);
            }else{
                paymentTransaction.setPaymentTransactionStatus(PaymentTransactionStatus.PARTIAL);
            }
        } else {
            paymentTransaction.setPaymentTransactionStatus(PaymentTransactionStatus.FAILED);
        }
        paymentTransaction.setProviderTransactionReference(fwTransactionResponseDto.getData().getFWTransactionDto().getFlwRef());
        paymentTransactionDao.updateObject(paymentTransaction);
        if (notify) {
            queueNotification(fwTransactionResponseDto, paymentTransaction);
            notificationService.sendPaymentNotification(10);
        }
        return paymentTransaction;
    }

    public String getBaseUrl() {
        return paymentService.getFlutterWaveServiceCredential(null).getBaseUrl();
    }

    public FlutterWaveApi getApiCaller() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .client(PaymentUtil.getOkHttpClient(ninjaProperties))
                .build();
        return retrofit.create(FlutterWaveApi.class);

    }

    public FWPaymentRequestDto constructFormRequest(PaymentTransaction paymentTransaction, FWEndsystemTransactionRequestDto request, List<SplitDto> split, Context context) {
        FlutterWaveServiceCredentials flutterWaveServiceCredential = paymentService.getFlutterWaveServiceCredential(null);
        FWPaymentRequestDto fwPaymentRequestDto = new FWPaymentRequestDto();
        fwPaymentRequestDto.setPubKey(flutterWaveServiceCredential.getApiKey());
        fwPaymentRequestDto.setIntegrityHash("");
        fwPaymentRequestDto.setTransactionReference(paymentTransaction.getTransactionId());
//        fwPaymentRequestDto.setPaymentOptions("");
//        fwPaymentRequestDto.setPaymentPlan("");
//        fwPaymentRequestDto.setSubAccounts("");
        fwPaymentRequestDto.setAmount(paymentTransaction.getAmountInKobo());
//        fwPaymentRequestDto.setCurrency("");
//        fwPaymentRequestDto.setCountry("");
        fwPaymentRequestDto.setCustomerEmail(request.getCustomerEmail());
        fwPaymentRequestDto.setCustomerPhone(request.getCustomerPhone());
        fwPaymentRequestDto.setCustomerFirstname(request.getCustomerFirstname());
        fwPaymentRequestDto.setCustomerLastname(request.getCustomerLastname());
        fwPaymentRequestDto.setPayButtonText("PROCEED");
        fwPaymentRequestDto.setCustomTitle("BWPAY");
        fwPaymentRequestDto.setCustomDescription("YPAY WIRH");
        fwPaymentRequestDto.setRedirectUrl("");
//        fwPaymentRequestDto.setCustomLogo("");
        HashMap<String, Object> meta = Maps.newHashMap();
        meta.put("accountId", request.getAccountCode());
        fwPaymentRequestDto.setMeta(meta);


        return fwPaymentRequestDto;
    }

    public PaymentTransaction getPaymentTransactionByMerchantRef(String mRef) {
//        QPaymentTransaction paymentTransaction = QPaymentTransaction.paymentTransaction;
//        JPAQuery<PaymentTransaction> paymentTransactionJPAQuery = paymentTransactionDao.startJPAQuery(paymentTransaction);
//        List<PaymentTransaction> paymentTransactions = paymentTransactionJPAQuery.where(paymentTransaction.merchantTransactionReferenceId.equalsIgnoreCase(mRef))
//                .where(paymentTransaction.paymentProvider.eq(PaymentProviderConstant.FLUTTERWAVE))
//                .fetch();
//        if (paymentTransactions.isEmpty()) {
//            return null;
//        }
//        if (paymentTransactions.size() > 1) {
//            throw new IllegalArgumentException("More than one result");
//        }
//        return paymentTransactions.get(0);
        return null;
    }
}
