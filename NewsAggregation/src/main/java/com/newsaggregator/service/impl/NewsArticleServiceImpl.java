package com.newsaggregator.service.impl;
import com.newsaggregator.dao.NewsArticleDAO;
import com.newsaggregator.model.NewsArticle;
import com.newsaggregator.service.INewsArticleService;

import java.util.List;

public class NewsArticleServiceImpl implements INewsArticleService {
    private final NewsArticleDAO dao = new NewsArticleDAO();

    @Override
    public void saveArticle(NewsArticle article) {
        dao.save(article);
    }

    @Override
    public List<NewsArticle> getAllArticles() {
        return dao.findAll();
    }

    @Override
    public List<NewsArticle> getArticlesByDateRange(String startDate, String endDate, String category) {
        return dao.findByDateRangeAndCategory(startDate, endDate, category);
    }

    @Override
    public List<NewsArticle> getArticlesByToday() {
        return dao.findToday();
    }

    @Override
    public List<NewsArticle> getArticlesByKeyword(String keyword) {
        return dao.findByKeyword(keyword);
    }
}
