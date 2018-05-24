package controllers;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import dao.MerchantDao;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;
import org.apache.commons.lang3.StringUtils;
import pojo.payDirect.customerValidation.request.CustomerInformationRequest;
import pojo.payDirect.customerValidation.response.CustomerInformationResponse;
import pojo.payDirect.paymentNotification.request.PaymentNotificationRequest;
import pojo.payDirect.paymentNotification.response.PaymentNotificationResponse;
import services.PayDirectService;
import services.PaymentTransactionService;
import services.QuickTellerService;
import utils.Constants;
import utils.PaymentUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * CREATED BY GIBAH
 */
@Singleton
public class PrototypeController {
    @Inject
    private XmlMapper xmlMapper;
    @Inject
    private PayDirectService payDirectService;
    @Inject
    private QuickTellerService quickTellerService;
    @Inject
    private PaymentTransactionService paymentTransactionService;
    @Inject
    private MerchantDao merchantDao;


    public Result interswitchPay(@Param("amount") String amount, @Param("transactionId") String transactionId,
                                 @Param("itemCode") String itemCode, @Param("type") String type, Context context) {
        System.out.println("<=== processing payment" + transactionId + amount);
        return Results.html().render("tid", transactionId).render("amount", amount == null ? null : PaymentUtil.getFormattedMoneyDisplay(PaymentUtil.getAmountInKobo(new BigDecimal(amount))));
//        String payload = "<CustomerInformationRequest><ServiceUsername></ServiceUsername><ServicePassword></ServicePassword>" +
//                "<MerchantReference>1342356</MerchantReference><CustReference>" + transactionId + "</CustReference><PaymentItemCode>" +
//                itemCode + "</PaymentItemCode><ThirdPartyCode></ThirdPartyCode></CustomerInformationRequest>";
//
//        try {
//            CustomerInformationRequest request = null;
//            request = xmlMapper.readValue(payload, CustomerInformationRequest.class);
//            CustomerInformationResponse customerInformationResponse = payDirectService.processCustomerValidationRequest(request, context);
//            System.out.println(PaymentUtil.toJSON(customerInformationResponse));
//            if (customerInformationResponse == null || customerInformationResponse.getCustomers().getCustomers().get(0).getStatus() == PayDirectService.CUSTOMER_INVALID) {
//                return Results.badRequest().json();
////                String key = type.equalsIgnoreCase("ar") ? "poa" : type.equalsIgnoreCase("rin") ? "directCapture" : "";
////                if (key.equalsIgnoreCase("poa")) {
////                    context.getFlashScope().error("Customer not found. Enter RIN or Phone number instead");
////                } else if (key.equalsIgnoreCase("directCapture")) {
////                    context.getFlashScope().error("Customer not found. Enter Direct capture details");
////                }
////                return Results.badRequest().json().render(key, "true");
//            } else {
//                return Results.json().render("data", customerInformationResponse);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return Results.badRequest().json();
    }

