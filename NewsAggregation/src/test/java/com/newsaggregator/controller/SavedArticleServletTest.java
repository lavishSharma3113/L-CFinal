package com.newsaggregator.controller;

import com.newsaggregator.model.NewsArticle;
import com.newsaggregator.model.SavedArticle;
import com.newsaggregator.service.ISavedArticleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import javax.servlet.http.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SavedArticleServletTest {

    @InjectMocks
    private SavedArticleServlet servlet;

    @Mock
    private ISavedArticleService service;

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    private StringWriter responseWriter;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Inject mock service into servlet (field is final)
        java.lang.reflect.Field f1 = SavedArticleServlet.class.getDeclaredField("service");
        f1.setAccessible(true);
        f1.set(servlet, service);

        responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
    }

    @Test
    void testDoPost_saveSuccess() throws Exception {
        when(request.getParameter("articleId")).thenReturn("10");
        when(request.getParameter("userId")).thenReturn("2");
        when(service.saveArticle(any(SavedArticle.class))).thenReturn(true);

        servlet.doPost(request, response);

        verify(service).saveArticle(any(SavedArticle.class));
        assertTrue(responseWriter.toString().contains("\"status\":\"success\""));
        assertTrue(responseWriter.toString().contains("Article saved"));
    }

    @Test
    void testDoPost_saveFailure() throws Exception {
        when(request.getParameter("articleId")).thenReturn("10");
        when(request.getParameter("userId")).thenReturn("2");
        when(service.saveArticle(any(SavedArticle.class))).thenReturn(false);

        servlet.doPost(request, response);

        verify(service).saveArticle(any(SavedArticle.class));
        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        assertTrue(responseWriter.toString().contains("\"status\":\"failure\""));
        assertTrue(responseWriter.toString().contains("Could not save article"));
    }

    @Test
    void testDoGet_returnsSavedArticles() throws Exception {
        when(request.getParameter("userId")).thenReturn("5");
        List<NewsArticle> articles = List.of(new NewsArticle());
        when(service.getSavedByUser(5)).thenReturn(articles);

        servlet.doGet(request, response);

        verify(service).getSavedByUser(5);
        assertTrue(responseWriter.toString().contains("["));
    }

    @Test
    void testDoDelete_removeSuccess() throws Exception {
        when(request.getParameter("userId")).thenReturn("3");
        when(request.getParameter("articleId")).thenReturn("7");
        when(service.removeSavedArticle(3, 7)).thenReturn(true);

        servlet.doDelete(request, response);

        verify(service).removeSavedArticle(3, 7);
        verify(response).setStatus(HttpServletResponse.SC_OK);
        assertTrue(responseWriter.toString().contains("\"status\":\"success\""));
        assertTrue(responseWriter.toString().contains("Article removed"));
    }

    @Test
    void testDoDelete_removeFailure() throws Exception {
        when(request.getParameter("userId")).thenReturn("3");
        when(request.getParameter("articleId")).thenReturn("7");
        when(service.removeSavedArticle(3, 7)).thenReturn(false);

        servlet.doDelete(request, response);

        verify(service).removeSavedArticle(3, 7);
        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        assertTrue(responseWriter.toString().contains("\"status\":\"failure\""));
        assertTrue(responseWriter.toString().contains("Could not delete article"));
    }
}