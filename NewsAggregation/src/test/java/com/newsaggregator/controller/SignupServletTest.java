package com.newsaggregator.controller;

import com.newsaggregator.model.User;
import com.newsaggregator.service.IUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import javax.servlet.http.*;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SignupServletTest {

    @InjectMocks
    private SignupServlet servlet;

    @Mock
    private IUserService userService;

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    private StringWriter responseWriter;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Inject mock service into servlet (field is final)
        java.lang.reflect.Field f1 = SignupServlet.class.getDeclaredField("userService");
        f1.setAccessible(true);
        f1.set(servlet, userService);

        responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
    }

    @Test
    void testDoGet_signupSuccess() throws Exception {
        when(request.getParameter("username")).thenReturn("testuser");
        when(request.getParameter("email")).thenReturn("test@example.com");
        when(request.getParameter("password")).thenReturn("pass123");
        when(userService.registerUser(any(User.class))).thenReturn(true);

        servlet.doGet(request, response);

        verify(userService).registerUser(any(User.class));
        assertTrue(responseWriter.toString().contains("\"status\":\"success\""));
        assertTrue(responseWriter.toString().contains("Signup successful"));
    }

    @Test
    void testDoGet_signupFailure() throws Exception {
        when(request.getParameter("username")).thenReturn("testuser");
        when(request.getParameter("email")).thenReturn("test@example.com");
        when(request.getParameter("password")).thenReturn("pass123");
        when(userService.registerUser(any(User.class))).thenReturn(false);

        servlet.doGet(request, response);

        verify(userService).registerUser(any(User.class));
        assertTrue(responseWriter.toString().contains("\"status\":\"failure\""));
        assertTrue(responseWriter.toString().contains("User already exists"));
    }
}