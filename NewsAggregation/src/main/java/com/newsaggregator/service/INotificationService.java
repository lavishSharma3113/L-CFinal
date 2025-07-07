package com.newsaggregator.service;

import com.newsaggregator.model.NewsArticle;
import com.newsaggregator.model.Notification;
import java.util.List;

public interface INotificationService {
    boolean sendNotification(Notification notification);
    List<NewsArticle> getUserNotifications(int userId);
    boolean updateLastNotificationSeen(int userId);
}
