package controllers;

import com.bw.payment.entity.PaymentTransaction;
import com.bw.payment.enumeration.PaymentTransactionStatus;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import dao.PaymentTransactionDao;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojo.ItemPojo;
import pojo.PayerPojo;
import pojo.TransactionRequestPojo;
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


    public Result paymentPage(@Param("amountInKobo") Long amount, @Param("notificationUrl") String notificationUrl, @Param("productId") String productId,
                              @Param("payerName") String payerName, @Param("payerEmail") String payerEmail, @Param("customerReference") String customerReference,
                              @Param("paymentItemId") String paymentItemId) {
        TransactionRequestPojo transactionRequestPojo = new TransactionRequestPojo();
        transactionRequestPojo.setAmountInKobo(amount);
        transactionRequestPojo.setNotifyOnStatusChange(true);
        transactionRequestPojo.setNotificationUrl(notificationUrl);
        transactionRequestPojo.setPaymentProvider("INTERSWITCH");
        transactionRequestPojo.setPaymentChannel("WEBPAY");
        transactionRequestPojo.setServiceTypeId(productId);
        transactionRequestPojo.setCustomerTransactionReference(customerReference);
        PayerPojo payer = new PayerPojo();
        payer.setFirstName(payerName);
        payer.setEmail(payerEmail);
        transactionRequestPojo.setPayer(payer);

        ItemPojo item = new ItemPojo();
        item.setItemId(paymentItemId);
        item.setName("WEBPAY ITEM");
        item.setPriceInKobo(amount);
        item.setTotalInKobo(amount);
        item.setSubTotalInKobo(amount);
        transactionRequestPojo.setItems(Arrays.asList(item));
        transactionRequestPojo.setInstantTransaction(true);

        PaymentTransaction paymentTransaction = paymentTransactionDao.createTransaction(transactionRequestPojo, null);

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
