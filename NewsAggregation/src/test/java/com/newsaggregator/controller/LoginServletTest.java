package com.newsaggregator.controller;

import com.google.gson.Gson;
import com.newsaggregator.model.User;
import com.newsaggregator.service.IUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.http.*;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class LoginServletTest {

    private LoginServlet servlet;
    private IUserService mockUserService;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private StringWriter responseWriter;

    @BeforeEach
    void setup() throws Exception {
        mockUserService = mock(IUserService.class);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        responseWriter = new StringWriter();

        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
        when(request.getSession()).thenReturn(session);

        servlet = new LoginServlet() {
            @Override
            public void init() {
                // do nothing
            }

            protected IUserService getUserService() {
                return mockUserService;
            }
        };
    }

    @Test
    void testDoGet_validCredentials_shouldLogin() throws Exception {
        User user = new User();
        user.setUsername("john");
        user.setRole("user");

        when(request.getParameter("email")).thenReturn("john@example.com");
        when(request.getParameter("password")).thenReturn("secret");
        when(mockUserService.loginUser("john@example.com", "secret")).thenReturn(user);

        servlet.doGet(request, response);

        verify(session).setAttribute("user", user);
        String output = responseWriter.toString();
        assertTrue(output.contains("john"));
        assertTrue(output.contains("user"));
    }

    @Test
    void testDoGet_missingEmailOrPassword_shouldReturn400() throws Exception {
        when(request.getParameter("email")).thenReturn(null);
        when(request.getParameter("password")).thenReturn("abc");

        servlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertTrue(responseWriter.toString().contains("must be provided"));
    }

    @Test
    void testDoGet_invalidCredentials_shouldReturn401() throws Exception {
        when(request.getParameter("email")).thenReturn("invalid@example.com");
        when(request.getParameter("password")).thenReturn("wrong");
        when(mockUserService.loginUser("invalid@example.com", "wrong")).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        assertTrue(responseWriter.toString().contains("Invalid email or password"));
    }

    @Test
    void testDoGet_exception_shouldReturn500() throws Exception {
        when(request.getParameter("email")).thenReturn("crash@example.com");
        when(request.getParameter("password")).thenReturn("boom");
        when(mockUserService.loginUser(any(), any())).thenThrow(new RuntimeException("DB failed"));

        servlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        assertTrue(responseWriter.toString().contains("Unexpected error occurred"));
    }
}
