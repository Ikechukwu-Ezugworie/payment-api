package services;

import com.bw.payment.entity.NotificationQueue;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import dao.MerchantDao;
import dao.PaymentTransactionDao;
import ninja.ReverseRouter;
import ninja.utils.NinjaProperties;
import okhttp3.*;
import services.sequence.PayerIdSequence;
import services.sequence.TransactionIdSequence;
import utils.PaymentUtil;

import java.io.IOException;
import java.util.List;

/*
 * Created by Gibah Joseph on Sep, 2018
 */
public class NotificationService {
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private PaymentTransactionDao paymentTransactionDao;

    private OkHttpClient client;

    @Inject
    public NotificationService(PaymentTransactionDao paymentTransactionDao, NinjaProperties ninjaProperties) {
        this.paymentTransactionDao = paymentTransactionDao;
        this.client = PaymentUtil.getOkHttpClient(ninjaProperties);
    }

    @Transactional
    private void processPendingNotifications(Integer batchSize) {
        List<NotificationQueue> notificationQueues = paymentTransactionDao.getPendingNotifications(batchSize);
        System.out.println("<==== processing notifications : " + notificationQueues.size());
        for (NotificationQueue notificationQueue : notificationQueues) {
            doNotification(notificationQueue);
        }
    }

    @Transactional
    public void doNotification(NotificationQueue notificationQueue) {
        try {
            RequestBody body = RequestBody.create(JSON, notificationQueue.getMessageInJson());
            System.out.println("<==== processing notification : " + notificationQueue.getMessageInJson());
            Request request = new Request.Builder().url(notificationQueue.getNotificationUrl()).post(body).build();

            try (Response response = client.newCall(request).execute()) {
                System.out.println("<== notification response code " + request.url() + " : " + response.code());
                if (response.code() == 200) {
                    notificationSent(notificationQueue);
                    return;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (notificationQueue.getId() == null) {
            paymentTransactionDao.saveObject(notificationQueue);
        }
    }

    public void sendPaymentNotification(Integer batchSize) {
        Integer finalBatchSize = batchSize == null ? 50 : batchSize;
        processPendingNotifications(finalBatchSize);
    }

    private void notificationSent(NotificationQueue notificationQueue) {
        notificationQueue.setNotificationSent(true);
        paymentTransactionDao.updateObject(notificationQueue);
    }
}
