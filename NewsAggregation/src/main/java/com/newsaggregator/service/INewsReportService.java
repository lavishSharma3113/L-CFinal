package com.newsaggregator.service;

import com.newsaggregator.model.ArticleReport;
import com.newsaggregator.model.NewsArticle;

import java.util.List;


public interface INewsReportService {
    boolean saveReport(ArticleReport report);
    List<NewsArticle> getAllReports();
}
