package com.newsaggregator.client.console;



import com.newsaggregator.client.model.NewsArticle;
import com.newsaggregator.client.service.SavedArticleClient;

import java.util.List;
import java.util.Scanner;

public class SavedArticleConsoleApp {
    private final SavedArticleClient client = new SavedArticleClient();
    private final Scanner scanner = new Scanner(System.in);

    public void showSavedArticles(int userId) {
        List<NewsArticle> saved = client.getSavedArticles(userId);
        if (saved.isEmpty()) {
            System.out.println("No saved articles found.");
            return;
        }

        System.out.println("Your Saved Articles:");
        for (int i = 0; i < saved.size(); i++) {
            NewsArticle a = saved.get(i);
            System.out.println((i + 1) + ". " + a.getTitle());
            System.out.println("   Source: " + a.getSource());
            System.out.println("   Date: " + a.getPublishedAt());
            System.out.println("   URL: " + a.getUrl());
        }

        System.out.print("\nEnter article number to delete (0 to skip): ");
        int choice = Integer.parseInt(scanner.nextLine());
        if (choice > 0 && choice <= saved.size()) {
            int articleId = saved.get(choice - 1).getId();
            boolean deleted = client.deleteSavedArticle(userId, articleId);
            if (deleted) {
                System.out.println("Article removed.");
            } else {
                System.out.println("Could not remove article.");
            }
        }
    }
}

