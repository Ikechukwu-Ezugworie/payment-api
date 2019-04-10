package controllers;
import com.google.common.collect.Lists;

import com.bw.payment.entity.PaymentTransaction;
import com.bw.payment.enumeration.PaymentChannelConstant;
import com.bw.payment.enumeration.PaymentProviderConstant;
import com.bw.payment.enumeration.PaymentTransactionStatus;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import dao.CurrencyDao;
import dao.PaymentTransactionDao;
import extractors.IPAddress;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.ReverseRouter;
import ninja.params.Param;
import ninja.params.PathParam;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojo.ApiResponse;
import pojo.PayerPojo;
import pojo.PaymentTransactionFilterResponseDto;
import pojo.TransactionRequestPojo;
import pojo.flutterWave.*;
import retrofit2.Response;
import services.*;
import services.api.EndSystemApi;
import utils.PaymentUtil;

import javax.validation.ConstraintViolation;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Set;

/**
 * CREATED BY GIBAH
 */
@Singleton
public class FlutterWaveController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Inject
    private PaymentTransactionService paymentTransactionService;
    @Inject
    private NotificationService notificationService;
    @Inject
    private PaymentTransactionDao paymentTransactionDao;
    @Inject
    private CurrencyDao currencyDao;
    @Inject
    private FlutterWaveService flutterWaveService;
    @Inject
    private PaymentService paymentService;
    @Inject
    private TransactionTemplate transactionTemplate;
    @Inject
    private EndSystemApi endSystemApi;
    @Inject
    private ReverseRouter reverseRouter;


