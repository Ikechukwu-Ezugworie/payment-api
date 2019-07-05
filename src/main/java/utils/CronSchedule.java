package utils;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import ninja.scheduler.Schedule;
import services.NotificationService;

import java.util.concurrent.TimeUnit;

@Singleton
public class CronSchedule {
    private NotificationService notificationService;

    @Inject
    public CronSchedule(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Schedule(delay = 30, initialDelay = 1, timeUnit = TimeUnit.SECONDS)
    public void runNotificationJob() {
        notificationService.sendPaymentNotification(100);
    }
}
