package com.newsaggregator.controller;

import com.google.gson.Gson;
import com.newsaggregator.model.ExternalServer;
import com.newsaggregator.model.NewsCategory;
import com.newsaggregator.service.IAdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import javax.servlet.http.*;

import java.io.*;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class AdminControllerServletTest {

    @InjectMocks
    private AdminControllerServlet servlet;

    @Mock
    private IAdminService adminService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private StringWriter responseWriter;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
    }

    // --- doGet Tests ---

    @Test
    void testDoGet_listServers_shouldReturnJson() throws Exception {
        when(request.getParameter("action")).thenReturn("listServers");
        when(adminService.getAllExternalServers()).thenReturn(List.of(new ExternalServer()));

        servlet.doGet(request, response);

        verify(adminService).getAllExternalServers();
        assertTrue(responseWriter.toString().contains("["));
    }

    @Test
    void testDoGet_viewServer_shouldReturnServerJson() throws Exception {
        ExternalServer server = new ExternalServer();
        server.setId(1);
        server.setName("Test Server");

        when(request.getParameter("action")).thenReturn("viewServer");
        when(request.getParameter("serverId")).thenReturn("1");
        when(adminService.getExternalServerById(1)).thenReturn(server);

        servlet.doGet(request, response);

        verify(adminService).getExternalServerById(1);
        assertTrue(responseWriter.toString().contains("Test Server"));
    }

    @Test
    void testDoGet_invalidAction_shouldReturnError() throws Exception {
        when(request.getParameter("action")).thenReturn("invalid");

        servlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertTrue(responseWriter.toString().contains("Invalid action"));
    }

    // --- doPost Tests ---

    @Test
    void testDoPost_addCategory_shouldSucceed() throws Exception {
        when(request.getParameter("action")).thenReturn("addCategory");
        when(request.getParameter("name")).thenReturn("tech");
        when(adminService.addNewsCategory(any(NewsCategory.class))).thenReturn(true);

        servlet.doPost(request, response);

        assertTrue(responseWriter.toString().contains("success"));
    }

    @Test
    void testDoPost_editServer_shouldSucceed() throws Exception {
        when(request.getParameter("action")).thenReturn("editServer");

        String json = new Gson().toJson(new ExternalServer());
        BufferedReader reader = new BufferedReader(new StringReader(json));
        when(request.getReader()).thenReturn(reader);
        when(adminService.updateExternalServer(any())).thenReturn(true);

        servlet.doPost(request, response);

        assertTrue(responseWriter.toString().contains("success"));
    }

    @Test
    void testDoPost_hideArticle_shouldSucceed() throws Exception {
        when(request.getParameter("action")).thenReturn("hideArticle");
        when(request.getParameter("articleId")).thenReturn("5");
        when(adminService.hideArticle(5)).thenReturn(true);

        servlet.doPost(request, response);

        assertTrue(responseWriter.toString().contains("success"));
    }

    @Test
    void testDoPost_hideArticleWithKeyword_shouldSucceed() throws Exception {
        when(request.getParameter("action")).thenReturn("hideArticleWithKeyword");
        when(request.getParameter("keyword")).thenReturn("test");
        when(adminService.hideArticle("test")).thenReturn(true);

        servlet.doPost(request, response);

        assertTrue(responseWriter.toString().contains("success"));
    }

    @Test
    void testDoPost_hideCategory_shouldSucceed() throws Exception {
        when(request.getParameter("action")).thenReturn("hideCategory");
        when(request.getParameter("categoryId")).thenReturn("3");
        when(request.getParameter("isHidden")).thenReturn("true");
        when(adminService.hideCategory(3, true)).thenReturn(true);

        servlet.doPost(request, response);

        assertTrue(responseWriter.toString().contains("success"));
    }

    @Test
    void testDoPost_invalidAction_shouldReturnError() throws Exception {
        when(request.getParameter("action")).thenReturn("badAction");

        servlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertTrue(responseWriter.toString().contains("Invalid action"));
    }

    @Test
    void testDoPost_actionFails_shouldReturnFailure() throws Exception {
        when(request.getParameter("action")).thenReturn("hideArticle");
        when(request.getParameter("articleId")).thenReturn("1");
        when(adminService.hideArticle(1)).thenReturn(false);

        servlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertTrue(responseWriter.toString().contains("failure"));
    }
}
