package com.newsaggregator.controller;

import com.google.gson.Gson;
import com.newsaggregator.model.NewsArticle;
import com.newsaggregator.model.NewsCategory;
import com.newsaggregator.service.IArticleReactionService;
import com.newsaggregator.service.INewsArticleService;
import com.newsaggregator.service.INewsCategoryService;
import com.newsaggregator.service.impl.ArticleReactionServiceImpl;
import com.newsaggregator.service.impl.NewsArticleCategoryImpl;
import com.newsaggregator.service.impl.NewsArticleServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/news")
public class NewsArticleServlet extends HttpServlet {

    private static final String APPLICATION_JSON = "application/json";
    private static final String UTF_8 = "UTF-8";

    private final INewsArticleService articleService = new NewsArticleServiceImpl();
    private final INewsCategoryService categoryService = new NewsArticleCategoryImpl();
    private final IArticleReactionService reactionService = new ArticleReactionServiceImpl();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        prepareJsonResponse(resp);
        String action = req.getParameter("action");

        try {
            switch (action != null ? action : "all") {
                case "today" -> handleTodayArticles(req, resp);
                case "keyword" -> handleKeywordArticles(req, resp);
                case "range" -> handleDateRangeArticles(req, resp);
                case "categories" -> handleCategoryList(resp);
                case "mostLiked" -> handleMostLikedArticles(resp);
                case "all" -> handleAllArticles(req, resp);
                default -> sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid action.");
            }
        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        prepareJsonResponse(resp);
        String action = req.getParameter("action");

        try {
            int userId = Integer.parseInt(req.getParameter("userId"));
            int articleId = Integer.parseInt(req.getParameter("articleId"));
            boolean success = false;

            if ("like".equalsIgnoreCase(action)) {
                success = reactionService.saveReaction(userId, articleId, "like");
            } else if ("dislike".equalsIgnoreCase(action)) {
                success = reactionService.saveReaction(userId, articleId, "dislike");
            } else {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid action type.");
                return;
            }

            writeJson(resp, Map.of("message", success ? "Success" : "Failed"));

        } catch (NumberFormatException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid articleId or userId.");
        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    // ===== Helper Methods =====

    private void prepareJsonResponse(HttpServletResponse resp) {
        resp.setContentType(APPLICATION_JSON);
        resp.setCharacterEncoding(UTF_8);
    }

    private void writeJson(HttpServletResponse resp, Object data) throws IOException {
        resp.getWriter().write(gson.toJson(data));
    }

    private void sendError(HttpServletResponse resp, int status, String message) throws IOException {
        resp.setStatus(status);
        writeJson(resp, Map.of("error", message));
    }

    private void handleTodayArticles(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int userId = Integer.parseInt(req.getParameter("userId"));
        List<NewsArticle> articles = articleService.getArticlesByToday(userId);
        writeJson(resp, articles);
    }

    private void handleKeywordArticles(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String keyword = req.getParameter("keyword");
        List<NewsArticle> articles = articleService.getArticlesByKeyword(keyword);
        writeJson(resp, articles);
    }

    private void handleDateRangeArticles(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String start = req.getParameter("start");
        String end = req.getParameter("end");
        String category = req.getParameter("category");
        List<NewsArticle> articles = articleService.getArticlesByDateRange(start, end, category);
        writeJson(resp, articles);
    }

    private void handleCategoryList(HttpServletResponse resp) throws IOException {
        List<NewsCategory> categories = categoryService.getAllNewsCategories();
        writeJson(resp, categories);
    }

    private void handleMostLikedArticles(HttpServletResponse resp) throws IOException {
        List<NewsArticle> articles = articleService.findMostLiked();
        writeJson(resp, articles);
    }

    private void handleAllArticles(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String start = req.getParameter("start");
        String end = req.getParameter("end");
        int userId = Integer.parseInt(req.getParameter("userId"));
        List<NewsArticle> articles = articleService.getAllArticles(start, end, userId);
        writeJson(resp, articles);
    }
}
