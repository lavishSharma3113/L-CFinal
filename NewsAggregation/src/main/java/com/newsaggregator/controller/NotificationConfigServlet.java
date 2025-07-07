package com.newsaggregator.controller;

import com.newsaggregator.model.NotificationConfig;
import com.newsaggregator.service.INotificationConfigService;
import com.newsaggregator.service.impl.NotificationConfigServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/notification-config")
public class NotificationConfigServlet extends HttpServlet {
    private final INotificationConfigService service = new NotificationConfigServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int userId = Integer.parseInt(request.getParameter("userId"));
        List<NotificationConfig> configs = service.getConfigsByUser(userId);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int userId = Integer.parseInt(request.getParameter("userId"));
        int categoryId = Integer.parseInt(request.getParameter("categoryId"));
        boolean isEnabled = Boolean.parseBoolean(request.getParameter("enabled"));

        NotificationConfig config = new NotificationConfig();
        config.setUserId(userId);
        config.setCategoryId(categoryId);
        config.setEnabled(isEnabled);

        boolean success = service.update(config);

    }
}

