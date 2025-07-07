package com.newsaggregator.controller;

import com.google.gson.Gson;
import com.newsaggregator.model.NewsArticle;
import com.newsaggregator.service.INewsArticleService;
import com.newsaggregator.service.impl.NewsArticleServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/news")
public class NewsArticleServlet extends HttpServlet {
    private final INewsArticleService service = new NewsArticleServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        List<NewsArticle> articles;

        try {
            switch (action != null ? action : "all") {
                case "today":
                    articles = service.getArticlesByToday();
                    break;
                case "keyword":
                    String keyword = req.getParameter("keyword");
                    articles = service.getArticlesByKeyword(keyword);
                    break;
                case "range":
                    String start = req.getParameter("start");
                    String end = req.getParameter("end");
                    String category = req.getParameter("category");
                    articles = service.getArticlesByDateRange(start, end, category);
                    break;
                default:
                    articles = service.getAllArticles();
                    break;
            }

            Gson gson = new Gson();
            String json = gson.toJson(articles);

            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write(json);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}