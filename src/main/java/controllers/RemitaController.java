package controllers;

import com.bw.payment.entity.PaymentTransaction;
import com.bw.payment.entity.RawDump;
import com.bw.payment.enumeration.PaymentChannelConstant;
import com.bw.payment.enumeration.PaymentProviderConstant;
import com.bw.payment.enumeration.PaymentTransactionStatus;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import dao.MerchantDao;
import dao.PaymentTransactionDao;
import exceptions.ApiResponseException;
import extractors.ContentExtract;
import extractors.IPAddress;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.i18n.Messages;
import ninja.params.Param;
import ninja.validation.JSR303Validation;
import ninja.validation.Validation;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojo.ApiResponse;
import pojo.ItemPojo;
import pojo.PayerPojo;
import pojo.TransactionRequestPojo;
import pojo.remitta.RemittaCreateTransactionResponse;
import pojo.remitta.RemittaNotification;
import pojo.remitta.RemittaPaymentRequestPojo;
import pojo.webPay.BwPaymentsWebPayRequest;
import pojo.webPay.WebPayPaymentDataDto;
import pojo.webPay.WebPayTransactionRequestPojo;
import pojo.webPay.WebPayTransactionResponsePojo;
import services.PayDirectService;
import services.PaymentTransactionService;
import services.RemittaService;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

/**
 * CREATED BY GIBAH
 */
@Singleton
public class RemitaController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Inject
    private Messages messages;
    @Inject
    private MerchantDao merchantDao;
    @Inject
    private PaymentTransactionDao paymentTransactionDao;
    @Inject
    private PaymentTransactionService paymentTransactionService;
    @Inject
    private PayDirectService payDirectService;
    @Inject
    private RemittaService remittaService;


    public Result doCreateTransaction(@JSR303Validation TransactionRequestPojo data, Validation validation) {
        ApiResponse<RemittaCreateTransactionResponse> apiResponse = new ApiResponse();
        if (validation.hasViolations()) {
            apiResponse.setMessage(validation.getViolations().get(0).getDefaultMessage());
            apiResponse.setCode(HttpStatus.SC_BAD_REQUEST);
            return Results.ok().json().render(apiResponse);
        }
//        TransactionRequestPojo transactionRequestPojo = new TransactionRequestPojo();
//        transactionRequestPojo.setAmountInKobo(data.getAmount());
//        transactionRequestPojo.setNotifyOnStatusChange(true);
        data.setPaymentProvider("REMITTA");
        data.setPaymentChannel(PaymentChannelConstant.BANK.getValue()); // TODO Update after model update
//        transactionRequestPojo.setServiceTypeId(data.getProductId());
//        transactionRequestPojo.setCustomerTransactionReference(data.getCustomerReference());
//        PayerPojo payer = new PayerPojo();
//        payer.setFirstName(data.getPayerName());
//        payer.setEmail(data.getPayerEmail());
//        transactionRequestPojo.setPayer(payer);
//
//        ItemPojo item = new ItemPojo();
//        item.setItemId(data.getPaymentItemId());
//        item.setName("REMITTA ITEM");
//        item.setPriceInKobo(data.getAmount());
//        item.setTotalInKobo(data.getAmount());
//        item.setSubTotalInKobo(data.getAmount());
//        transactionRequestPojo.setItems(Arrays.asList(item));
//        transactionRequestPojo.setInstantTransaction(true);

        try {
            PaymentTransaction paymentTransaction = remittaService.generateRemittaRRR(data);
            apiResponse.setMessage("Created a payment Transaction Successfully");
            apiResponse.setData(new RemittaCreateTransactionResponse().setTransactionId(paymentTransaction.getTransactionId()));
            apiResponse.setCode(HttpStatus.SC_OK);
            return Results.ok().json().render(apiResponse);


        } catch (Exception ex) {

            if (ex instanceof ApiResponseException) {
                apiResponse.setMessage(ex.getMessage());
                apiResponse.setCode(HttpStatus.SC_FORBIDDEN);
                return Results.ok().json().render(apiResponse);
            }
            apiResponse.setMessage(ex.getMessage());

        }

        apiResponse.setMessage("There was an internal error");
        apiResponse.setCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        return Results.ok().json().render(apiResponse);
    }

    public Result paymentPage(@Param("transactionId") String transactionId, Context context) {
        if (StringUtils.isBlank(transactionId)) {
            return Results.ok().html().render("error", "Invalid transactionId");
        }

        PaymentTransaction paymentTransaction = paymentTransactionService.getPaymentTransactionByTransactionId(transactionId);

        if (paymentTransaction == null || paymentTransaction.getPaymentTransactionStatus().equals(PaymentTransactionStatus.SUCCESSFUL)) {
            return Results.ok().html().render("error", "Transaction not found");
        }

        RemittaPaymentRequestPojo paymentRequestPojo = remittaService.createRemittaPaymentRequestPojo(paymentTransaction, context);

        return Results.html()
                .render("data", paymentRequestPojo)
                .render("transactionData", paymentTransactionService.getFullPaymentTransactionDetailsAsPojo(paymentTransaction));
    }


    public Result paymentCompleted(@ContentExtract  String data) {
        System.out.println("!!!!!!!! response from data" + data);

        try {
//            PaymentTransaction paymentTransaction = paymentTransactionService.getPaymentTransactionByTransactionId(data.getTxnref());
//
//            WebPayPaymentDataDto webPayPaymentDataDto = webPayService.getPaymentData(paymentTransaction);
//
//            URIBuilder b = new URIBuilder(paymentService.getWebPayCredentials(null).getMerchantRedirectUrl());
//            if (webPayPaymentDataDto.getResponseCode().equalsIgnoreCase("00")) {
//                paymentTransaction.setPaymentTransactionStatus(PaymentTransactionStatus.SUCCESSFUL);
//                paymentTransaction.setProviderTransactionReference(webPayPaymentDataDto.getPaymentReference());
//                paymentTransactionDao.updateObject(paymentTransaction);
//                webPayService.queueNotification(webPayPaymentDataDto, paymentTransaction);
//                notificationService.sendPaymentNotification(10);
//                b.addParameter("status", "successful");
//            } else {
//                b.addParameter("status", "failed");
//                b.addParameter("description", String.format("%s : %s", webPayPaymentDataDto.getResponseCode(), webPayPaymentDataDto.getResponseDescription()));
//            }
//            b.addParameter("customerReference", paymentTransaction.getCustomerTransactionReference());
//            logger.info("=== > response: {}", webPayPaymentDataDto);
//
//            String redirectUrl = b.build().toString();
//            return Results.redirect(redirectUrl);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return Results.internalServerError();
            return null;
        }
    }

    public Result doRemittaNotification(@ContentExtract String payload, @IPAddress String ipAddress) {
        RawDump rawDump = new RawDump();
        rawDump.setRequest(payload);
        rawDump.setDateCreated(Timestamp.from(Instant.now()));
        rawDump.setPaymentProvider(PaymentProviderConstant.REMITA);
        rawDump.setPaymentChannel(PaymentChannelConstant.BANK);
        rawDump.setRequestIp(ipAddress);
        paymentTransactionService.dump(rawDump);


        List<RemittaNotification> remittaNotifications = new Gson().fromJson(payload, new TypeToken<List<RemittaNotification>>() {
        }.getType());
        remittaService.processPaymentNotification(remittaNotifications);

        return Results.json();

    }
}
