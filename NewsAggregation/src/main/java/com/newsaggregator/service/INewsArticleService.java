package com.newsaggregator.service;

import com.newsaggregator.model.NewsArticle;

import java.util.List;

public interface INewsArticleService {
    void saveArticle(NewsArticle article);
    List<NewsArticle> getAllArticles();
    List<NewsArticle> getArticlesByDateRange(String startDate, String endDate, String category);
    List<NewsArticle> getArticlesByToday();
    List<NewsArticle> getArticlesByKeyword(String keyword);
}
