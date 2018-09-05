package utils;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import ninja.scheduler.Schedule;
import services.NotificationService;
import services.PaymentTransactionService;

import javax.annotation.PreDestroy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Singleton
public class CronSchedule {
    private NotificationService notificationService;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Inject
    public CronSchedule(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Schedule(delay = 5, initialDelay = 1, timeUnit = TimeUnit.MINUTES)
    public void runNotificationJob() {
        System.out.println("::: RUNNING NOTIFICATION JOB :::");
        executorService.submit(() -> notificationService.sendPaymentNotification(100));
        System.out.println("::: ENDING NOTIFICATION JOB :::");
    }

    @PreDestroy
    public void cleanUp() {
        System.out.println(" [] =======> DESTROYING<=======");
        executorService.shutdownNow();
    }
}
