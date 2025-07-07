package com.newsaggregator.controller;

import com.google.gson.Gson;
import com.newsaggregator.model.ArticleReport;
import com.newsaggregator.model.NewsArticle;
import com.newsaggregator.service.INewsReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.http.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class ArticleReportServletTest {

    private ArticleReportServlet servlet;
    private INewsReportService reportService;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private StringWriter responseWriter;

    @BeforeEach
    void setup() throws Exception {
        reportService = mock(INewsReportService.class);
        servlet = new ArticleReportServlet() {

            protected INewsReportService getReportService() {
                return reportService;
            }
        };

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
    }

    @Test
    void doGet_shouldReturnReportedArticles() throws Exception {
        NewsArticle article = new NewsArticle();
        article.setTitle("Test Article");

        when(reportService.getAllReports()).thenReturn(List.of(article));

        servlet.doGet(request, response);

        verify(reportService).getAllReports();
        assertTrue(responseWriter.toString().contains("Test Article"));
    }

    @Test
    void doGet_whenException_shouldReturn500() throws Exception {
        when(reportService.getAllReports()).thenThrow(new RuntimeException("Error"));

        servlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        assertTrue(responseWriter.toString().contains("Error"));
    }

    @Test
    void doPost_withValidParams_shouldReturnSuccess() throws Exception {
        when(request.getParameter("articleId")).thenReturn("1");
        when(request.getParameter("userId")).thenReturn("5");
        when(reportService.saveReport(any(ArticleReport.class))).thenReturn(true);

        servlet.doPost(request, response);

        verify(reportService).saveReport(any(ArticleReport.class));
        assertTrue(responseWriter.toString().contains("\"status\":\"success\""));
    }

    @Test
    void doPost_withInvalidParams_shouldReturn400() throws Exception {
        when(request.getParameter("articleId")).thenReturn("abc"); // invalid int
        when(request.getParameter("userId")).thenReturn("5");

        servlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertTrue(responseWriter.toString().contains("Invalid articleId or userId"));
    }

    @Test
    void doPost_whenSaveFails_shouldReturnBadRequest() throws Exception {
        when(request.getParameter("articleId")).thenReturn("1");
        when(request.getParameter("userId")).thenReturn("5");
        when(reportService.saveReport(any())).thenReturn(false);

        servlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertTrue(responseWriter.toString().contains("Failed to report the article"));
    }

    @Test
    void doPost_whenException_shouldReturn500() throws Exception {
        when(request.getParameter("articleId")).thenReturn("1");
        when(request.getParameter("userId")).thenReturn("5");
        when(reportService.saveReport(any())).thenThrow(new RuntimeException("Unexpected"));

        servlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        assertTrue(responseWriter.toString().contains("Unexpected"));
    }
}
