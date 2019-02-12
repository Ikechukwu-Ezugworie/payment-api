package controllers;

import com.bw.payment.entity.PaymentTransaction;
import com.bw.payment.enumeration.PaymentTransactionStatus;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import dao.PaymentTransactionDao;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;
import ninja.validation.JSR303Validation;
import ninja.validation.Validation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojo.ItemPojo;
import pojo.PayerPojo;
import pojo.TransactionRequestPojo;
import pojo.webPay.BwPaymentsWebPayRequest;
import pojo.webPay.WebPayPaymentDataDto;
import pojo.webPay.WebPayTransactionRequestPojo;
import pojo.webPay.WebPayTransactionResponsePojo;
import services.PaymentTransactionService;
import services.QuickTellerService;
import services.WebPayService;

import java.util.Arrays;

/**
 * CREATED BY GIBAH
 */
@Singleton
public class WebPayController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Inject
    private QuickTellerService quickTellerService;
    @Inject
    private PaymentTransactionService paymentTransactionService;
    @Inject
    private PaymentTransactionDao paymentTransactionDao;
    @Inject
    private WebPayService webPayService;


    public Result doCreateTransaction(@JSR303Validation BwPaymentsWebPayRequest data, Validation validation) {
        if (validation.hasViolations()) {
            return Results.badRequest().json().render("message", validation.getViolations().get(0).getDefaultMessage());
        }
        TransactionRequestPojo transactionRequestPojo = new TransactionRequestPojo();
        transactionRequestPojo.setAmountInKobo(data.getAmount());
        transactionRequestPojo.setNotifyOnStatusChange(true);
        transactionRequestPojo.setNotificationUrl(data.getNotificationUrl());
        transactionRequestPojo.setPaymentProvider("INTERSWITCH");
        transactionRequestPojo.setPaymentChannel("WEBPAY");
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

        return Results.ok().json().render("transactionId", paymentTransaction.getTransactionId());
    }


    public Result paymentPage(@Param("transactionId") String transactionId) {
        if (StringUtils.isBlank(transactionId)) {
            return Results.badRequest().json().render("message", "Invalid transactionId");
        }

        PaymentTransaction paymentTransaction = paymentTransactionService.getPaymentTransactionByTransactionId(transactionId);

        if (paymentTransaction == null || paymentTransaction.getPaymentTransactionStatus().equals(PaymentTransactionStatus.SUCCESSFUL)) {
            return Results.notFound().json().render("message", "Transaction not found");
        }

        WebPayTransactionRequestPojo webPayTransactionRequestPojo = webPayService.createWebPayRequest(paymentTransaction);

        return Results.html().render("data", webPayTransactionRequestPojo);
    }

    public Result paymentCompleted(WebPayTransactionResponsePojo data) {
        PaymentTransaction paymentTransaction = paymentTransactionService.getPaymentTransactionByTransactionId(data.getTxnref());
        WebPayPaymentDataDto webPayPaymentDataDto = webPayService.getPaymentData(paymentTransaction);
        if (webPayPaymentDataDto.getResponseCode().equalsIgnoreCase("00")) {
            paymentTransaction.setPaymentTransactionStatus(PaymentTransactionStatus.SUCCESSFUL);
        }
        return Results.html().render("data", webPayPaymentDataDto);
    }
}
