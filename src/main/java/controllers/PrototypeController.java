package controllers;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;
import ninja.utils.NinjaProperties;
import org.apache.commons.lang3.StringUtils;
import pojo.payDirect.customerValidation.request.CustomerInformationRequest;
import pojo.payDirect.customerValidation.response.CustomerInformationResponse;
import pojo.payDirect.paymentNotification.request.PaymentNotificationRequest;
import pojo.payDirect.paymentNotification.response.PaymentNotificationResponse;
import services.PayDirectService;
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
    private static String MERCHANT_REF = "6405";
    private XmlMapper xmlMapper;
    private PayDirectService payDirectService;
    private NinjaProperties ninjaProperties;

    @Inject
    public PrototypeController(XmlMapper xmlMapper, PayDirectService payDirectService, NinjaProperties ninjaProperties) {
        this.xmlMapper = xmlMapper;
        this.payDirectService = payDirectService;
        this.ninjaProperties = ninjaProperties;

        if (this.ninjaProperties.isDev()) {
            MERCHANT_REF = "13425356";
        } else {
            MERCHANT_REF = payDirectService.getDefaultMerchantReference();
        }
    }

    public Result interswitchPay(@Param("amount") String amount, @Param("transactionId") String transactionId,
                                 @Param("itemCode") String itemCode, @Param("type") String type, Context context) {
        System.out.println("<=== processing payment" + transactionId + amount);
        return Results.html().render("tid", transactionId).render("amount", amount == null ? null :
                PaymentUtil.getFormattedMoneyDisplay(PaymentUtil.getAmountInKobo(new BigDecimal(amount))));
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
        String payload = generatePayload(amount, transactionId, itemCode, context);

        try {
            System.out.println("<=== processing payment " + payload);
            PaymentNotificationRequest request = xmlMapper.readValue(payload, PaymentNotificationRequest.class);
            PaymentNotificationResponse paymentNotificationResponsePojo = payDirectService.processPaymentNotification(request, rawDump, context);
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
                "<MerchantReference>" + MERCHANT_REF + "</MerchantReference><CustReference>" + transactionId + "</CustReference><PaymentItemCode>" +
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
                "<MerchantReference>" + MERCHANT_REF + "</MerchantReference><CustReference>" + transactionId + "</CustReference><PaymentItemCode>" +
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
                "<MerchantReference>" + MERCHANT_REF + "</MerchantReference><CustReference>" + transactionId + "</CustReference><PaymentItemCode>" +
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

    private String generatePayload(String amount, String transactionId, String itemCode, Context context) {
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.INTERSWITCH_DATE_FORMAT);
        StringBuilder payloadBuilder = new StringBuilder();
        payloadBuilder.append("<PaymentNotificationRequest><ServiceUrl>http://test.com/Payments/Interswitch/Notification_CPN.aspx</ServiceUrl>")
                .append("<ServiceUsername/><ServicePassword/><FtpUrl>http://test.com/Payments/Interswitch/Notification_CPN.aspx</FtpUrl>")
                .append("<FtpUsername/><FtpPassword/><Payments><Payment><IsRepeated>False</IsRepeated><ProductGroupCode>HTTPGENERICv31</ProductGroupCode>")
                .append("<PaymentLogId>1331").append(transactionId).append(new Date().getTime()).append("</PaymentLogId><CustReference>")
                .append(transactionId).append("</CustReference>").append("<AlternateCustReference>").append(context.getParameter("cCat")).append("</AlternateCustReference>")
                .append("<Amount>").append(amount).append("</Amount><PaymentStatus>0</PaymentStatus><PaymentMethod>Cash</PaymentMethod>")
                .append("<PaymentReference>FBN|BRH|ABSA|17-03-2016|").append(new Date().getTime()).append("</PaymentReference>")
                .append("<TerminalId/><ChannelName>Bank Branc</ChannelName><Location>ABAJI</Location><IsReversal>False</IsReversal>")
                .append("<PaymentDate>").append(sdf.format(new Date())).append("</PaymentDate>")
                .append("<SettlementDate>03/18/2016 00:00:01</SettlementDate><InstitutionId>ABSA</InstitutionId><InstitutionName>Abia State Autoreg</InstitutionName>")
                .append("<BranchName>ABAJI</BranchName><BankName>First Bank of Nigeria Plc</BankName><FeeName/>")
                .append("<CustomerName>").append(context.getParameter("name")).append("</CustomerName>")
                .append("<OtherCustomerInfo>");
        //other customer info
        if (StringUtils.isNotBlank(context.getParameter("email"))) {
            payloadBuilder.append("<EmailAddress>").append(context.getParameter("email")).append("</EmailAddress>");
        }
        if (StringUtils.isNotBlank(context.getParameter("eaid"))) {
            payloadBuilder.append("<EconomicsActivitiesID>").append(context.getParameter("eaid")).append("</EconomicsActivitiesID>");
        }
        if (StringUtils.isNotBlank(context.getParameter("taxOfficeId"))) {
            payloadBuilder.append("<TaxOfficeID>").append(context.getParameter("taxOfficeId")).append("</TaxOfficeID>");
        }
        if (StringUtils.isNotBlank(context.getParameter("nid"))) {
            payloadBuilder.append("<NationalID>").append(context.getParameter("nid")).append("</NationalID>");
        }
        if (StringUtils.isNotBlank(context.getParameter("notMethod"))) {
            payloadBuilder.append("<NotificationMethod>").append(context.getParameter("notMethod")).append("</NotificationMethod>");
        }
        payloadBuilder.append("</OtherCustomerInfo>")
                .append("<ReceiptNo>1607749469</ReceiptNo><CollectionsAccount>12232345690</CollectionsAccount><ThirdPartyCode/><PaymentItems><PaymentItem>")
                .append("<ItemName>Payment</ItemName>")
                .append("<ItemCode>").append(itemCode).append("</ItemCode>")
                .append("<ItemAmount>").append(amount).append("</ItemAmount>")
                .append("<LeadBankCode>FBN</LeadBankCode><LeadBankCbnCode>011</LeadBankCbnCode>")
                .append("<LeadBankName>First Bank of Nigeria Plc</LeadBankName><CategoryCode/>");
        if (context.getParameter("desc") != null) {
            payloadBuilder.append("<CategoryName>").append(context.getParameter("desc")).append("</CategoryName>");
        }
        payloadBuilder.append("<ItemQuantity>1</ItemQuantity></PaymentItem></PaymentItems><BankCode>FBN</BankCode><CustomerAddress>")
                .append(context.getParameter("address")).append("</CustomerAddress><CustomerPhoneNumber>").append(context.getParameter("phoneNumber"))
                .append("</CustomerPhoneNumber><DepositorName/><DepositSlipNumber>1212343</DepositSlipNumber>")
                .append("<PaymentCurrency>566</PaymentCurrency><OriginalPaymentLogId/><OriginalPaymentReference/><Teller>ABAJI13 ABAJI13</Teller></Payment></Payments></PaymentNotificationRequest>");
        return payloadBuilder.toString();
    }
}
