package com.newsaggregator.service;

import com.newsaggregator.model.ExternalServer;
import com.newsaggregator.model.NewsCategory;

import java.util.List;

public interface IAdminService {
    List<ExternalServer> getAllExternalServers();
    ExternalServer getExternalServerById(int id);
    boolean updateExternalServer(ExternalServer server);
    boolean addNewsCategory(NewsCategory category);
    boolean hideArticle(int articleId);
    boolean hideArticle(String keyword);
    boolean hideCategory(int categoryId, boolean isHidden);
}

