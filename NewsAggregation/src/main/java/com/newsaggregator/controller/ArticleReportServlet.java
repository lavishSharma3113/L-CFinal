package com.newsaggregator.controller;

import com.google.gson.Gson;
import com.newsaggregator.model.ArticleReport;
import com.newsaggregator.model.NewsArticle;
import com.newsaggregator.service.INewsReportService;
import com.newsaggregator.service.impl.NewsReportServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.*;

@WebServlet("/report")
public class ArticleReportServlet extends HttpServlet {

    private static final String CONTENT_TYPE_JSON = "application/json";
    private static final String CHARACTER_ENCODING = "UTF-8";

    private final INewsReportService reportService = new NewsReportServiceImpl();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        prepareJsonResponse(resp);
        try {
            List<NewsArticle> articles = reportService.getAllReports();
            writeJson(resp, articles);
        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        prepareJsonResponse(resp);

        try {
            int articleId = Integer.parseInt(req.getParameter("articleId"));
            int userId = Integer.parseInt(req.getParameter("userId"));

            ArticleReport report = new ArticleReport();
            report.setArticleId(articleId);
            report.setUserId(userId);

            boolean success = reportService.saveReport(report);

            if (success) {
                writeJson(resp, Map.of("status", "success", "message", "Article removed."));
            } else {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Failed to report the article.");
            }

        } catch (NumberFormatException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid articleId or userId");
        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private void prepareJsonResponse(HttpServletResponse resp) {
        resp.setContentType(CONTENT_TYPE_JSON);
        resp.setCharacterEncoding(CHARACTER_ENCODING);
    }

    private void writeJson(HttpServletResponse resp, Object data) throws IOException {
        resp.getWriter().write(gson.toJson(data));
    }

    private void sendError(HttpServletResponse resp, int status, String message) throws IOException {
        resp.setStatus(status);
        writeJson(resp, Map.of("status", "error", "message", message));
    }
}
