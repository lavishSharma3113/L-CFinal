package com.newsaggregator.controller;

import com.google.gson.Gson;
import com.newsaggregator.model.NewsArticle;
import com.newsaggregator.service.INotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import javax.servlet.http.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationServletTest {

    @InjectMocks
    private NotificationServlet servlet;

    @Mock
    private INotificationService service;

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    private StringWriter responseWriter;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Inject mock service into servlet (field is final)
        java.lang.reflect.Field f1 = NotificationServlet.class.getDeclaredField("service");
        f1.setAccessible(true);
        f1.set(servlet, service);

        responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
    }

    @Test
    void testDoGet_returnsNotificationsAndUpdatesSeen() throws Exception {
        when(request.getParameter("userId")).thenReturn("1");
        List<NewsArticle> notifications = List.of(new NewsArticle());
        when(service.getUserNotifications(1)).thenReturn(notifications);

        servlet.doGet(request, response);

        verify(service).getUserNotifications(1);
        verify(service).updateLastNotificationSeen(1);
        assertTrue(responseWriter.toString().contains("["));
    }
}