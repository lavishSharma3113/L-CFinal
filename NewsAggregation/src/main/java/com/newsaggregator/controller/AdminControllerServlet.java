package com.newsaggregator.controller;

import com.google.gson.Gson;
import com.newsaggregator.model.ExternalServer;
import com.newsaggregator.model.NewsCategory;
import com.newsaggregator.service.IAdminService;
import com.newsaggregator.service.impl.AdminServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/admin")
public class AdminControllerServlet extends HttpServlet {
    private static final String ACTION = "action";
    private static final Gson gson = new Gson();
    private final IAdminService adminService = new AdminServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String action = request.getParameter(ACTION);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            switch (action) {
                case "listServers" -> handleListServers(response);
                case "viewServer" -> handleViewServer(request, response);
                default -> respondWithError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
            }
        } catch (Exception e) {
            respondWithError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String action = request.getParameter(ACTION);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            boolean success = switch (action) {
                case "editServer" -> handleEditServer(request);
                case "addCategory" -> handleAddCategory(request);
                case "hideArticle" -> handleHideArticleById(request);
                case "hideArticleWithKeyword" -> handleHideArticleByKeyword(request);
                case "hideCategory" -> handleHideCategory(request);
                default -> {
                    respondWithError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
                    yield false;
                }
            };

            if (success) {
                respondWithJson(response, Map.of("status", "success", "message", "Action completed successfully."));
            } else {
                respondWithError(response, HttpServletResponse.SC_BAD_REQUEST, "Action failed.");
            }

        } catch (Exception e) {
            respondWithError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }


    private void handleListServers(HttpServletResponse response) throws IOException {
        List<ExternalServer> servers = adminService.getAllExternalServers();
        respondWithJson(response, servers);
    }

    private void handleViewServer(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int id = Integer.parseInt(request.getParameter("serverId"));
        ExternalServer server = adminService.getExternalServerById(id);

        if (server != null) {
            respondWithJson(response, server);
        } else {
            respondWithError(response, HttpServletResponse.SC_NOT_FOUND, "Server not found");
        }
    }

    private boolean handleEditServer(HttpServletRequest request) throws IOException {
        try (BufferedReader reader = request.getReader()) {
            ExternalServer server = gson.fromJson(reader, ExternalServer.class);
            return adminService.updateExternalServer(server);
        }
    }

    private boolean handleAddCategory(HttpServletRequest request) {
        String name = request.getParameter("name");
        NewsCategory category = new NewsCategory();
        category.setName(name);
        return adminService.addNewsCategory(category);
    }

    private boolean handleHideArticleById(HttpServletRequest request) {
        int articleId = Integer.parseInt(request.getParameter("articleId"));
        return adminService.hideArticle(articleId);
    }

    private boolean handleHideArticleByKeyword(HttpServletRequest request) {
        String keyword = request.getParameter("keyword");
        return adminService.hideArticle(keyword);
    }

    private boolean handleHideCategory(HttpServletRequest request) {
        int categoryId = Integer.parseInt(request.getParameter("categoryId"));
        boolean isHidden = Boolean.parseBoolean(request.getParameter("isHidden"));
        return adminService.hideCategory(categoryId, isHidden);
    }


    private void respondWithJson(HttpServletResponse response, Object data) throws IOException {
        response.getWriter().write(gson.toJson(data));
    }

    private void respondWithError(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.setStatus(statusCode);
        Map<String, String> error = new HashMap<>();
        error.put("status", "error");
        error.put("message", message);
        respondWithJson(response, error);
    }
}
