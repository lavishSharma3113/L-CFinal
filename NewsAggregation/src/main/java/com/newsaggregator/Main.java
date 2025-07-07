package com.newsaggregator;

import com.newsaggregator.dao.NewsArticleDAO;
import com.newsaggregator.model.NewsArticle;
import com.newsaggregator.newsfetcher.NewsApiOrgFetcher;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Instantiate the fetcher and DAO
        NewsApiOrgFetcher fetcher = new NewsApiOrgFetcher();
        NewsArticleDAO dao = new NewsArticleDAO();

        // Fetch articles from API
        List<NewsArticle> articles = fetcher.fetchNews();
        System.out.println("Fetched " + articles.size() + " articles from News API.");

        // Save each article to the database
        for (NewsArticle article : articles) {
            dao.save(article);
        }

        System.out.println("News articles have been saved to the database.");
    }
}
