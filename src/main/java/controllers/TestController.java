package controllers;

import com.bw.payment.entity.PaymentTransaction;
import com.bw.payment.enumeration.PaymentResponseStatusConstant;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import dao.PaymentTransactionDao;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.ReverseRouter;
import ninja.params.Param;
import ninja.utils.NinjaProperties;
import ninja.validation.JSR303Validation;
import ninja.validation.Validation;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojo.payDirect.customerValidation.response.CustomerInformationResponse;
import pojo.payDirect.paymentNotification.response.PaymentNotificationResponse;
import pojo.payDirect.paymentNotification.response.PaymentResponsePojo;
import services.TestService;
import utils.Constants;
import utils.PaymentUtil;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * CREATED BY GIBAH
 */
@Singleton
public class TestController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private OkHttpClient client;
    private PaymentTransactionDao paymentTransactionDao;
    private ReverseRouter reverseRouter;
    private XmlMapper xmlMapper;
    private TestService testService;

    @Inject
    public TestController(OkHttpClient client, PaymentTransactionDao paymentTransactionDao, NinjaProperties ninjaProperties,
                          ReverseRouter reverseRouter, XmlMapper xmlMapper) {
        this.client = PaymentUtil.getOkHttpClient(ninjaProperties);
        this.paymentTransactionDao = paymentTransactionDao;
        this.reverseRouter = reverseRouter;
        this.xmlMapper = xmlMapper;
    }

    public Result test(Context context) {
        return Results.html();
    }

    public Result doTest(@Param("custRef") String custRef, @Param("merchRef") String merchRef, Context context) {
        if (StringUtils.isBlank(custRef)) {
            return Results.badRequest().json().render("errorMessage", "Customer reference cannot be empty");
        }
//        if (StringUtils.isBlank(merchRef)) {
//            return Results.badRequest().json().render("errorMessage", "Merchant reference cannot be empty");
//        }

        String payload = "<CustomerInformationRequest><ServiceUsername></ServiceUsername><ServicePassword></ServicePassword>" +
                "<MerchantReference>" + merchRef + "</MerchantReference><CustReference>" + custRef + "</CustReference><PaymentItemCode>" +
                "" + "</PaymentItemCode><ThirdPartyCode></ThirdPartyCode></CustomerInformationRequest>";
        String url = String.format("%s%s", paymentTransactionDao.getSettingsValue(Constants.END_SYSTEM_BASE_URL, "http://localhost:8080", true), reverseRouter.with(PayDirectController::doPayDirectRequest));

        MediaType XML = MediaType.parse("application/xml; charset=utf-8");
        RequestBody body = RequestBody.create(XML, payload);
        Request request = new Request.Builder().url(url).post(body).build();
        try (Response response = client.newCall(request).execute()) {
            if (response.code() == 200) {
                String s = response.body().string();
                logger.info("<=== cusotmer validation res from {} ::: {}", url, s);

                CustomerInformationResponse customerInformationResponse = xmlMapper.readValue(s, CustomerInformationResponse.class);
                return Results.json().render(customerInformationResponse);
            }
            return Results.status(response.code()).json();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Results.internalServerError().json();
    }

//    private String generatePayloa(String customerReference, String name, String phoneNumber, String amount, String itemCode, boolean reversal, Context context) {
//        SimpleDateFormat sdf = new SimpleDateFormat(Constants.INTERSWITCH_DATE_FORMAT);
//
//
//        PaymentNotificationRequest paymentNotificationRequest = new PaymentNotificationRequest();
//        paymentNotificationRequest.setServiceUrl(String.format("http://%s/%s", context.getHostname(), reverseRouter.with(this::doPay)));
//
//        Payments payments = new Payments();
//
//        Payment payment = new Payment();
//        payment.setRepeated(false);
//        payment.setProductGroupCode("HTTPGENERICv31");
//        payment.setCustReference(customerReference);
//        payment.setAlternateCustReference(customerReference);
//        payment.setAmount(new BigDecimal(amount));
//        payment.setPaymentStatus(0);
//        payment.setPaymentMethod("Cash");
//        payment.setPaymentReference("FBN|BRH|ABSA|" + new Date().getTime());
//        payment.setChannelName("Bank Branch");
//        payment.setLocation("Abuja");
//        payment.setReversal(reversal);
//        payment.setPaymentDate(sdf.format(new Date()));
//        payment.setSettlementDate(sdf.format(new Date()));
//        payment.setInstitutionId("ABSA");
//        payment.setInstitutionName("Abia State Auto Reg");
//        payment.setBranchName("Abuja");
//        payment.setBankName("First Bank of Nigeria Plc");
//        payment.setCustomerName(name);
//        payment.setReceiptNo("1607749469");
//        payment.setCollectionsAccount("12232345690");
//        payment.setBankCode("FBN");
//        payment.setCustomerPhoneNumber(phoneNumber);
//        payment.setDepositorName(name);
//        payment.setDepositSlipNumber("1212343");
//        payment.setPaymentCurrency("566");
//        payment.setOriginalPaymentReference("");
//        payment.setTeller("");
//        payment.setPaymentItems(new PaymentItems());
//        payment.setStatus(0);
//        String lid = "1331" + new Date().getTime() + "";
//        payment.setPaymentLogId(reversal ? "R" + lid : lid);
//        payment.setOriginalPaymentLogId(lid);
//
//        payments.setPayment(Lists.asList(payment));
//
//        paymentNotificationRequest.setPayments();
//    }

    public Result doPay(@JSR303Validation PaymentData paymentData, Validation validation, Context context) {
        if (paymentData == null) {
            return Results.badRequest().json().render("errorMessage", "no content");
        }

        if (validation.hasViolations()) {
            return Results.badRequest().json().render("errorMessage", validation.getViolations().iterator().next().getDefaultMessage());
        }
        Integer numberOfCalls = paymentData.getDuplicateCalls();
        if (numberOfCalls == null || numberOfCalls < 1) {
            numberOfCalls = 1;
        }

        logger.info("<=== testing with number of calls {}", numberOfCalls);

        String paymentNotificationRequest = generatePayload(paymentData, context);

        if (numberOfCalls > 1) {
            makeMultipleRequests(paymentNotificationRequest, numberOfCalls, context);
            return Results.badRequest().json().render("error", "Multiple requests");
        }

        try (Response response = testService.doCustomerValidation(paymentNotificationRequest)) {
            if (response.code() == 200) {
                String s = response.body().string();
                PaymentNotificationResponse paymentNotificationResponse = xmlMapper.readValue(s, PaymentNotificationResponse.class);
                PaymentResponsePojo paymentResponsePojo = paymentNotificationResponse.getPayments().getPayment().get(0);
                PaymentTransaction paymentTransaction = paymentTransactionDao.getPaymentResponseLogByLogIdAndStatus(paymentResponsePojo.getPaymentLogId(),
                        PaymentResponseStatusConstant.ACCEPTED);
                return Results.json().render("status", paymentResponsePojo).render("details", paymentTransaction);
            }
            return Results.status(response.code()).json();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Results.internalServerError().json();
    }

    private void makeMultipleRequests(String paymentNotificationRequest, Integer numberOfCalls, Context context) {

        for (int i = 0; i < numberOfCalls; i++) {
            logger.info("<=== making payment notification call {}", i);
            String url = String.format("%s://%s%s", context.getScheme(), context.getHostname(), reverseRouter.with(PayDirectController::doPayDirectRequest));

            MediaType XML = MediaType.parse("application/xml; charset=utf-8");
            RequestBody body = RequestBody.create(XML, paymentNotificationRequest);
            Request request = new Request.Builder().url(url).post(body).build();
            int finalI = i;
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    logger.info("<== multi call {} returned with {}", finalI, response.body().string());
                }
            });
        }
    }

    private String generatePayload(PaymentData paymentData, Context context) {
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.INTERSWITCH_DATE_FORMAT);
        String lid = "1331" + new Date().getTime() + "";
        String.format("http://%s/%s", context.getHostname(), reverseRouter.with(TestController::doPay));
        StringBuilder payloadBuilder = new StringBuilder();
        payloadBuilder.append("<PaymentNotificationRequest><ServiceUrl>http://test.com/Payments/Interswitch/Notification_CPN.aspx</ServiceUrl>")
                .append("<ServiceUsername/><ServicePassword/><FtpUrl>http://test.com/Payments/Interswitch/Notification_CPN.aspx</FtpUrl>")
                .append("<FtpUsername/><FtpPassword/><Payments><Payment><IsRepeated>False</IsRepeated><ProductGroupCode>HTTPGENERICv31</ProductGroupCode>")
                .append("<PaymentLogId>").append(paymentData.getReversal() ? "R" + lid : lid).append("</PaymentLogId><CustReference>");
        if (context.getParameter("noCR", "false").equalsIgnoreCase("true")) {
            payloadBuilder.append("--NA--");
        } else {
            payloadBuilder.append(paymentData.getCustRef());
        }
        payloadBuilder.append("</CustReference>")
                .append("<AlternateCustReference>").append(paymentData.getCustRef()).append("</AlternateCustReference>")
                .append("<Amount>").append(paymentData.getAmount()).append("</Amount><PaymentStatus>0</PaymentStatus><PaymentMethod>Cash</PaymentMethod>")
                .append("<PaymentReference>FBN|BRH|ABSA|").append(new Date().getTime()).append("</PaymentReference>")
                .append("<TerminalId/><ChannelName>Bank Branc</ChannelName><Location>ABAJI</Location><IsReversal>False</IsReversal>")
                .append("<PaymentDate>").append(sdf.format(new Date())).append("</PaymentDate>")
                .append("<SettlementDate>03/18/2016 00:00:01</SettlementDate><InstitutionId>ABSA</InstitutionId><InstitutionName>Abia State Autoreg</InstitutionName>")
                .append("<BranchName>ABAJI</BranchName><BankName>First Bank of Nigeria Plc</BankName><FeeName/>")
                .append("<CustomerName>").append(paymentData.getName()).append("</CustomerName>")
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
                .append("<ItemCode>").append(paymentData.getItemCode()).append("</ItemCode>")
                .append("<ItemAmount>").append(paymentData.getAmount()).append("</ItemAmount>")
                .append("<LeadBankCode>FBN</LeadBankCode><LeadBankCbnCode>011</LeadBankCbnCode>")
                .append("<LeadBankName>First Bank of Nigeria Plc</LeadBankName><CategoryCode/>");
        if (context.getParameter("desc") != null) {
            payloadBuilder.append("<CategoryName>").append(context.getParameter("desc")).append("</CategoryName>");
        }
        payloadBuilder.append("<ItemQuantity>1</ItemQuantity></PaymentItem></PaymentItems><BankCode>FBN</BankCode><CustomerAddress>")
                .append(context.getParameter("address")).append("</CustomerAddress><CustomerPhoneNumber>").append(context.getParameter("phoneNumber"))
                .append("</CustomerPhoneNumber><DepositorName/><DepositSlipNumber>TEST_PAYMENT</DepositSlipNumber>")
                .append("<PaymentCurrency>566</PaymentCurrency><OriginalPaymentLogId>").append(lid).append("</OriginalPaymentLogId><OriginalPaymentReference/><Teller>ABAJI13 ABAJI13</Teller></Payment></Payments></PaymentNotificationRequest>");
        return payloadBuilder.toString();
    }

    public static class PaymentData {
        @NotBlank(message = "Customer reference cannot be blank")
        private String custRef;
        private String itemCode;
        @NotBlank(message = "Amount cannot be blank")
        private String amount;
        private String phoneNumber;
        @NotNull(message = "Reversal cannot be null")
        private Boolean reversal;
        @NotBlank(message = "Name cannot be blank")
        private String name;
        private Integer duplicateCalls = 1;

        public String getCustRef() {
            return custRef;
        }

        public PaymentData setCustRef(String custRef) {
            this.custRef = custRef;
            return this;
        }

        public String getItemCode() {
            return itemCode;
        }

        public PaymentData setItemCode(String itemCode) {
            this.itemCode = itemCode;
            return this;
        }

        public String getAmount() {
            return amount;
        }

        public PaymentData setAmount(String amount) {
            this.amount = amount;
            return this;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public PaymentData setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Boolean getReversal() {
            return reversal;
        }

        public PaymentData setReversal(Boolean reversal) {
            this.reversal = reversal;
            return this;
        }

        public String getName() {
            return name;
        }

        public PaymentData setName(String name) {
            this.name = name;
            return this;
        }

        public Integer getDuplicateCalls() {
            return duplicateCalls;
        }

        public PaymentData setDuplicateCalls(Integer duplicateCalls) {
            this.duplicateCalls = duplicateCalls;
            return this;
        }
    }
}
