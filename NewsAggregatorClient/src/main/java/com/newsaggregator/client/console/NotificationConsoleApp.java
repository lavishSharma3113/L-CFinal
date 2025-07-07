package com.newsaggregator.client.console;

import com.newsaggregator.client.model.NewsArticle;
import com.newsaggregator.client.model.NotificationConfig;
import com.newsaggregator.client.service.NewsClient;
import com.newsaggregator.client.service.NotificationClient;
import com.newsaggregator.client.service.NotificationConfigClient;

import java.util.List;
import java.util.Scanner;

public class NotificationConsoleApp {
    private final NotificationClient notificationClient = new NotificationClient();
    private final NewsClient newsClient = new NewsClient();
    private final NotificationConfigClient configClient = new NotificationConfigClient();
    private final Scanner scanner = new Scanner(System.in);

    public void launch(int userId) {
        boolean back = false;
        while (!back) {
            printMenu();
            int choice = readInt("Choose: ");

            switch (choice) {
                case 1 -> showNotifications(userId);
                case 2 -> configureNotifications(userId);
                case 3 -> back = true;
                case 4 -> logout();
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void printMenu() {
        System.out.println("\n--- Notification Menu ---");
        System.out.println("1. View Notifications");
        System.out.println("2. Configure Notifications");
        System.out.println("3. Back");
        System.out.println("4. Logout");
    }

    private void showNotifications(int userId) {
        List<NewsArticle> notifications = notificationClient.fetchUserNotifications(userId);

        if (notifications == null || notifications.isEmpty()) {
            System.out.println("You have no notifications.");
            return;
        }

        System.out.println("\n--- Your Notifications ---");
        for (NewsArticle n : notifications) {
            System.out.printf("- %s [%s] %s • New%n", n.getTitle(), n.getPublishedAt(), n.getContent());
        }

        System.out.println("(All notifications marked as read.)");
    }

    private void configureNotifications(int userId) {
        List<NotificationConfig> configs = newsClient.getNotificationCategories(userId);

        if (configs == null || configs.isEmpty()) {
            System.out.println("No categories available.");
            return;
        }

        printNotificationConfigs(configs);

        int choice = readInt("Choose a category to toggle (or option): ");
        if (choice >= 1 && choice <= configs.size()) {
            handleConfigToggle(userId, configs.get(choice - 1));
        } else if (choice == configs.size() + 1) {
            return;
        } else if (choice == configs.size() + 2) {
            logout();
        } else {
            System.out.println("Invalid option.");
        }
    }

    private void printNotificationConfigs(List<NotificationConfig> configs) {
        System.out.println("\n--- Configure Notifications ---");
        for (int i = 0; i < configs.size(); i++) {
            NotificationConfig config = configs.get(i);
            String status = config.isEnabled() ? "Enabled" : "Disabled";
            System.out.printf("%d. %s - %s%n", i + 1, config.getCategoryName(), status);
        }
        System.out.printf("%d. Back%n", configs.size() + 1);
        System.out.printf("%d. Logout%n", configs.size() + 2);
    }

    private void handleConfigToggle(int userId, NotificationConfig config) {
        if ("keyword".equalsIgnoreCase(config.getCategoryName())) {
            String keyword = readLine("Enter the keyword for notification: ");
            boolean success = configClient.insertUserKeyword(userId, keyword);
            System.out.println(success ? "Keyword added for notification." : "Failed to add keyword.");
        } else {
            boolean newStatus = !config.isEnabled();
            config.setEnabled(newStatus);
            boolean updated = configClient.updateConfig(userId, config.getCategoryId(), newStatus);
            System.out.println(updated
                    ? "Updated " + config.getCategoryName() + " to " + (newStatus ? "Enabled" : "Disabled")
                    : "Failed to update preference.");
        }
    }

    private void logout() {
        System.out.println("Logging out...");
        System.exit(0);
    }

    // Utility Methods
    private int readInt(String prompt) {
        System.out.print(prompt);
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException ex) {
            System.out.println("Invalid number. Please try again.");
            return readInt(prompt);
        }
    }

    private String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }
}
