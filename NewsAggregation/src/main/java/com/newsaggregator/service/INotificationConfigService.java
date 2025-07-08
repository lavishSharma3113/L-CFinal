package com.newsaggregator.service;


import com.newsaggregator.model.NotificationConfig;
import java.util.List;

public interface INotificationConfigService {
    boolean update(NotificationConfig config);
    List<NotificationConfig> getConfigsByUser(int userId);
    boolean insertUserKeyword(int userId, String keyword);
}

