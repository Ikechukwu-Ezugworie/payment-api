package controllers;

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
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojo.payDirect.customerValidation.response.CustomerInformationResponse;
import utils.PaymentUtil;

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
        if (StringUtils.isBlank(merchRef)) {
            return Results.badRequest().json().render("errorMessage", "Customer reference cannot be empty");
        }

        String payload = "<CustomerInformationRequest><ServiceUsername></ServiceUsername><ServicePassword></ServicePassword>" +
                "<MerchantReference>" + merchRef + "</MerchantReference><CustReference>" + custRef + "</CustReference><PaymentItemCode>" +
                "" + "</PaymentItemCode><ThirdPartyCode></ThirdPartyCode></CustomerInformationRequest>";
        String url = "http://" + context.getHostname() + reverseRouter.with(PayDirectController::doPayDirectRequest);

        MediaType XML = MediaType.parse("application/xml; charset=utf-8");
        RequestBody body = RequestBody.create(XML, payload);
        Request request = new Request.Builder().url(url).post(body).build();
        try (Response response = client.newCall(request).execute()) {
            if (response.code() == 200) {
                String s = response.body().string();
                CustomerInformationResponse customerInformationResponse = xmlMapper.readValue(s, CustomerInformationResponse.class);
                return Results.json().render(customerInformationResponse);
            }
            return Results.status(response.code()).json();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Results.internalServerError().json();
    }
}
