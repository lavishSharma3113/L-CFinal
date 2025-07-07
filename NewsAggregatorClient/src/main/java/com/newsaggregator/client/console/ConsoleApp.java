package com.newsaggregator.client.console;

import com.newsaggregator.client.model.User;
import com.newsaggregator.client.service.AuthClient;
import com.newsaggregator.client.service.NewsClient;

import java.util.Scanner;

public class ConsoleApp {
    private static final Scanner scanner = new Scanner(System.in);
    private static final AuthClient authClient = new AuthClient();
    private static final NewsConsoleApp newsConsole = new NewsConsoleApp();
    private static final SavedArticleConsoleApp savedConsole = new SavedArticleConsoleApp();
    private static final NotificationConsoleApp notificationConsole = new NotificationConsoleApp();
    private static final NewsClient newsClient = new NewsClient();

    public static void main(String[] args) {
        try {
            User user = authenticateUser();

            if (user != null) {
                if (user.getRole().equalsIgnoreCase("Admin")) {
                    new AdminConsoleApp().launch();
                } else {
                    showUserDashboard(user);
                }
            }

        } catch (Exception e) {
            System.out.println("Unexpected error occurred: " + e.getMessage());
        }
    }

    private static User authenticateUser() {
        User user = null;

        while (user == null) {
            System.out.println("==== Welcome to News Aggregator Console ====");
            System.out.println("1. Login");
            System.out.println("2. Signup");
            int choice = readInt("Choose: ");

            switch (choice) {
                case 1 -> user = handleLogin();
                case 2 -> handleSignup();
                default -> System.out.println("Invalid option. Please try again.");
            }
        }

        return user;
    }

    private static User handleLogin() {
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        try {
            User user = authClient.login(email, password);
            System.out.println("Welcome, " + user.getUsername() + " (User ID: " + user.getId() + ")");
            return user;
        } catch (Exception e) {
            if (e.getMessage().contains("Connection refused")) {
                System.out.println("Server is down. Please try again later.");
            } else {
                System.out.println("Login failed: Incorrect email or password.");
            }
            return null;
        }
    }

    private static void handleSignup() {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        boolean success = authClient.register(username, email, password);
        System.out.println(success ? "Signup successful! Please login." : "Signup failed!");
    }

    private static void showUserDashboard(User user) {
        boolean exit = false;

        while (!exit) {
            printDashboardMenu();
            int option = readInt("Choose: ");

            switch (option) {
                case 1 -> newsConsole.showHeadlineMenu(user.getId());
                case 2 -> savedConsole.showSavedArticles(user.getId());
                case 3 -> newsConsole.showNewsByKeyword(user.getId());
                case 4 -> notificationConsole.launch(user.getId());
                case 5 -> {
                    System.out.println("Logging out...");
                    exit = true;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private static void printDashboardMenu() {
        System.out.println("\n==== User Dashboard ====");
        System.out.println("1. View Headlines");
        System.out.println("2. Saved Articles");
        System.out.println("3. Search News");
        System.out.println("4. Notifications");
        System.out.println("5. Logout");
    }

    private static int readInt(String prompt) {
        System.out.print(prompt);
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return readInt(prompt);
        }
    }
}
