package com.newsaggregator.controller;
import com.google.gson.Gson;
import com.newsaggregator.model.NewsArticle;
import com.newsaggregator.model.Notification;
import com.newsaggregator.service.INotificationService;
import com.newsaggregator.service.impl.NotificationServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/notifications")
public class NotificationServlet extends HttpServlet {
    private final INotificationService service = new NotificationServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        int userId = Integer.parseInt(request.getParameter("userId"));
        List<NewsArticle> notifications = service.getUserNotifications(userId);

        service.updateLastNotificationSeen(userId);

        String json = new Gson().toJson(notifications);
        response.getWriter().write(json);
    }

}
