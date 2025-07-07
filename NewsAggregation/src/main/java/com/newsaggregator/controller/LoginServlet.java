package com.newsaggregator.controller;

import com.google.gson.Gson;
import com.newsaggregator.model.User;
import com.newsaggregator.service.IUserService;
import com.newsaggregator.service.impl.UserServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.Map;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private static final String APPLICATION_JSON = "application/json";
    private static final String UTF_8 = "UTF-8";

    private final IUserService userService = new UserServiceImpl();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        prepareJsonResponse(response);

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (email == null || password == null) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Email and password must be provided.");
            return;
        }

        try {
            User user = userService.loginUser(email, password);

            if (user != null) {
                HttpSession session = request.getSession();
                session.setAttribute("user", user);

                System.out.printf("[LoginServlet] User logged in: %s [%s]%n", user.getUsername(), user.getRole());
                writeJson(response, user);
            } else {
                sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid email or password.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexpected error occurred.");
        }
    }

    private void prepareJsonResponse(HttpServletResponse response) {
        response.setContentType(APPLICATION_JSON);
        response.setCharacterEncoding(UTF_8);
    }

    private void writeJson(HttpServletResponse response, Object data) throws IOException {
        response.getWriter().write(gson.toJson(data));
    }

    private void sendError(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.setStatus(statusCode);
        writeJson(response, Map.of("error", message));
    }
}
