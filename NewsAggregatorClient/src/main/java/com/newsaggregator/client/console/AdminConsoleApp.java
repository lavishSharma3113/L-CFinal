package com.newsaggregator.client.console;

import com.newsaggregator.client.model.ExternalServer;
import com.newsaggregator.client.model.NewsArticle;
import com.newsaggregator.client.model.NewsCategory;
import com.newsaggregator.client.service.AdminClient;
import com.newsaggregator.client.service.NewsClient;
import com.newsaggregator.client.service.NewsReportClient;

import java.util.List;
import java.util.Scanner;

public class AdminConsoleApp {
    private final AdminClient adminClient = new AdminClient();
    private final NewsReportClient reportClient = new NewsReportClient();
    private final NewsClient newsClient = new NewsClient();
    private final Scanner scanner = new Scanner(System.in);

    public void launch() {
        boolean logout = false;
        while (!logout) {
            printMenu();
            int choice = readInt("Enter option: ");
            switch (choice) {
                case 1 -> displayAllServers();
                case 2 -> displayServerDetails();
                case 3 -> updateServerApiKey();
                case 4 -> createNewsCategory();
                case 5 -> showReports();
                case 6 -> toggleCategoryVisibility();
                case 7 -> hideArticlesByKeyword();
                case 8 -> logout = true;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void printMenu() {
        System.out.println("\n=== Admin Dashboard ===");
        System.out.println("1. View All External Servers");
        System.out.println("2. View External Server Details");
        System.out.println("3. Update External Server API Key");
        System.out.println("4. Add News Category");
        System.out.println("5. View Reports");
        System.out.println("6. Hide/Unhide Category");
        System.out.println("7. Hide News by Keyword");
        System.out.println("8. Logout");
    }

    private void displayAllServers() {
        List<ExternalServer> servers = adminClient.getAllServers();
        for (ExternalServer s : servers) {
            System.out.printf("ID: %d | Name: %s | Status: %s | Last Accessed: %s%n",
                    s.getId(), s.getName(), s.isActive() ? "Active" : "Inactive", s.getLastAccessed());
        }
    }

    private void displayServerDetails() {
        List<ExternalServer> servers = adminClient.getAllServers();
        for (ExternalServer s : servers) {
            System.out.printf("Name: %s | API Key: %s%n", s.getName(), s.getApiKey());
        }
    }

    private void updateServerApiKey() {
        int id = readInt("Enter server ID to update API key: ");
        ExternalServer server = adminClient.getServerById(id);

        if (server == null) {
            System.out.println("Server not found.");
            return;
        }

        String newKey = readLine("Enter new API key: ");
        server.setApiKey(newKey);

        boolean success = adminClient.updateServer(server);
        System.out.println(success ? "API key updated successfully." : "API key update failed.");
    }

    private void createNewsCategory() {
        String name = readLine("Enter new category name: ");
        boolean success = adminClient.addCategory(name);
        System.out.println(success ? "Category added." : "Failed to add category.");
    }

    private void toggleCategoryVisibility() {
        List<NewsCategory> categories = newsClient.getAllNewsCategories();

        for (int i = 0; i < categories.size(); i++) {
            NewsCategory c = categories.get(i);
            System.out.printf("%d. %s\n   ID: %d | Hidden: %s%n",
                    i + 1, c.getName(), c.getId(), c.getIsHidden());
        }

        int selection = readInt("Select category number to Hide/Unhide (0 to go back): ");
        if (selection == 0) return;

        if (selection > 0 && selection <= categories.size()) {
            NewsCategory selected = categories.get(selection - 1);
            boolean updated = adminClient.hideCategory(selected.getId(), !selected.getIsHidden());
            System.out.println(updated ? "Category visibility toggled." : "Failed to update category.");
        } else {
            System.out.println("Invalid selection.");
        }
    }

    private void hideArticlesByKeyword() {
        String keyword = readLine("Enter the keyword: ");
        boolean success = adminClient.hideArticleKeyword(keyword);
        System.out.println(success ? "Articles hidden successfully." : "Failed to hide articles.");
    }

    private void showReports() {
        List<NewsArticle> articles = reportClient.getAllNewsReport();
        if (articles == null || articles.isEmpty()) {
            System.out.println("No articles found.");
            return;
        }

        for (int i = 0; i < articles.size(); i++) {
            NewsArticle a = articles.get(i);
            System.out.printf("%d. %s\n   Source: %s\n   Published: %s\n   Category: %s\n   URL: %s%n%n",
                    i + 1, a.getTitle(), a.getSource(), a.getPublishedAt(), a.getCategory(), a.getUrl());
        }

        int selection = readInt("Select article number to hide (0 to go back): ");
        if (selection == 0) return;

        if (selection > 0 && selection <= articles.size()) {
            NewsArticle selected = articles.get(selection - 1);
            boolean success = adminClient.hideArticle(selected.getId());
            System.out.println(success ? "Article hidden successfully." : "Failed to hide article.");
        } else {
            System.out.println("Invalid selection.");
        }
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
