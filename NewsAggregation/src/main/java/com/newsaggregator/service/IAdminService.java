package com.newsaggregator.service;

import com.newsaggregator.model.ExternalServer;
import com.newsaggregator.model.NewsCategory;

import java.util.List;

public interface IAdminService {
    List<ExternalServer> getAllExternalServers();
    ExternalServer getExternalServerById(int id);
    boolean updateExternalServer(ExternalServer server);
    boolean addNewsCategory(NewsCategory category);
}

