package com.newsaggregator.controller;

import com.newsaggregator.exception.AuthenticationFailedException;
import com.newsaggregator.model.User;
import com.newsaggregator.service.IUserService;
import com.newsaggregator.service.impl.UserServiceImpl;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private final IUserService userService = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            if (userService.loginUser(email, password)) {
                User user = userService.getUser(email);
                HttpSession session = request.getSession();
                session.setAttribute("user", user);

                System.out.println("User logged in: " + user.getUsername() + " [" + user.getRole() + "]");

                // Return user object as JSON
                Gson gson = new Gson();
                String userJson = gson.toJson(user);
                response.getWriter().write(userJson);

            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                throw new AuthenticationFailedException();
            }

        } catch (AuthenticationFailedException e) {
            response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Unexpected error occurred.\"}");
        }
    }
}
