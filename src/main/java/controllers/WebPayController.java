package controllers;

import com.bw.payment.entity.PaymentTransaction;
import com.bw.payment.entity.RawDump;
import com.bw.payment.entity.WebPayServiceCredentials;
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
import ninja.utils.NinjaProperties;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojo.*;
import pojo.webPay.BwPaymentsWebPayRequest;
import pojo.webPay.WebPayPaymentDataDto;
import pojo.webPay.WebPayTransactionRequestPojo;
import pojo.webPay.WebPayTransactionResponsePojo;
import services.*;

import javax.validation.ConstraintViolation;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * CREATED BY GIBAH
 */
@Singleton
public class WebPayController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Inject
    private PaymentTransactionService paymentTransactionService;
    @Inject
    private NotificationService notificationService;
    @Inject
    private PaymentTransactionDao paymentTransactionDao;
    @Inject
    private WebPayService webPayService;
    @Inject
    private PaymentService paymentService;
    @Inject
    private TransactionTemplate transactionTemplate;
    @Inject
    private NinjaProperties ninjaProperties;

    public Result doCreateTransaction(@ContentExtract String payload, @IPAddress String ipAddress) {
        logger.info("====> {} ", payload);
        BwPaymentsWebPayRequest data = new Gson().fromJson(payload, BwPaymentsWebPayRequest.class);
        Set<ConstraintViolation<BwPaymentsWebPayRequest>> constraintViolations = javax.validation
                .Validation.buildDefaultValidatorFactory().getValidator().validate(data);

        if (!constraintViolations.isEmpty()) {
            for (ConstraintViolation<BwPaymentsWebPayRequest> constraintViolation : constraintViolations) {
                ApiResponse apiResponse = new ApiResponse();
                apiResponse.setCode(400);
                apiResponse.setMessage(constraintViolation.getMessage());
                return Results.ok().json().render(apiResponse);
            }
        }
        TransactionRequestPojo transactionRequestPojo = new TransactionRequestPojo();
        transactionRequestPojo.setAmountInKobo(data.getAmount());
        transactionRequestPojo.setNotifyOnStatusChange(true);
        transactionRequestPojo.setNotificationUrl(data.getNotificationUrl());
        transactionRequestPojo.setPaymentProvider("INTERSWITCH");
        transactionRequestPojo.setPaymentChannel(PaymentChannelConstant.WEBPAY.getValue());
        transactionRequestPojo.setServiceTypeId(data.getProductId());
        transactionRequestPojo.setCustomerTransactionReference(data.getCustomerReference());
        PayerPojo payer = new PayerPojo();
        payer.setFirstName(data.getPayerName());
        payer.setEmail(data.getPayerEmail());
        transactionRequestPojo.setPayer(payer);

        ItemPojo item = new ItemPojo();
        item.setItemId(data.getPaymentItemId());
        item.setName("WEBPAY ITEM");
        item.setPriceInKobo(data.getAmount());
        item.setTotalInKobo(data.getAmount());
        item.setSubTotalInKobo(data.getAmount());
        transactionRequestPojo.setItems(Arrays.asList(item));
        transactionRequestPojo.setInstantTransaction(true);

        PaymentTransaction paymentTransaction = paymentTransactionService.createTransaction(transactionRequestPojo, null);

        RawDump rawDump = new RawDump();
        rawDump.setRequest(payload);
        rawDump.setDateCreated(new Timestamp(new java.util.Date().getTime()));
        rawDump.setPaymentProvider(PaymentProviderConstant.INTERSWITCH);
        rawDump.setPaymentChannel(PaymentChannelConstant.WEBPAY);
        rawDump.setDescription("PAYMENT REQUEST");
        rawDump.setRequestIp(ipAddress);
        rawDump.setPaymentTransaction(paymentTransaction);

        transactionTemplate.execute((entityManager) -> {
            entityManager.persist(rawDump);
        });

        ApiResponse<Map> apiResponse = new ApiResponse<>();
        Map<String, String> res = new HashMap<>();
        res.put("transactionId", paymentTransaction.getTransactionId());
        apiResponse.setData(res);
        apiResponse.setCode(200);

        return Results.ok().json().render(apiResponse);
    }


    public Result paymentPage(@Param("transactionId") String transactionId, Context context) {
        if (StringUtils.isBlank(transactionId)) {
            return Results.ok().html().render("error", "Invalid transactionId");
        }

        PaymentTransaction paymentTransaction = paymentTransactionService.getPaymentTransactionByTransactionId(transactionId);

        if (paymentTransaction == null) {
            return Results.ok().html().render("error", "Transaction not found");
        }
        TransactionRequestPojo fullPaymentTransactionDetailsAsPojo = paymentTransactionService.getFullPaymentTransactionDetailsAsPojo(paymentTransaction);
        if (paymentTransaction.getPaymentTransactionStatus().equals(PaymentTransactionStatus.SUCCESSFUL)) {
            return Results.ok().html().render("success", "Payment has already been made")
                    .render("transactionData", fullPaymentTransactionDetailsAsPojo);
        }

        WebPayTransactionRequestPojo webPayTransactionRequestPojo = webPayService.createWebPayRequest(paymentTransaction, context);

        logger.info("webPayTransactionRequestPojo is " + webPayTransactionRequestPojo);

        String webpaypath=webPayService.getBaseUrl()+ninjaProperties.getWithDefault("webpay.payment.path", "/collections/w/pay");
        return Results.html()
                .render("webpayPaymentUrl", webpaypath)
                .render("data", webPayTransactionRequestPojo)
                .render("transactionData", fullPaymentTransactionDetailsAsPojo);
    }

    public Result paymentCompleted(WebPayTransactionResponsePojo data, @IPAddress String ipAddress, Context context) {

        logger.info("Web pay trans response pojo ===> {}", new Gson().toJson(data));
        PaymentTransaction paymentTransaction = paymentTransactionService.getPaymentTransactionByTransactionId(data.getTxnref());
        RawDump rawDump = transactionTemplate.execute(entityManager -> {
            return paymentTransactionDao.getUniqueRecordByProperty(RawDump.class, "paymentTransaction", paymentTransaction);
        });

        try {

            WebPayPaymentDataDto webPayPaymentDataDto = webPayService.getPaymentData(paymentTransaction);
            if (rawDump != null) {
                rawDump.setResponse(new Gson().toJson(webPayPaymentDataDto));
                rawDump.setRequestIp(ipAddress);
                transactionTemplate.execute(entityManager -> {
                    entityManager.merge(rawDump);
                });
            }
            URIBuilder b = new URIBuilder(paymentService.getProviderCredentials(WebPayServiceCredentials.class, null)
                    .getMerchantRedirectUrl());
            webPayService.processPaymentData(paymentTransaction, webPayPaymentDataDto);
            if (paymentTransaction.getPaymentTransactionStatus().equals(PaymentTransactionStatus.SUCCESSFUL)) {
                b.addParameter("status", "successful");
            } else {
                b.addParameter("status", "failed");
                b.addParameter("description", String.format("%s", webPayPaymentDataDto.getResponseDescription()));
            }
            b.addParameter("customerReference", paymentTransaction.getCustomerTransactionReference());
            logger.info("=== > response: {}", webPayPaymentDataDto);

            String redirectUrl = b.build().toString();
            return Results.redirect(redirectUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Results.internalServerError().json();
    }

    public Result requeryTransaction(@Param("transactionId") String transactionId) {
        logger.info("===> Checking transaction ID " + transactionId);
        ApiResponse<PaymentTransactionFilterResponseDto> apiResponse = new ApiResponse<>();
        if (StringUtils.isBlank(transactionId)) {
            apiResponse.setCode(400);
            apiResponse.setMessage("Transaction ID cannot be blank");
            return Results.badRequest().json().render(apiResponse);
        }
        PaymentTransaction paymentTransaction = paymentTransactionService.getPaymentTransactionByTransactionId(transactionId);
        if (paymentTransaction == null || !paymentTransaction.getPaymentChannel().equals(PaymentChannelConstant.WEBPAY)) {
            apiResponse.setCode(404);
            apiResponse.setMessage("Payment transaction not found");
            return Results.notFound().json().render(apiResponse);
        }
        try {
            WebPayPaymentDataDto webPayPaymentDataDto = webPayService.getPaymentData(paymentTransaction);
            paymentTransaction = webPayService.processPaymentData(paymentTransaction, webPayPaymentDataDto, false);

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
