package com.newsaggregator.controller;

import com.google.gson.Gson;
import com.newsaggregator.model.User;
import com.newsaggregator.service.IUserService;
import com.newsaggregator.service.impl.UserServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/signup")
public class SignupServlet extends HttpServlet {
    private final IUserService userService = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole("User");

        Map<String, String> result = new HashMap<>();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        if (userService.registerUser(user)) {
            result.put("status", "success");
            result.put("message", "Signup successful.");
        } else {
            result.put("status", "failure");
            result.put("message", "User already exists or input is invalid.");
        }

        String json = new Gson().toJson(result);
        response.getWriter().write(json);
    }
}
