package com.newsaggregator.service.impl;

import com.newsaggregator.dao.NotificationConfigDAO;
import com.newsaggregator.model.NotificationConfig;
import com.newsaggregator.service.INotificationConfigService;

import java.util.List;

public class NotificationConfigServiceImpl implements INotificationConfigService {
    private final NotificationConfigDAO dao = new NotificationConfigDAO();

    public boolean update(NotificationConfig config) {
        return dao.saveOrUpdate(config);
    }

    public List<NotificationConfig> getConfigsByUser(int userId) {
        return dao.getConfigsByUser(userId);
    }
}

