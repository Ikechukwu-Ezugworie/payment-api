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

    @Schedule(delay = 5, initialDelay = 1, timeUnit = TimeUnit.MINUTES)
    public void runNotificationJob() {
        notificationService.sendPaymentNotification(100);
    }
}
