package services;

import com.bw.payment.entity.NotificationQueue;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import dao.PaymentTransactionDao;
import ninja.lifecycle.Dispose;
import ninja.utils.NinjaProperties;
import okhttp3.*;
import utils.PaymentUtil;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
 * Created by Gibah Joseph on Sep, 2018
 */
@Singleton
public class NotificationService {
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private PaymentTransactionDao paymentTransactionDao;
    private static ExecutorService notificationService = Executors.newSingleThreadExecutor();
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
        notificationService.execute(() -> processPendingNotifications(finalBatchSize));
    }

    private void notificationSent(NotificationQueue notificationQueue) {
        notificationQueue.setNotificationSent(true);
        paymentTransactionDao.updateObject(notificationQueue);
    }

    @Dispose
    private void cleanUp() {
        System.out.println("<=== cleaning up notification queue");
        notificationService.shutdown();
    }
}
