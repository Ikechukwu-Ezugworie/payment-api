package controllers;

import com.bw.payment.entity.PaymentTransaction;
import com.bw.payment.enumeration.PaymentChannelConstant;
import com.bw.payment.enumeration.PaymentTransactionStatus;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import dao.PaymentTransactionDao;
import extractors.ContentExtract;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;
import ninja.validation.JSR303Validation;
import ninja.validation.Validation;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojo.ApiResponse;
import pojo.ItemPojo;
import pojo.PayerPojo;
import pojo.TransactionRequestPojo;
import pojo.webPay.BwPaymentsWebPayRequest;
import pojo.webPay.WebPayPaymentDataDto;
import pojo.webPay.WebPayTransactionRequestPojo;
import pojo.webPay.WebPayTransactionResponsePojo;
import retrofit2.http.Url;
import services.NotificationService;
import services.PaymentService;
import services.PaymentTransactionService;
import services.WebPayService;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
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


    public Result doCreateTransaction(@ContentExtract String payload, Validation validation) {
        logger.info("====> {}", payload);
        BwPaymentsWebPayRequest data = new Gson().fromJson(payload, BwPaymentsWebPayRequest.class);
        Set<ConstraintViolation<BwPaymentsWebPayRequest>> constraintViolations = javax.validation.Validation.buildDefaultValidatorFactory().getValidator().validate(data);
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

        PaymentTransaction paymentTransaction = paymentTransactionDao.createTransaction(transactionRequestPojo, null);

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

        return Results.html()
                .render("data", webPayTransactionRequestPojo)
                .render("transactionData", fullPaymentTransactionDetailsAsPojo);
    }

    public Result paymentCompleted(WebPayTransactionResponsePojo data) {
        try {
            PaymentTransaction paymentTransaction = paymentTransactionService.getPaymentTransactionByTransactionId(data.getTxnref());

            WebPayPaymentDataDto webPayPaymentDataDto = webPayService.getPaymentData(paymentTransaction);

            URIBuilder b = new URIBuilder(paymentService.getWebPayCredentials(null).getMerchantRedirectUrl());
            if (webPayPaymentDataDto.getResponseCode().equalsIgnoreCase("00")) {
                webPayService.processPaymentData(paymentTransaction, webPayPaymentDataDto);
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
        return Results.internalServerError();
    }
}
