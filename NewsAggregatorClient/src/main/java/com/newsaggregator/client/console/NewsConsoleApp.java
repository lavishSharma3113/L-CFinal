package com.newsaggregator.client.console;

import com.newsaggregator.client.model.NewsArticle;
import com.newsaggregator.client.service.ArticleReactionClient;
import com.newsaggregator.client.service.NewsClient;
import com.newsaggregator.client.service.NewsReportClient;
import com.newsaggregator.client.service.SavedArticleClient;

import java.util.List;
import java.util.Scanner;

public class NewsConsoleApp {
    private final NewsClient newsClient = new NewsClient();
    private final SavedArticleClient savedClient = new SavedArticleClient();
    private final NewsReportClient reportClient = new NewsReportClient();
    private final ArticleReactionClient reactionClient = new ArticleReactionClient();
    private final Scanner scanner = new Scanner(System.in);

    public void showHeadlineMenu(int userId) {
        System.out.println("\n==== View Headlines ====");
        System.out.println("1. View Today’s News");
        System.out.println("2. View by Date Range and Category");
        System.out.println("3. View Most Liked News");
        int choice = readInt("Choose: ");

        switch (choice) {
            case 1 -> showTodayHeadlines(userId);
            case 2 -> showByDateRange(userId);
            case 3 -> showByMostLiked(userId);
            default -> System.out.println("Invalid choice.");
        }
    }

    public void showTodayHeadlines(int userId) {
        List<NewsArticle> articles = newsClient.getHeadlines(userId);
        displayArticlesWithActions(articles, userId);
    }

    public void showByDateRange(int userId) {
        String start = readLine("Enter start date (YYYY-MM-DD): ");
        String end = readLine("Enter end date (YYYY-MM-DD): ");

        System.out.println("\nChoose Category:");
        System.out.println("1. All");
        System.out.println("2. Business");
        System.out.println("3. Entertainment");
        System.out.println("4. Sports");
        System.out.println("5. Technology");
        int categoryOption = readInt("Enter number: ");

        String category = switch (categoryOption) {
            case 1 -> "all";
            case 2 -> "business";
            case 3 -> "entertainment";
            case 4 -> "sports";
            case 5 -> "technology";
            default -> "";
        };

        List<NewsArticle> articles = category.equals("all")
                ? newsClient.searchAllCategoryNews(start, end, userId)
                : newsClient.searchByDateRange(start, end, category);

        displayArticlesWithActions(articles, userId);
    }

    public void showNewsByKeyword(int userId) {
        String keyword = readLine("Enter keyword: ");
        List<NewsArticle> articles = newsClient.searchByKeyword(keyword);
        displayArticlesWithActions(articles, userId);
    }

    public void showByMostLiked(int userId) {
        List<NewsArticle> articles = newsClient.searchMostLiked();
        displayArticlesWithActions(articles, userId);
    }

    private void displayArticlesWithActions(List<NewsArticle> articles, int userId) {
        if (articles == null || articles.isEmpty()) {
            System.out.println("No articles found.");
            return;
        }

        printArticleTitles(articles);

        while (true) {
            int selection = readInt("Select article number to view in detail (0 to skip): ");
            if (selection == 0) return;

            if (selection > 0 && selection <= articles.size()) {
                NewsArticle selected = articles.get(selection - 1);
                savedClient.saveUserArticleHistory(userId, selected.getId());
                printArticleDetails(selected);
                performArticleActions(userId, selected);
            } else {
                System.out.println("Invalid selection. Try again.");
            }
        }
    }

    private void printArticleTitles(List<NewsArticle> articles) {
        for (int i = 0; i < articles.size(); i++) {
            NewsArticle a = articles.get(i);
            System.out.printf("%d. %s\n   Category: %s\n\n", i + 1, a.getTitle(), a.getCategory());
        }
    }

    private void printArticleDetails(NewsArticle article) {
        System.out.println("\n=== Article Details ===");
        System.out.println("Title    : " + article.getTitle());
        System.out.println("Source   : " + article.getSource());
        System.out.println("Published: " + article.getPublishedAt());
        System.out.println("Category : " + article.getCategory());
        System.out.println("URL      : " + article.getUrl());
        System.out.println("Content  : " + article.getContent());
    }

    private void performArticleActions(int userId, NewsArticle article) {
        while (true) {
            System.out.printf("\nChoose Action for: %s\n", article.getTitle());
            System.out.println("1. Save Article");
            System.out.println("2. Like Article");
            System.out.println("3. Dislike Article");
            System.out.println("4. Report Article");
            System.out.println("5. Back");
            int action = readInt("Enter choice: ");

            switch (action) {
                case 1 -> handleSaveArticle(userId, article);
                case 2 -> handleLikeArticle(userId, article);
                case 3 -> handleDislikeArticle(userId, article);
                case 4 -> handleReportArticle(userId, article);
                case 5 -> { return; }
                default -> System.out.println("Invalid action. Try again.");
            }
        }
    }

    private void handleSaveArticle(int userId, NewsArticle article) {
        boolean saved = savedClient.saveArticle(userId, article.getId());
        System.out.println(saved ? "Article saved." : "Failed to save article.");
    }

    private void handleLikeArticle(int userId, NewsArticle article) {
        boolean liked = reactionClient.likeArticle(userId, article.getId());
        System.out.println(liked ? "Article liked." : "Failed to like article.");
    }

    private void handleDislikeArticle(int userId, NewsArticle article) {
        boolean disliked = reactionClient.dislikeArticle(userId, article.getId());
        System.out.println(disliked ? "Article disliked." : "Failed to dislike article.");
    }

    private void handleReportArticle(int userId, NewsArticle article) {
        boolean reported = reportClient.saveReport(userId, article.getId());
        System.out.println(reported ? "Article reported." : "Failed to report or already reported.");
    }

    // Input Utility Methods
    private int readInt(String prompt) {
        System.out.print(prompt);
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number. Please try again.");
            return readInt(prompt);
        }
    }

    private String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }
}
