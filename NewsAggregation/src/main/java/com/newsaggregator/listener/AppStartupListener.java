package com.newsaggregator.listener;

import com.newsaggregator.emailscheduler.EmailNotificationScheduler;
import com.newsaggregator.scheduler.NewsFetcherScheduler;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class AppStartupListener implements ServletContextListener {

    private NewsFetcherScheduler newsScheduler;
    private EmailNotificationScheduler emailScheduler;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("[AppStartupListener] Initializing schedulers...");

//        newsScheduler = new NewsFetcherScheduler();
//      newsScheduler.start();
//
//        emailScheduler = new EmailNotificationScheduler();
//        emailScheduler.startDailyNotificationTask();

        System.out.println("[AppStartupListener] Schedulers started successfully.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
//        if (newsScheduler != null) {
//            newsScheduler.stop();
//        }
//
//        if (emailScheduler != null) {
//            emailScheduler.stopDailyNotificationTask();
//        }

        System.out.println("[AppStartupListener] Application shutdown completed.");
    }
}
