package com.newsaggregator.service.impl;

import com.newsaggregator.dao.ArticleReportDAO;
import com.newsaggregator.model.ArticleReport;
import com.newsaggregator.model.NewsArticle;
import com.newsaggregator.service.INewsReportService;

import java.util.List;

public class NewsReportServiceImpl implements INewsReportService {
    private final ArticleReportDAO dao = new ArticleReportDAO();
    public boolean saveReport(ArticleReport report) {
        return dao.saveReport(report);
    }

    public List<NewsArticle> getAllReports() {
        return dao.getAllReports();
    }
}