//    public Result requestPayment(@ContentExtract String payload, @IPAddress String ipAddress) {
//        logger.info("====> {} ", payload);
//        transactionTemplate.execute(entityManager -> {
//            RawDump rawDump=new RawDump();
//            rawDump.setRequest(payload);
//            rawDump.setDateCreated(Timestamp.from(Instant.now()));
//            rawDump.setPaymentProvider(PaymentProviderConstant.FLUTTERWAVE);
//            rawDump.setPaymentChannel(PaymentChannelConstant.CARD);
//            rawDump.setDescription("REQUEST_FROM_EXTERNAL_SYSTEM");
//            rawDump.setRequestIp(ipAddress);
//
//            entityManager.persist(rawDump);
//        });
//        FWEndsystemTransactionRequestDto data = new Gson().fromJson(payload, FWEndsystemTransactionRequestDto.class);
//        Set<ConstraintViolation<FWEndsystemTransactionRequestDto>> constraintViolations = javax.validation.Validation.buildDefaultValidatorFactory().getValidator().validate(data);
//
//        if (!constraintViolations.isEmpty()) {
//            for (ConstraintViolation<FWEndsystemTransactionRequestDto> constraintViolation : constraintViolations) {
//                ApiResponse apiResponse = new ApiResponse();
//                apiResponse.setCode(400);
//                apiResponse.setMessage(constraintViolation.getMessage());
//                return Results.ok().json().render(apiResponse);
//            }
//        }
//        TransactionRequestPojo transactionRequestPojo = new TransactionRequestPojo();
//        transactionRequestPojo.setAmountInKobo(data.getAmountInKobo());
//        transactionRequestPojo.setNotifyOnStatusChange(true);
//        transactionRequestPojo.setNotificationUrl(data.getRedirectUrl());
//        transactionRequestPojo.setPaymentProvider(PaymentProviderConstant.FLUTTERWAVE.getValue());
//        transactionRequestPojo.setPaymentChannel(PaymentChannelConstant.CARD.getValue());
////        transactionRequestPojo.setServiceTypeId(data.getProductId());
//        transactionRequestPojo.setCustomerTransactionReference(data.getTransactionReference());
//        PayerPojo payer = new PayerPojo();
//        payer.setFirstName(data.getCustomerFirstname());
//        payer.setLastName(data.getCustomerLastname());
//        payer.setEmail(data.getCustomerEmail());
//        transactionRequestPojo.setPayer(payer);
//
////        ItemPojo item = new ItemPojo();
////        item.setItemId(data.getPaymentItemId());
////        item.setName("WEBPAY ITEM");
////        item.setPriceInKobo(data.getAmount());
////        item.setTotalInKobo(data.getAmount());
////        item.setSubTotalInKobo(data.getAmount());
////        transactionRequestPojo.setItems(Arrays.asList(item));
////        transactionRequestPojo.setInstantTransaction(true);
//
//        PaymentTransaction paymentTransaction = paymentTransactionDao.createTransaction(transactionRequestPojo, null);
//
//        RawDump rawDump = new RawDump();
//        rawDump.setRequest(payload);
//        rawDump.setDateCreated(new Timestamp(new java.util.Date().getTime()));
//        rawDump.setPaymentProvider(PaymentProviderConstant.INTERSWITCH);
//        rawDump.setPaymentChannel(PaymentChannelConstant.WEBPAY);
//        rawDump.setDescription("PAYMENT REQUEST");
//        rawDump.setRequestIp(ipAddress);
//        rawDump.setPaymentTransaction(paymentTransaction);
//
//        transactionTemplate.execute((entityManager) -> {
//            entityManager.persist(rawDump);
//        });
//
//        ApiResponse<Map> apiResponse = new ApiResponse<>();
//        Map<String, String> res = new HashMap<>();
//        res.put("transactionId", paymentTransaction.getTransactionId());
//        apiResponse.setData(res);
//        apiResponse.setCode(200);
//        return Results.ok().json().render(apiResponse);
//    }


    public Result paymentPage(@IPAddress String ipAddress, Context context){
        try{
            FWEndsystemTransactionRequestDto data = new FWEndsystemTransactionRequestDto();
            data.setAccountCode(context.getParameter("accountCode"));
            data.setTransactionReference(context.getParameter("paymentRetrievalReference"));
            data.setHash(context.getParameter("hash"));
            data.setRedirectUrl(context.getParameter("redirectUrl"));
            data.setCustomerEmail(context.getParameter("customerEmail"));
            data.setCustomerPhone(context.getParameter("customerPhone"));

            Set<ConstraintViolation<FWEndsystemTransactionRequestDto>> constraintViolations = javax.validation.Validation.buildDefaultValidatorFactory().getValidator().validate(data);

            if (!constraintViolations.isEmpty()) {
                for (ConstraintViolation<FWEndsystemTransactionRequestDto> constraintViolation : constraintViolations) {
                    ApiResponse apiResponse = new ApiResponse();
                    apiResponse.setCode(400);
                    apiResponse.setMessage(constraintViolation.getMessage());
                    return Results.ok().json().render(apiResponse);
                }
            }

            String endSystemUrl = paymentService.getFlutterWaveServiceCredential(null).getBaseUrl();
            Response<TransactionRequestPojo> response = endSystemApi.validateFlutterWavePayment(endSystemUrl, data).execute();
            if(!response.isSuccessful()){
                if(response.code()==400)
                return Results.html()
                        .render("error", "Bad request");
            }

            TransactionRequestPojo transactionRequestPojo = response.body();
//            body.setValid(true);
//            body.setCurrencyCode("NGN");
//            body.setSplit(Lists.newArrayList());

            if (transactionRequestPojo == null) {
                return Results.html()
                        .render("error", "No response gotten from server");
            }
//        try {
//            PaymentTransaction pt = flutterWaveService.getPaymentTransactionByMerchantRef(data.getTransactionReference());
//            if (pt != null) {
//                return Results.html()
//                        .render("error", "Transaction reference not unique");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return Results.html()
//                    .render("error", "Transaction reference not unique");
//        }
//        if (!request.isSuccessful()) {
//            return Results.html()
//                    .render("error", "Unable to auth this request");
//        }

//            transactionRequestPojo = new TransactionRequestPojo();
            transactionRequestPojo.setAmountInKobo(PaymentUtil.getAmountInKobo(new BigDecimal(1000)));
            transactionRequestPojo.setNotifyOnStatusChange(true);
            transactionRequestPojo.setNotificationUrl("http://google.com");
            transactionRequestPojo.setPaymentProvider(PaymentProviderConstant.FLUTTERWAVE.getValue());
            transactionRequestPojo.setPaymentChannel(PaymentChannelConstant.CARD.getValue());
//        transactionRequestPojo.setServiceTypeId(data.getProductId());
            transactionRequestPojo.setCustomerTransactionReference(data.getTransactionReference());
            PayerPojo payer = new PayerPojo();
            payer.setFirstName("JOHN");
            payer.setLastName("DOE");
            payer.setEmail(data.getCustomerEmail());
            transactionRequestPojo.setPayer(payer);

//        ItemPojo item = new ItemPojo();
//        item.setItemId(data.getPaymentItemId());
//        item.setName("WEBPAY ITEM");
//        item.setPriceInKobo(data.getAmount());
//        item.setTotalInKobo(data.getAmount());
//        item.setSubTotalInKobo(data.getAmount());
//        transactionRequestPojo.setItems(Arrays.asList(item));
//        transactionRequestPojo.setInstantTransaction(true);

            PaymentTransaction paymentTransaction =paymentTransactionService.createTransaction(transactionRequestPojo, null);

            if (paymentTransaction == null) {
                return Results.ok().html().render("error", "Transaction not found");
            }
            TransactionRequestPojo fullPaymentTransactionDetailsAsPojo = paymentTransactionService.getFullPaymentTransactionDetailsAsPojo(paymentTransaction);
            if (paymentTransaction.getPaymentTransactionStatus().equals(PaymentTransactionStatus.SUCCESSFUL)) {
                return Results.ok().html().render("success", "Payment has already been made")
                        .render("transactionData", fullPaymentTransactionDetailsAsPojo);
            }

            FWPaymentRequestDto fwPaymentRequestDto = flutterWaveService.constructFormRequest(paymentTransaction, data, transactionRequestPojo.getSplit(), context);

//        logger.info("webPayTransactionRequestPojo is " + webPayTransactionRequestPojo);

            return Results.html()
                    .render("fwBaseUrl", flutterWaveService.getBaseUrl())
                    .render("data", fwPaymentRequestDto)
                    .render("transactionData", fullPaymentTransactionDetailsAsPojo);

        }catch (Exception e){
            e.printStackTrace();
            return Results.html()
                    .render("error", e.getMessage());
        }
    }

    public Result paymentCompleted(@PathParam("tRef") String transactionRef, @Param("redirectUrl") String rUrl, @IPAddress String ipAddress, Context context) {

        PaymentTransaction paymentTransaction = paymentTransactionService.getPaymentTransactionByTransactionId(transactionRef);

        FWPaymentVerificationRequestDto paymentVerificationRequestDto = new FWPaymentVerificationRequestDto();
        paymentVerificationRequestDto.setTransactionReference(transactionRef);
        paymentVerificationRequestDto.setSecretKey(paymentService.getFlutterWaveServiceCredential(null).getSecretKey());

        String prr = paymentTransaction.getMerchantTransactionReferenceId();
        try {
            FWTransactionResponseDto fwTransactionResponseDto = flutterWaveService.getPaymentData(paymentTransaction);
            paymentTransaction = flutterWaveService.processPaymentData(paymentTransaction, fwTransactionResponseDto, true);

            URIBuilder b = new URIBuilder(rUrl);
            b.addParameter("prr", prr);
            String redirectUrl = b.build().toString();
            return Results.redirect(redirectUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Results.redirect(rUrl + "?prr=" + prr);
    }

    public Result getTransaction(@Param("transactionReference") String transactionId) {
        ApiResponse<PaymentTransactionFilterResponseDto> apiResponse = new ApiResponse<>();
        if (StringUtils.isBlank(transactionId)) {
            apiResponse.setCode(400);
            apiResponse.setMessage("Transaction reference cannot be blank");
            return Results.badRequest().json().render(apiResponse);
        }
        PaymentTransaction paymentTransaction = flutterWaveService.getPaymentTransactionByMerchantRef(transactionId);
        if (paymentTransaction == null) {
            apiResponse.setCode(404);
            apiResponse.setMessage("Transaction not found");
            return Results.notFound().json().render(apiResponse);
        }
        try {
            FWTransactionResponseDto fwTransactionResponseDto = flutterWaveService.getPaymentData(paymentTransaction);
            paymentTransaction = flutterWaveService.processPaymentData(paymentTransaction, fwTransactionResponseDto, true);

            PaymentTransactionFilterResponseDto data = PaymentTransactionFilterResponseDto.from(paymentTransaction);
            data.setAdditionalData(null);
            apiResponse.setData(data);
            apiResponse.setCode(200);
            logger.info("===> Transaction Query response " + new Gson().toJson(apiResponse));
            return Results.json().render(apiResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Results.internalServerError().json();
    }

    public Result requeryTransaction(@Param("merchantTransactionRef") String transactionId) {
        logger.info("===> Checking transaction ID " + transactionId);
        ApiResponse<PaymentTransactionFilterResponseDto> apiResponse = new ApiResponse<>();
        if (StringUtils.isBlank(transactionId)) {
            apiResponse.setCode(400);
            apiResponse.setMessage("Transaction ID cannot be blank");
            return Results.badRequest().json().render(apiResponse);
        }
        PaymentTransaction paymentTransaction = flutterWaveService.getPaymentTransactionByMerchantRef(transactionId);
        if (paymentTransaction == null) {
            apiResponse.setCode(404);
            apiResponse.setMessage("Transaction not found");
            return Results.notFound().json().render(apiResponse);
        }
        try {
            FWTransactionResponseDto fwTransactionResponseDto = flutterWaveService.getPaymentData(paymentTransaction);
            paymentTransaction = flutterWaveService.processPaymentData(paymentTransaction, fwTransactionResponseDto, true);

            apiResponse.setData(PaymentTransactionFilterResponseDto.from(paymentTransaction));
            apiResponse.setCode(200);
            logger.info("===> Transaction Query response " + new Gson().toJson(apiResponse));
            return Results.json().render(apiResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Results.internalServerError().json();
    }

    public Result test(Context context) {

        return Results.html().render("url", reverseRouter.with(FlutterWaveController::paymentPage));
    }
}
