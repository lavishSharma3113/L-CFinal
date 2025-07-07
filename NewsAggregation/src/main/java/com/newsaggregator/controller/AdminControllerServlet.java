package com.newsaggregator.controller;

import com.newsaggregator.model.ExternalServer;
import com.newsaggregator.model.NewsCategory;
import com.newsaggregator.service.IAdminService;
import com.newsaggregator.service.impl.AdminServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/admin")
public class AdminControllerServlet extends HttpServlet {
    private final IAdminService adminService = new AdminServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        switch (action) {
            case "listServers":
                List<ExternalServer> servers = adminService.getAllExternalServers();
                out.println("<h2>External Servers</h2>");
                for (ExternalServer s : servers) {
                    out.println("ID: " + s.getId() + " | " + s.getName() + " | Active: " + s.isActive() + "<br>");
                }
                break;

            case "viewServer":
                int serverId = Integer.parseInt(request.getParameter("serverId"));
                ExternalServer server = adminService.getExternalServerById(serverId);
                if (server != null) {
                    out.println("<h2>Server Details</h2>");
                    out.println("Name: " + server.getName() + "<br>");
                    out.println("Base URL: " + server.getBaseUrl() + "<br>");
                    out.println("API Key: " + server.getApiKey() + "<br>");
                    out.println("Active: " + server.isActive() + "<br>");
                    out.println("Last Accessed: " + server.getLastAccessed() + "<br>");
                } else {
                    out.println("Server not found.");
                }
                break;

            default:
                out.println("Invalid action.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String action = request.getParameter("action");
        boolean success = false;

        if ("editServer".equals(action)) {
            ExternalServer server = new ExternalServer();
            server.setId(Integer.parseInt(request.getParameter("serverId")));
            server.setName(request.getParameter("name"));
            server.setBaseUrl(request.getParameter("baseUrl"));
            server.setApiKey(request.getParameter("apiKey"));
            server.setActive(Boolean.parseBoolean(request.getParameter("isActive")));
            success = adminService.updateExternalServer(server);
        } else if ("addCategory".equals(action)) {
            String name = request.getParameter("name");
            NewsCategory category = new NewsCategory();
            category.setName(name);
            success = adminService.addNewsCategory(category);
        }

        response.setContentType("text/plain");
        response.getWriter().write(success ? "Action completed successfully." : "Failed to process action.");
    }
}

