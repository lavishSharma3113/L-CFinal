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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminControllerServletTest {

    @InjectMocks
    private AdminControllerServlet servlet;

    @Mock
    private IAdminService adminService;

    @Spy
    private Gson gson = new Gson();

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    private StringWriter responseWriter;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Inject mocks into servlet using reflection (fields are final)
        java.lang.reflect.Field f1 = AdminControllerServlet.class.getDeclaredField("adminService");
        f1.setAccessible(true);
        f1.set(servlet, adminService);
        java.lang.reflect.Field f2 = AdminControllerServlet.class.getDeclaredField("gson");
        f2.setAccessible(true);
        f2.set(servlet, gson);

        responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
    }

    @Test
    void testDoGet_listServers() throws Exception {
        when(request.getParameter("action")).thenReturn("listServers");
        List<ExternalServer> servers = List.of(new ExternalServer());
        when(adminService.getAllExternalServers()).thenReturn(servers);

        servlet.doGet(request, response);

        verify(adminService).getAllExternalServers();
        assertTrue(responseWriter.toString().contains("["));
    }

    @Test
    void testDoGet_viewServer_found() throws Exception {
        when(request.getParameter("action")).thenReturn("viewServer");
        when(request.getParameter("serverId")).thenReturn("1");
        ExternalServer server = new ExternalServer();
        when(adminService.getExternalServerById(1)).thenReturn(server);

        servlet.doGet(request, response);

        verify(adminService).getExternalServerById(1);
        assertTrue(responseWriter.toString().contains("{"));
    }

    @Test
    void testDoGet_viewServer_notFound() throws Exception {
        when(request.getParameter("action")).thenReturn("viewServer");
        when(request.getParameter("serverId")).thenReturn("2");
        when(adminService.getExternalServerById(2)).thenReturn(null);

        servlet.doGet(request, response);

        verify(adminService).getExternalServerById(2);
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        assertTrue(responseWriter.toString().contains("Server not found"));
    }

    @Test
    void testDoGet_invalidAction() throws Exception {
        when(request.getParameter("action")).thenReturn("invalid");

        servlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertTrue(responseWriter.toString().contains("Invalid action"));
    }

    @Test
    void testDoPost_editServer_success() throws Exception {
        when(request.getParameter("action")).thenReturn("editServer");
        ExternalServer server = new ExternalServer();
        String json = gson.toJson(server);
        BufferedReader reader = new BufferedReader(new StringReader(json));
        when(request.getReader()).thenReturn(reader);
        when(adminService.updateExternalServer(any(ExternalServer.class))).thenReturn(true);

        servlet.doPost(request, response);

        verify(adminService).updateExternalServer(any(ExternalServer.class));
        assertTrue(responseWriter.toString().contains("success"));
    }

    @Test
    void testDoPost_addCategory_success() throws Exception {
        when(request.getParameter("action")).thenReturn("addCategory");
        when(request.getParameter("name")).thenReturn("Tech");
        when(adminService.addNewsCategory(any(NewsCategory.class))).thenReturn(true);

        servlet.doPost(request, response);

        verify(adminService).addNewsCategory(any(NewsCategory.class));
        assertTrue(responseWriter.toString().contains("success"));
    }

    @Test
    void testDoPost_hideArticle_success() throws Exception {
        when(request.getParameter("action")).thenReturn("hideArticle");
        when(request.getParameter("articleId")).thenReturn("5");
        when(adminService.hideArticle(5)).thenReturn(true);

        servlet.doPost(request, response);

        verify(adminService).hideArticle(5);
        assertTrue(responseWriter.toString().contains("success"));
    }

    @Test
    void testDoPost_hideArticleWithKeyword_success() throws Exception {
        when(request.getParameter("action")).thenReturn("hideArticleWithKeyword");
        when(request.getParameter("keyword")).thenReturn("politics");
        when(adminService.hideArticle("politics")).thenReturn(true);

        servlet.doPost(request, response);

        verify(adminService).hideArticle("politics");
        assertTrue(responseWriter.toString().contains("success"));
    }

    @Test
    void testDoPost_hideCategory_success() throws Exception {
        when(request.getParameter("action")).thenReturn("hideCategory");
        when(request.getParameter("categoryId")).thenReturn("3");
        when(request.getParameter("isHidden")).thenReturn("true");
        when(adminService.hideCategory(3, true)).thenReturn(true);

        servlet.doPost(request, response);

        verify(adminService).hideCategory(3, true);
        assertTrue(responseWriter.toString().contains("success"));
    }

    @Test
    void testDoPost_invalidAction() throws Exception {
        when(request.getParameter("action")).thenReturn("foo");

        servlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertTrue(responseWriter.toString().contains("Invalid action"));
    }

    @Test
    void testDoPost_actionFailed() throws Exception {
        when(request.getParameter("action")).thenReturn("hideArticle");
        when(request.getParameter("articleId")).thenReturn("5");
        when(adminService.hideArticle(5)).thenReturn(false);

        servlet.doPost(request, response);

        verify(adminService).hideArticle(5);
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertTrue(responseWriter.toString().contains("Action failed"));
    }
}