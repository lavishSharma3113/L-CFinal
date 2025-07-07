package com.newsaggregator.client.console;





import com.newsaggregator.client.model.NewsArticle;
import com.newsaggregator.client.service.NewsClient;
import com.newsaggregator.client.service.SavedArticleClient;

import java.util.List;
import java.util.Scanner;

public class NewsConsoleApp {
    private final NewsClient newsClient = new NewsClient();
    private final SavedArticleClient savedClient = new SavedArticleClient();
    private final Scanner scanner = new Scanner(System.in);

    public void showHeadlineMenu(int userId) {
        System.out.println("\n==== View Headlines ====");
        System.out.println("1. View Today’s News");
        System.out.println("2. View by Date Range and Category");
        System.out.print("Choose: ");
        int choice = Integer.parseInt(scanner.nextLine());

        switch (choice) {
            case 1:
                showTodayHeadlines(userId);
                break;
            case 2:
                showByDateRange(userId);
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    public void showTodayHeadlines(int userId) {
        List<NewsArticle> articles = newsClient.getHeadlines();
        printAndOfferSave(articles, userId);
    }

    public void showByDateRange(int userId) {
        System.out.print("Enter start date (YYYY-MM-DD): ");
        String start = scanner.nextLine();
        System.out.print("Enter end date (YYYY-MM-DD): ");
        String end = scanner.nextLine();

        System.out.println("\nChoose Category:");
        System.out.println("1. All");
        System.out.println("2. Business");
        System.out.println("3. Entertainment");
        System.out.println("4. Sports");
        System.out.println("5. Technology");
        System.out.print("Enter number: ");
        int categoryOption = Integer.parseInt(scanner.nextLine());

        String category = switch (categoryOption) {
            case 2 -> "business";
            case 3 -> "entertainment";
            case 4 -> "sports";
            case 5 -> "technology";
            default -> "";
        };

        List<NewsArticle> articles = newsClient.searchByDateRange(start, end, category);
        printAndOfferSave(articles, userId);
    }

    private void printAndOfferSave(List<NewsArticle> articles, int userId) {
        if (articles == null || articles.isEmpty()) {
            System.out.println("No articles found.");
            return;
        }

        for (int i = 0; i < articles.size(); i++) {
            NewsArticle a = articles.get(i);
            System.out.println((i + 1) + ". " + a.getTitle());
            System.out.println("   Source: " + a.getSource());
            System.out.println("   Published: " + a.getPublishedAt());
            System.out.println("   Category: " + a.getCategory());
            System.out.println("   URL: " + a.getUrl());
            System.out.println();
        }

        System.out.print("Enter article number to save (or 0 to skip): ");
        int selection = Integer.parseInt(scanner.nextLine());
        if (selection > 0 && selection <= articles.size()) {
            int articleId = articles.get(selection - 1).getId();
            if (savedClient.saveArticle(userId, articleId)) {
                System.out.println("Article saved successfully.");
            } else {
                System.out.println("Failed to save article.");
            }
        }
    }
}
