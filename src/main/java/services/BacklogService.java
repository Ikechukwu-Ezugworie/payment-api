package services;

import com.bw.payment.entity.RawDump;
import com.google.inject.Inject;
import controllers.PayDirectController;
import dao.PaymentTransactionDao;
import ninja.Context;
import ninja.ReverseRouter;
import ninja.utils.NinjaProperties;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.Constants;
import utils.PaymentUtil;

import java.util.List;

/*
 * Created by Gibah Joseph on Oct, 2018
 */
public class BacklogService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    private OkHttpClient client;
    private PaymentTransactionDao paymentTransactionDao;
    private ReverseRouter reverseRouter;

    @Inject
    public BacklogService(OkHttpClient client, PaymentTransactionDao paymentTransactionDao, NinjaProperties ninjaProperties,
                          ReverseRouter reverseRouter) {
        this.client = PaymentUtil.getOkHttpClient(ninjaProperties);
        this.paymentTransactionDao = paymentTransactionDao;
        this.reverseRouter = reverseRouter;
    }

    public void processAllPaymentNotifications(Context context) {
        List<RawDump> rawDumps = paymentTransactionDao.getByProperty(RawDump.class, "description", "PAYMENT NOTIFICATION");
        logger.info("<== PROCESSING BACKLOG :: :: {} payments", rawDumps.size());
        for (RawDump rawDump : rawDumps) {
            String url = paymentTransactionDao.getSettingsValue(Constants.END_SYSTEM_BASE_URL, "http://localhost:8080", true) +
                    reverseRouter.with(PayDirectController::doPayDirectRequest);
            logger.info("<== POSTING PAYMENT ID :: {} :: to :: {} :: ", rawDump.getId(), url);

            MediaType XML = MediaType.parse("application/xml; charset=utf-8");
            RequestBody body = RequestBody.create(XML, rawDump.getRequest());
            Request request = new Request.Builder().url(url).post(body).build();
            try (Response response = client.newCall(request).execute()) {
                String s = response.body().string();

//                logger.info("<== BACKLOG ID {} responded with code {} and body {}", rawDump.getId(), response.code(), s);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
