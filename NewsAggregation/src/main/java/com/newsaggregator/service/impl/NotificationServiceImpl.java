package com.newsaggregator.service.impl;

import com.newsaggregator.dao.NotificationDAO;
import com.newsaggregator.model.Notification;
import com.newsaggregator.service.INotificationService;

import java.util.List;

public class NotificationServiceImpl implements INotificationService {
    private final NotificationDAO dao = new NotificationDAO();

    public boolean sendNotification(Notification notification) {
        return dao.save(notification);
    }

    public List<Notification> getUserNotifications(int userId) {
        return dao.getByUser(userId);
    }

    public boolean markNotificationRead(int id) {
        return dao.markAsRead(id);
    }
}
