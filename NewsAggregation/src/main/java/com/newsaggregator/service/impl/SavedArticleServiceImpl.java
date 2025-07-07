package com.newsaggregator.service.impl;

import com.newsaggregator.dao.SavedArticleDAO;
import com.newsaggregator.model.NewsArticle;
import com.newsaggregator.model.SavedArticle;
import com.newsaggregator.service.ISavedArticleService;

import java.util.List;

public class SavedArticleServiceImpl implements ISavedArticleService {
    private final SavedArticleDAO dao = new SavedArticleDAO();

    public boolean saveArticle(SavedArticle saved) {
        return dao.save(saved);
    }

    public List<NewsArticle> getSavedByUser(int userId) {
        return dao.findByUserId(userId);
    }

    public boolean removeSavedArticle(int userId, int articleId) {
        return dao.delete(userId, articleId);
    }
}
