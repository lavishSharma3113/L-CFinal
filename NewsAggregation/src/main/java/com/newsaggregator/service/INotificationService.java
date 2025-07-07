package com.newsaggregator.service;

import com.newsaggregator.model.Notification;
import java.util.List;

public interface INotificationService {
    boolean sendNotification(Notification notification);
    List<Notification> getUserNotifications(int userId);
    boolean markNotificationRead(int id);
}
