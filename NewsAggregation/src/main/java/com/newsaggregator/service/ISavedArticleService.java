package com.newsaggregator.service;

import com.newsaggregator.model.NewsArticle;
import com.newsaggregator.model.SavedArticle;

import java.util.List;

public interface ISavedArticleService {
    boolean saveArticle(SavedArticle saved);
    List<NewsArticle> getSavedByUser(int userId);
    boolean removeSavedArticle(int userId, int articleId);
    boolean saveUserArticleHistory(int userId , int articleId);
}