    public Result doMakePay(@Param("amount") String amount, @Param("transactionId") String transactionId,
                            @Param("itemCode") String itemCode, @Param("type") String type, Context context) {
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.INTERSWITCH_DATE_FORMAT);
        String payload = "<PaymentNotificationRequest><ServiceUrl>http://test.com/Payments/Interswitch/Notification_CPN.aspx</ServiceUrl>" +
                "<ServiceUsername/><ServicePassword/><FtpUrl>http://test.com/Payments/Interswitch/Notification_CPN.aspx</FtpUrl>" +
                "<FtpUsername/><FtpPassword/><Payments><Payment><IsRepeated>False</IsRepeated><ProductGroupCode>HTTPGENERICv31</ProductGroupCode>" +
                "<PaymentLogId>1331" + transactionId + new Date().getTime() + "</PaymentLogId><CustReference>" + transactionId + "</CustReference><AlternateCustReference>--N/A--</AlternateCustReference>" +
                "<Amount>" + amount + "</Amount><PaymentStatus>0</PaymentStatus><PaymentMethod>Cash</PaymentMethod><PaymentReference>FBN|BRH|ABSA|17-03-2016|" + new Date().getTime() + "</PaymentReference>" +
                "<TerminalId/><ChannelName>Bank Branc</ChannelName><Location>ABAJI</Location><IsReversal>False</IsReversal><PaymentDate>" + sdf.format(new Date()) + "</PaymentDate>" +
                "<SettlementDate>03/18/2016 00:00:01</SettlementDate><InstitutionId>ABSA</InstitutionId><InstitutionName>Abia State Autoreg</InstitutionName>" +
                "<BranchName>ABAJI</BranchName><BankName>First Bank of Nigeria Plc</BankName><FeeName/><CustomerName>" + context.getParameter("name") + "</CustomerName><OtherCustomerInfo>|</OtherCustomerInfo>" +
                "<ReceiptNo>1607749469</ReceiptNo><CollectionsAccount>12232345690</CollectionsAccount><ThirdPartyCode/><PaymentItems><PaymentItem>" +
                "<ItemName>Payment</ItemName><ItemCode>" + itemCode + "</ItemCode><ItemAmount>" + amount + "</ItemAmount><LeadBankCode>FBN</LeadBankCode><LeadBankCbnCode>011</LeadBankCbnCode>" +
                "<LeadBankName>First Bank of Nigeria Plc</LeadBankName><CategoryCode/>";
        if (context.getParameter("desc") != null) {
            payload += "<CategoryName>" + context.getParameter("desc") + "</CategoryName>";
        }
        payload += "<ItemQuantity>1</ItemQuantity></PaymentItem></PaymentItems><BankCode>FBN</BankCode><CustomerAddress>" +
                context.getParameter("address") + "</CustomerAddress><CustomerPhoneNumber>" + context.getParameter("phoneNumber") + "</CustomerPhoneNumber><DepositorName/><DepositSlipNumber>1212343</DepositSlipNumber>" +
                "<PaymentCurrency>566</PaymentCurrency><OriginalPaymentLogId/><OriginalPaymentReference/><Teller>ABAJI13 ABAJI13</Teller></Payment></Payments>" +
                "</PaymentNotificationRequest>";

        try {
            System.out.println("<=== processing payment");
            PaymentNotificationRequest request = xmlMapper.readValue(payload, PaymentNotificationRequest.class);
            PaymentNotificationResponse paymentNotificationResponsePojo = payDirectService.processPaymentNotification(request, context);
            if (paymentNotificationResponsePojo == null) {
                context.getFlashScope().error("Could not contact end system");
            } else if (paymentNotificationResponsePojo.getPayments().getPayment().get(0).getStatus() == PayDirectService.NOTIFICATION_RECEIVED) {
                context.getFlashScope().success("Your payment was successful");
            } else {
                context.getFlashScope().error(paymentNotificationResponsePojo.getPayments().getPayment().get(0).getStatusMessage());
            }
            return Results.redirect("/interswitch?transactionId=" + (StringUtils.isBlank(transactionId) ? context.getParameter("phoneNumber") : transactionId) + "&amount=" + amount);
        } catch (
                IOException e)

        {
            e.printStackTrace();
        }
        context.getFlashScope().

