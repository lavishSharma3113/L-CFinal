package com.newsaggregator.controller;
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        int userId = (int) session.getAttribute("userId");
        List<Notification> notifications = service.getUserNotifications(userId);


    }
}
