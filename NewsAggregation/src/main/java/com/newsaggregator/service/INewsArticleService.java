package com.newsaggregator.service;

import com.newsaggregator.model.NewsArticle;
import com.newsaggregator.model.NewsCategory;

import java.util.List;

public interface INewsArticleService {
    void saveArticle(NewsArticle article);
    List<NewsArticle> getAllArticles(String startDate, String endDate, int userId);
    List<NewsArticle> getArticlesByDateRange(String startDate, String endDate, String category);
    List<NewsArticle> getArticlesByToday(int userId);
    List<NewsArticle> getArticlesByKeyword(String keyword);
    List<NewsArticle> findMostLiked();

}
