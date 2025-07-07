package com.newsaggregator.console;

import com.newsaggregator.model.NewsArticle;
import com.newsaggregator.model.Notification;
import com.newsaggregator.model.User;
import com.newsaggregator.service.impl.NewsArticleServiceImpl;
import com.newsaggregator.service.impl.NotificationServiceImpl;
import com.newsaggregator.service.impl.SavedArticleServiceImpl;
import com.newsaggregator.service.impl.UserServiceImpl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class ConsoleDashboard {
    private static final Scanner scanner = new Scanner(System.in);
    private static final UserServiceImpl userService = new UserServiceImpl();
    private static final NewsArticleServiceImpl newsService = new NewsArticleServiceImpl();
    private static final NotificationServiceImpl notificationService = new NotificationServiceImpl();
    private static final SavedArticleServiceImpl savedArticleService = new SavedArticleServiceImpl();

    public static void main(String[] args) {
        while (true) {
            System.out.println("==== Welcome to News Aggregator Console ====");
            System.out.println("1. Login");
            System.out.println("2. Signup");
            System.out.println("3. Exit");
            System.out.print("Enter choice: ");
            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1:
                    loginScreen();
                    break;
                case 2:
                    System.out.print("Enter username: ");
                    String username = scanner.nextLine();
                    System.out.print("Enter email: ");
                    String email = scanner.nextLine();
                    System.out.print("Enter password: ");
                    String password = scanner.nextLine();
                    User newUser = new User();
                    newUser.setUsername(username);
                    newUser.setEmail(email);
                    newUser.setPassword(password);
                    newUser.setRole("User");
                    if (userService.registerUser(newUser)) {
                        System.out.println("Signup successful! Please login.");
                    } else {
                        System.out.println("Signup failed! User may already exist.");
                    }
                    break;
                case 3:
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void loginScreen() {
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        if (userService.loginUser(email, password)) {
            User user = userService.getUser(email);
            if ("User".equals(user.getRole())) {
                showUserDashboard(user);
            } else {
                System.out.println("Admin login is not supported in this console version.");
            }
        } else {
            System.out.println("Login failed. Check email or password.");
        }
    }

    private static void showUserDashboard(User user) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mma");
        String date = dateFormat.format(new Date());
        String time = timeFormat.format(new Date());

        System.out.println("Welcome to the News Application, " + user.getUsername() + "!");
        System.out.println("Date: " + date);
        System.out.println("Time: " + time);
    }



    private static void printArticles(List<NewsArticle> articles) {
        if (articles.isEmpty()) {
            System.out.println("No articles found.");
            return;
        }
        for (int i = 0; i < articles.size(); i++) {
            NewsArticle a = articles.get(i);
            System.out.println(" " + (i + 1) + ". " + a.getTitle());
                    System.out.println("Source: " + a.getSource());
            System.out.println("URL: " + a.getUrl());
            System.out.println("Published: " + a.getPublishedAt());
        }
    }
}
