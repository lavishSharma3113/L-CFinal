package com.newsaggregator.service.impl;

import com.newsaggregator.dao.AdminDAO;
import com.newsaggregator.model.ExternalServer;
import com.newsaggregator.model.NewsCategory;
import com.newsaggregator.service.IAdminService;

import java.util.List;

public class AdminServiceImpl implements IAdminService {
    private final AdminDAO dao = new AdminDAO();

    public List<ExternalServer> getAllExternalServers() {
        return dao.getAllExternalServers();
    }

    public ExternalServer getExternalServerById(int id) {
        return dao.getExternalServerById(id);
    }

    public boolean updateExternalServer(ExternalServer server) {
        return dao.updateExternalServer(server);
    }

    public boolean addNewsCategory(NewsCategory category) {
        return dao.addNewsCategory(category);
    }
}

