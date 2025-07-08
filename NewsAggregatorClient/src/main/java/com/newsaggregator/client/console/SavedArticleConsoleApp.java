package com.newsaggregator.client.console;

import com.newsaggregator.client.model.NewsArticle;
import com.newsaggregator.client.service.SavedArticleClient;

import java.util.List;
import java.util.Scanner;

public class SavedArticleConsoleApp {
    private final SavedArticleClient client = new SavedArticleClient();
    private final Scanner scanner = new Scanner(System.in);

    public void showSavedArticles(int userId) {
        List<NewsArticle> savedArticles = client.getSavedArticles(userId);

        if (savedArticles == null || savedArticles.isEmpty()) {
            System.out.println("No saved articles found.");
            return;
        }

        printSavedArticles(savedArticles);

        int choice = readInt("\nEnter article number to delete (0 to skip): ");
        if (choice == 0) return;

        if (choice > 0 && choice <= savedArticles.size()) {
            NewsArticle selected = savedArticles.get(choice - 1);
            deleteSavedArticle(userId, selected.getId());
        } else {
            System.out.println("Invalid selection.");
        }
    }

    private void printSavedArticles(List<NewsArticle> articles) {
        System.out.println("=== Your Saved Articles ===");
        for (int i = 0; i < articles.size(); i++) {
            NewsArticle article = articles.get(i);
            System.out.printf("%d. %s%n", i + 1, article.getTitle());
            System.out.println("   Source: " + article.getSource());
            System.out.println("   Date: " + article.getPublishedAt());
            System.out.println("   URL: " + article.getUrl());
        }
    }

    private void deleteSavedArticle(int userId, int articleId) {
        boolean success = client.deleteSavedArticle(userId, articleId);
        System.out.println(success ? "Article removed." : "Could not remove article.");
    }

    private int readInt(String prompt) {
        System.out.print(prompt);
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException ex) {
            System.out.println("Invalid input. Please enter a number.");
            return readInt(prompt);
        }
    }
}
