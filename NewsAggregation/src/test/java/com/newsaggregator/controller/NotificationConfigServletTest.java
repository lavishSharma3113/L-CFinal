package com.newsaggregator.controller;

import com.newsaggregator.model.NotificationConfig;
import com.newsaggregator.service.INotificationConfigService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import javax.servlet.http.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationConfigServletTest {

    @InjectMocks
    private NotificationConfigServlet servlet;

    @Mock
    private INotificationConfigService service;

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    private StringWriter responseWriter;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Inject mock service into servlet (field is final)
        java.lang.reflect.Field f1 = NotificationConfigServlet.class.getDeclaredField("service");
        f1.setAccessible(true);
        f1.set(servlet, service);

        responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
    }

    @Test
    void testDoGet_returnsConfigsAsJson() throws Exception {
        when(request.getParameter("userId")).thenReturn("1");
        List<NotificationConfig> configs = List.of(new NotificationConfig());
        when(service.getConfigsByUser(1)).thenReturn(configs);

        servlet.doGet(request, response);

        verify(service).getConfigsByUser(1);
        assertTrue(responseWriter.toString().contains("["));
    }

    @Test
    void testDoPost_insertKeyword_success() throws Exception {
        when(request.getParameter("userId")).thenReturn("2");
        when(request.getParameter("keyword")).thenReturn("sports");
        when(service.insertUserKeyword(2, "sports")).thenReturn(true);

        servlet.doPost(request, response);

        verify(service).insertUserKeyword(2, "sports");
        assertTrue(responseWriter.toString().contains("\"success\": true"));
        assertTrue(responseWriter.toString().contains("\"action\": \"insertKeyword\""));
    }

    @Test
    void testDoPost_updateConfig_success() throws Exception {
        when(request.getParameter("userId")).thenReturn("3");
        when(request.getParameter("keyword")).thenReturn(""); // No keyword, so update config
        when(request.getParameter("categoryId")).thenReturn("5");
        when(request.getParameter("enabled")).thenReturn("true");
        when(service.update(any(NotificationConfig.class))).thenReturn(true);

        servlet.doPost(request, response);

        ArgumentCaptor<NotificationConfig> captor = ArgumentCaptor.forClass(NotificationConfig.class);
        verify(service).update(captor.capture());
        NotificationConfig config = captor.getValue();
        assertEquals(3, config.getUserId());
        assertEquals(5, config.getCategoryId());
        assertTrue(config.isEnabled());
        assertTrue(responseWriter.toString().contains("\"success\": true"));
        assertTrue(responseWriter.toString().contains("\"action\": \"updateConfig\""));
    }

    @Test
    void testDoPost_updateConfig_false() throws Exception {
        when(request.getParameter("userId")).thenReturn("4");
        when(request.getParameter("keyword")).thenReturn(""); // No keyword, so update config
        when(request.getParameter("categoryId")).thenReturn("7");
        when(request.getParameter("enabled")).thenReturn("false");
        when(service.update(any(NotificationConfig.class))).thenReturn(false);

        servlet.doPost(request, response);

        verify(service).update(any(NotificationConfig.class));
        assertTrue(responseWriter.toString().contains("\"success\": false"));
        assertTrue(responseWriter.toString().contains("\"action\": \"updateConfig\""));
    }
}