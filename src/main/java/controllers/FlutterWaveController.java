package controllers;

import com.bw.payment.entity.PaymentTransaction;
import com.bw.payment.entity.RawDump;
import com.bw.payment.enumeration.PaymentChannelConstant;
import com.bw.payment.enumeration.PaymentProviderConstant;
import com.bw.payment.enumeration.PaymentTransactionStatus;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import dao.PaymentTransactionDao;
import extractors.ContentExtract;
import extractors.IPAddress;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojo.ApiResponse;
import pojo.PayerPojo;
import pojo.PaymentTransactionFilterResponseDto;
import pojo.TransactionRequestPojo;
import pojo.flutterWave.FWEndsystemTransactionRequestDto;
import pojo.flutterWave.FWPaymentRequestDto;
import pojo.flutterWave.FWTransactionResponseDto;
import pojo.flutterWave.FlutterWaveValidationResponseDto;
import retrofit2.Response;
import services.*;
import services.api.EndSystemApi;

import javax.validation.ConstraintViolation;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
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
    private FlutterWaveService flutterWaveService;
    @Inject
    private PaymentService paymentService;
    @Inject
    private TransactionTemplate transactionTemplate;
    @Inject
    private EndSystemApi endSystemApi;


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


    public Result paymentPage(@ContentExtract String payload, @IPAddress String ipAddress, Context context) throws IOException {
        logger.info("====> {} ", payload);
        transactionTemplate.execute(entityManager -> {
            RawDump rawDump = new RawDump();
            rawDump.setRequest(payload);
            rawDump.setDateCreated(Timestamp.from(Instant.now()));
            rawDump.setPaymentProvider(PaymentProviderConstant.FLUTTERWAVE);
            rawDump.setPaymentChannel(PaymentChannelConstant.CARD);
            rawDump.setDescription("REQUEST_FROM_EXTERNAL_SYSTEM");
            rawDump.setRequestIp(ipAddress);

            entityManager.persist(rawDump);
        });
        FWEndsystemTransactionRequestDto data = new Gson().fromJson(payload, FWEndsystemTransactionRequestDto.class);
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
        Response<FlutterWaveValidationResponseDto> request = endSystemApi.validateFlutterWavePayment(endSystemUrl, data).execute();
        if (!request.isSuccessful()) {
            return Results.html()
                    .render("error", "Unable to auth this request");
        }

        FlutterWaveValidationResponseDto body = request.body();
        if (body == null) {
            return Results.html()
                    .render("error", "No response gotten from server");
        }

        if (!body.isValid()) {
            return Results.html()
                    .render("error", "Invalid request");
        }

        TransactionRequestPojo transactionRequestPojo = new TransactionRequestPojo();
        transactionRequestPojo.setAmountInKobo(data.getAmountInKobo());
        transactionRequestPojo.setNotifyOnStatusChange(true);
        transactionRequestPojo.setNotificationUrl(data.getRedirectUrl());
        transactionRequestPojo.setPaymentProvider(PaymentProviderConstant.FLUTTERWAVE.getValue());
        transactionRequestPojo.setPaymentChannel(PaymentChannelConstant.CARD.getValue());
//        transactionRequestPojo.setServiceTypeId(data.getProductId());
        transactionRequestPojo.setCustomerTransactionReference(data.getTransactionReference());
        PayerPojo payer = new PayerPojo();
        payer.setFirstName(data.getCustomerFirstname());
        payer.setLastName(data.getCustomerLastname());
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

        PaymentTransaction paymentTransaction = paymentTransactionDao.createTransaction(transactionRequestPojo, null);

        if (paymentTransaction == null) {
            return Results.ok().html().render("error", "Transaction not found");
        }
        TransactionRequestPojo fullPaymentTransactionDetailsAsPojo = paymentTransactionService.getFullPaymentTransactionDetailsAsPojo(paymentTransaction);
        if (paymentTransaction.getPaymentTransactionStatus().equals(PaymentTransactionStatus.SUCCESSFUL)) {
            return Results.ok().html().render("success", "Payment has already been made")
                    .render("transactionData", fullPaymentTransactionDetailsAsPojo);
        }

        FWPaymentRequestDto fwPaymentRequestDto = flutterWaveService.constructFormRequest(paymentTransaction, data, body.getSplit(), context);

//        logger.info("webPayTransactionRequestPojo is " + webPayTransactionRequestPojo);

        return Results.html()
                .render("webpayBaseUrl", flutterWaveService.getBaseUrl())
                .render("data", fwPaymentRequestDto)
                .render("transactionData", fullPaymentTransactionDetailsAsPojo);
    }

//    public Result paymentCompleted(@PathParam("trid") String transactionRef, @IPAddress String ipAddress, Context context){
//
//        PaymentTransaction paymentTransaction = paymentTransactionService.getPaymentTransactionByMerchantRef(transactionRef);
//
//        FWPaymentVerificationRequestDto paymentVerificationRequestDto = new FWPaymentVerificationRequestDto();
//        paymentVerificationRequestDto.setTransactionReference(transactionRef);
//        paymentVerificationRequestDto.setSecretKey(paymentService.getFlutterWaveServiceCredential(null).getSecretKey());
//
//        try {
//
//            Response<FWTransactionResponseDto> response = flutterWaveService.getApiCaller().getTransactionStatus(paymentVerificationRequestDto).execute();
//
//            URIBuilder b = new URIBuilder(paymentService.getWebPayCredentials(null).getMerchantRedirectUrl());
//            if(!response.isSuccessful()){
//                b.addParameter("status","pending")
//                        .addParameter("transactionId",paymentTransaction.getMerchantTransactionReferenceId());
//                String redirectUrl = b.build().toString();
//                return Results.redirect(redirectUrl);
//            }
//            FWTransactionResponseDto body = response.body();
//            if(body==null){
//                b.addParameter("status","pending")
//                        .addParameter("transactionId",paymentTransaction.getMerchantTransactionReferenceId());
//                String redirectUrl = b.build().toString();
//                return Results.redirect(redirectUrl);
//            }
//            if(body.get)
//            RawDump rawDump = transactionTemplate.execute(entityManager -> {
//                return paymentTransactionDao.getUniqueRecordByProperty(RawDump.class, "paymentTransaction", paymentTransaction);
//            });
//
//            WebPayPaymentDataDto webPayPaymentDataDto = webPayService.getPaymentData(paymentTransaction);
//            if (rawDump != null) {
//                rawDump.setResponse(new Gson().toJson(webPayPaymentDataDto));
//                rawDump.setRequestIp(ipAddress);
//                transactionTemplate.execute(entityManager -> {
//                    entityManager.merge(rawDump);
//                });
//            }
//            webPayService.processPaymentData(paymentTransaction, webPayPaymentDataDto);
//            if (paymentTransaction.getPaymentTransactionStatus().equals(PaymentTransactionStatus.SUCCESSFUL)) {
//                b.addParameter("status", "successful");
//            } else {
//                b.addParameter("status", "failed");
//                b.addParameter("description", String.format("%s", webPayPaymentDataDto.getResponseDescription()));
//            }
//            b.addParameter("customerReference", paymentTransaction.getCustomerTransactionReference());
//            logger.info("=== > response: {}", webPayPaymentDataDto);
//
//            String redirectUrl = b.build().toString();
//            return Results.redirect(redirectUrl);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return Results.internalServerError().json();
//    }

    public Result getTransaction(@Param("merchantTransactionRef") String transactionId) {
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
}
