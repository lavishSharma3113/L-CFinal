package com.newsaggregator.controller;

import com.google.gson.Gson;
import com.newsaggregator.model.NewsArticle;
import com.newsaggregator.model.SavedArticle;
import com.newsaggregator.service.ISavedArticleService;
import com.newsaggregator.service.impl.SavedArticleServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/saved")
public class SavedArticleServlet extends HttpServlet {
    private final ISavedArticleService service = new SavedArticleServiceImpl();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int articleId = Integer.parseInt(request.getParameter("articleId"));
        int userId = Integer.parseInt(request.getParameter("userId")); // Now passed directly

        SavedArticle saved = new SavedArticle();
        saved.setUserId(userId);
        saved.setArticleId(articleId);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        if (service.saveArticle(saved)) {
            response.getWriter().write("{\"status\":\"success\", \"message\":\"Article saved.\"}");
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"status\":\"failure\", \"message\":\"Could not save article.\"}");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int userId = Integer.parseInt(request.getParameter("userId"));

        List<NewsArticle> list = service.getSavedByUser(userId);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String json = new Gson().toJson(list);
        response.getWriter().write(json);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int userId = Integer.parseInt(request.getParameter("userId"));
        int articleId = Integer.parseInt(request.getParameter("articleId"));

        if (service.removeSavedArticle(userId, articleId)) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("{\"status\":\"success\", \"message\":\"Article removed.\"}");
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"status\":\"failure\", \"message\":\"Could not delete article.\"}");
        }
    }
}