                error("Error making payment");
        return Results.redirect("/interswitch");
    }

    public Result assRef(@Param("amount") String amount, @Param("transactionId") String transactionId,
                         @Param("itemCode") String itemCode, @Param("type") String type, Context context) {
        if (StringUtils.isBlank(transactionId)) {
            return Results.html().template("/views/PrototypeController/assRef.ftl.html").render("tid", transactionId);
        }

        String payload = "<CustomerInformationRequest><ServiceUsername></ServiceUsername><ServicePassword></ServicePassword>" +
                "<MerchantReference>13425356</MerchantReference><CustReference>" + transactionId + "</CustReference><PaymentItemCode>" +
                itemCode + "</PaymentItemCode><ThirdPartyCode></ThirdPartyCode></CustomerInformationRequest>";
        try {
            CustomerInformationRequest request = null;
            request = xmlMapper.readValue(payload, CustomerInformationRequest.class);
            CustomerInformationResponse customerInformationResponse = payDirectService.processCustomerValidationRequest(request, context);
            System.out.println(PaymentUtil.toJSON(customerInformationResponse));
            if (customerInformationResponse == null || customerInformationResponse.getCustomers().getCustomers().get(0).getStatus() == PayDirectService.CUSTOMER_INVALID) {
                context.getFlashScope().error("No customer information found. Try pay on account.");
                return Results.html();
            } else {
                return Results.html().render("data", customerInformationResponse).render("tid", transactionId);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        context.getFlashScope().error("No customer information found. Try pay on account.");
        return Results.html().render("tid", transactionId);
    }

    public Result poa(@Param("amount") String amount, @Param("transactionId") String transactionId,
                      @Param("itemCode") String itemCode, @Param("type") String type, Context context) {
        if (StringUtils.isBlank(transactionId)) {
            return Results.html().template("/views/PrototypeController/poa.ftl.html");
        }
        String payload = "<CustomerInformationRequest><ServiceUsername></ServiceUsername><ServicePassword></ServicePassword>" +
                "<MerchantReference>1342356</MerchantReference><CustReference>" + transactionId + "</CustReference><PaymentItemCode>" +
                itemCode + "</PaymentItemCode><ThirdPartyCode></ThirdPartyCode></CustomerInformationRequest>";

        try {
            CustomerInformationRequest request = null;
            request = xmlMapper.readValue(payload, CustomerInformationRequest.class);
            CustomerInformationResponse customerInformationResponse = payDirectService.processCustomerValidationRequest(request, context);
            System.out.println(PaymentUtil.toJSON(customerInformationResponse));
            if (customerInformationResponse == null || customerInformationResponse.getCustomers().getCustomers().get(0).getStatus() == PayDirectService.CUSTOMER_INVALID) {
                context.getFlashScope().error("No customer information found. Try direct capture.");
                return Results.html();
            } else {
                return Results.html().render("data", customerInformationResponse);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        context.getFlashScope().error("No customer information found. Try direct capture.");
        return Results.html();
    }

    public Result dirCap(@Param("amount") String amount, @Param("transactionId") String transactionId,
                         @Param("itemCode") String itemCode, @Param("type") String type, Context context) {
        return Results.html();
    }

    public Result cusData(@Param("amount") String amount, @Param("transactionId") String transactionId,
                          @Param("itemCode") String itemCode, @Param("type") String type, Context context) {
        if (StringUtils.isBlank(transactionId)) {
            return Results.html().template("/views/PrototypeController/assRef.ftl.html");
        }
        String payload = "<CustomerInformationRequest><ServiceUsername></ServiceUsername><ServicePassword></ServicePassword>" +
                "<MerchantReference>1342356</MerchantReference><CustReference>" + transactionId + "</CustReference><PaymentItemCode>" +
                itemCode + "</PaymentItemCode><ThirdPartyCode></ThirdPartyCode></CustomerInformationRequest>";

        try {
            CustomerInformationRequest request = null;
            request = xmlMapper.readValue(payload, CustomerInformationRequest.class);
            CustomerInformationResponse customerInformationResponse = payDirectService.processCustomerValidationRequest(request, context);
            System.out.println(PaymentUtil.toJSON(customerInformationResponse));
            if (customerInformationResponse == null || customerInformationResponse.getCustomers().getCustomers().get(0).getStatus() == PayDirectService.CUSTOMER_INVALID) {
                String key = type.equalsIgnoreCase("ar") ? "poa" : type.equalsIgnoreCase("rin") ? "directCapture" : "";
                if (key.equalsIgnoreCase("poa")) {
                    context.getFlashScope().error("Customer not found. Enter RIN or Phone number instead");
                } else if (key.equalsIgnoreCase("directCapture")) {
                    context.getFlashScope().error("Customer not found. Enter Direct capture details");
                }
                return Results.html().render(key, "true");
            } else {
                return Results.html().render("data", customerInformationResponse);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Results.html();
    }

//    public Result quickTeller() {
//        Merchant merchant = merchantDao.getMerchantByCode("M0000001");
//        TransactionRequestPojo request = PaymentUtil.fromJSON("{\"merchantTransactionReferenceId\":\"0000000029\",\"amountInKobo\":34508734,\"notifyOnStatusChange\":true,\"notificationUrl\":\"\",\"paymentProvider\":\"INTERSWITCH\",\"paymentChannel\":\"QUICKTELLER\",\"payer\":{\"firstName\":\"Ramos\",\"lastName\":\"Harrell\",\"email\":\"ramosharrell@automon.com\",\"phoneNumber\":\"08137625011\"},\"items\":[{\"name\":\"ERAS ASSESSMENT\",\"itemId\":\"EDORPX821\",\"quantity\":1,\"priceInKobo\":34508734,\"taxInKobo\":0,\"subTotalInKobo\":34508734,\"totalInKobo\":34508734,\"description\":\"Pools Promoters Weekly Pay Tax - Annual Fee\"}],\"validateTransaction\":false}", TransactionRequestPojo.class);
//        Ticket transactionTicket = paymentTransactionService.createInstantTransaction(request, merchant);
//
//        System.out.println(transactionTicket);
//        return Results.html().render("data", transactionTicket);
//    }
}
