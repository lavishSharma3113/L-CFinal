package com.newsaggregator.client.console;

import com.newsaggregator.client.model.User;
import com.newsaggregator.client.service.AuthClient;

import java.util.Scanner;

public class ConsoleApp {
    private static final Scanner scanner = new Scanner(System.in);
    private static final AuthClient authClient = new AuthClient();
    private static final NewsConsoleApp newsConsole = new NewsConsoleApp();
    private static final SavedArticleConsoleApp savedConsole = new SavedArticleConsoleApp();

    public static void main(String[] args) {
        User user = null;

        while (user == null) {
            System.out.println("==== Welcome to News Aggregator Console ====");
            System.out.println("1. Login");
            System.out.println("2. Signup");
            System.out.print("Choose: ");
            int choice = Integer.parseInt(scanner.nextLine());

            if (choice == 1) {
                System.out.print("Email: ");
                String email = scanner.nextLine();
                System.out.print("Password: ");
                String password = scanner.nextLine();
                try {
                    user = authClient.login(email, password);
                    System.out.println("Welcome, " + user.getUsername());
                } catch (Exception e) {
                    System.out.println("Login failed: " + e.getMessage());
                }
            } else if (choice == 2) {
                System.out.print("Username: ");
                String username = scanner.nextLine();
                System.out.print("Email: ");
                String email = scanner.nextLine();
                System.out.print("Password: ");
                String password = scanner.nextLine();
                if (authClient.register(username, email, password)) {
                    System.out.println("Signup successful! Please login.");
                } else {
                    System.out.println("Signup failed!");
                }
            }
        }

        boolean exit = false;
        while (!exit) {
            System.out.println("\n==== Dashboard ====");
            System.out.println("1. View Headlines");
            System.out.println("2. Saved Articles");
            System.out.println("3. Logout");
            System.out.print("Choose: ");
            int opt = Integer.parseInt(scanner.nextLine());

            switch (opt) {
                case 1:
                    newsConsole.showTodayHeadlines(user.getId());
                    break;
                case 2:
                    savedConsole.showSavedArticles(user.getId());
                    break;
                case 3:
                    System.out.println("Goodbye!");
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }
}