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
    void doPost_withInvalidParams_shouldReturn400() throws Exception {
        when(request.getParameter("articleId")).thenReturn("abc"); // invalid int
        when(request.getParameter("userId")).thenReturn("5");

        servlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertTrue(responseWriter.toString().contains("Invalid articleId or userId"));
    }


}
