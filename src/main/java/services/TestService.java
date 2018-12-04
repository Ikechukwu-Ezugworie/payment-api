package services;

import com.google.inject.Inject;
import controllers.PayDirectController;
import dao.PaymentTransactionDao;
import ninja.ReverseRouter;
import ninja.utils.NinjaProperties;
import okhttp3.*;
import utils.Constants;
import utils.PaymentUtil;

import java.io.IOException;

/*
 * Created by Gibah Joseph on Nov, 2018
 */
public class TestService {
    @Inject
    private PaymentTransactionDao paymentTransactionDao;
    @Inject
    private ReverseRouter reverseRouter;
    private OkHttpClient client;

    @Inject
    public TestService(OkHttpClient client, NinjaProperties ninjaProperties) {
        this.client = PaymentUtil.getOkHttpClient(ninjaProperties);
    }

    public Response doCustomerValidation(String xmlRequest) throws IOException {
        String url = String.format("%s%s", paymentTransactionDao.getSettingsValue(Constants.END_SYSTEM_BASE_URL, "http://localhost:8080", true),
                reverseRouter.with(PayDirectController::doPayDirectRequest));

        MediaType XML = MediaType.parse("application/xml; charset=utf-8");
        RequestBody body = RequestBody.create(XML, xmlRequest);
        Request r = new Request.Builder().url(url).post(body).build();
        return client.newCall(r).execute();
    }

    public String getCustomerValidationTestPayload() {
        String p = "<CustomerInformationRequest xmlns:ns2=\"http://techquest.interswitchng.com/\" xmlns:ns3=\"http://www.w3.org/2003/05/soap-envelope\">" +
                "<ServiceUrl>http://185.203.119.28/api/v1/payments/interswitch/paydirect</ServiceUrl><ServiceUsername></ServiceUsername>" +
                "<ServicePassword></ServicePassword><RouteId>HTTPGENERICv31</RouteId><Service>bill-payment-service</Service>" +
                "<MerchantReference>7656</MerchantReference><CustReference>HARDWARE</CustReference><PaymentItemCategoryCode>" +
                "</PaymentItemCategoryCode><RequestReference></RequestReference><TerminalId></TerminalId><Amount>0</Amount>" +
                "<FtpUsername></FtpUsername><FtpPassword></FtpPassword></CustomerInformationRequest>";
        return paymentTransactionDao.getSettingsValue("TEST_CUSTOMER_VALIDATION_PAYLOAD", p, true);
    }
}
