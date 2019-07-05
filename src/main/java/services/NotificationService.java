package services;

import com.bw.payment.entity.NotificationQueue;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import dao.PaymentTransactionDao;
import ninja.lifecycle.Dispose;
import ninja.utils.NinjaProperties;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.PaymentUtil;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/*
 * Created by Gibah Joseph on Sep, 2018
 */
@Singleton
public class NotificationService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private PaymentTransactionDao paymentTransactionDao;
    private OkHttpClient client;
    private ThreadPoolExecutor notificationExecutorService;

    @Inject
    public NotificationService(PaymentTransactionDao paymentTransactionDao, NinjaProperties ninjaProperties) {
        this.paymentTransactionDao = paymentTransactionDao;
        this.client = PaymentUtil.getOkHttpClient(ninjaProperties);
        this.notificationExecutorService = new ThreadPoolExecutor(1, 1, 2, TimeUnit.SECONDS, new SynchronousQueue<>(), new ThreadPoolExecutor.DiscardPolicy());
    }

    private void processPendingNotifications(Integer batchSize) {
        int maxRetry = paymentTransactionDao.getMaxRetryCount();
        List<NotificationQueue> notificationQueues = paymentTransactionDao.getPendingNotifications(batchSize, maxRetry);
//        logger.info("<==== fetched payment notifications : " + notificationQueues.size());
        for (NotificationQueue notificationQueue : notificationQueues) {
            doNotification(notificationQueue);
        }
    }

    public void doNotification(NotificationQueue notificationQueue) {
        try {
            RequestBody body = RequestBody.create(JSON, notificationQueue.getMessageInJson());
            logger.info("<==== processing payment notification : " + notificationQueue.getMessageInJson());
            Request request = new Request.Builder().url(notificationQueue.getNotificationUrl()).post(body).build();

            try (Response response = client.newCall(request).execute()) {
                logger.info("<== payment notification response code [] " + request.url() + " : " + response.code() + response.message());
                notificationQueue.setRetryCount(notificationQueue.getRetryCount() == null ? 1 : notificationQueue.getRetryCount() + 1);
                notificationQueue.setLastMerchantResponse(String.format("%s :: %s", response.toString(), response.body() != null ? response.body().string() : ""));
                if (response.code() == 200) {
                    notificationQueue.setNotificationSent(true);
                }
                saveNotification(notificationQueue);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendPaymentNotification(Integer batchSize) {
        Integer finalBatchSize = batchSize == null ? 50 : batchSize;

        notificationExecutorService.execute(() -> processPendingNotifications(finalBatchSize));
    }

    @Transactional
    private void saveNotification(NotificationQueue notificationQueue) {
        if (notificationQueue.getId() == null) {
            paymentTransactionDao.saveObject(notificationQueue);
        } else {
            paymentTransactionDao.updateObject(notificationQueue);
        }
    }

    @Dispose
    private void cleanUp() {
        logger.info("<=== cleaning up notification queue");
        if (notificationExecutorService != null) {
            notificationExecutorService.shutdownNow();
            notificationExecutorService = null;
        }
    }
}
