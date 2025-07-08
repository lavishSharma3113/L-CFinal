package com.newsaggregator.emailscheduler;

import com.newsaggregator.email.emailservice.EmailNewsNotifierService;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class EmailNotificationScheduler {
    private static ScheduledExecutorService scheduler;

    public void startDailyNotificationTask() {
        if (scheduler == null || scheduler.isShutdown()) {
            scheduler = Executors.newSingleThreadScheduledExecutor();

            Runnable emailTask = EmailNewsNotifierService::sendTodaysNewsToAllUsers;
            scheduler.scheduleAtFixedRate(emailTask, 0, 24, TimeUnit.HOURS);

            System.out.println("News notification scheduler started.");
        }
    }

    public void stopDailyNotificationTask() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
            System.out.println("News notification scheduler stopped.");
        }
    }


}
