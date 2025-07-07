package com.newsaggregator.service.impl;

import com.newsaggregator.dao.ArticleReportDAO;
import com.newsaggregator.model.ArticleReport;
import com.newsaggregator.model.NewsArticle;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NewsReportServiceImplTest {

    @Test
    void testSaveReport_shouldReturnTrue() {
        ArticleReport report = new ArticleReport();

        try (MockedConstruction<ArticleReportDAO> mocked = mockConstruction(ArticleReportDAO.class,
                (mock, context) -> when(mock.saveReport(report)).thenReturn(true))) {

            NewsReportServiceImpl service = new NewsReportServiceImpl();
            boolean result = service.saveReport(report);

            assertTrue(result);
            ArticleReportDAO mockDao = mocked.constructed().get(0);
            verify(mockDao).saveReport(report);
        }
    }

    @Test
    void testSaveReport_shouldReturnFalse() {
        ArticleReport report = new ArticleReport();

        try (MockedConstruction<ArticleReportDAO> mocked = mockConstruction(ArticleReportDAO.class,
                (mock, context) -> when(mock.saveReport(report)).thenReturn(false))) {

            NewsReportServiceImpl service = new NewsReportServiceImpl();
            boolean result = service.saveReport(report);

            assertFalse(result);
            ArticleReportDAO mockDao = mocked.constructed().get(0);
            verify(mockDao).saveReport(report);
        }
    }

    @Test
    void testGetAllReports_shouldReturnArticles() {
        NewsArticle article1 = new NewsArticle();
        NewsArticle article2 = new NewsArticle();
        List<NewsArticle> expected = Arrays.asList(article1, article2);

        try (MockedConstruction<ArticleReportDAO> mocked = mockConstruction(ArticleReportDAO.class,
                (mock, context) -> when(mock.getAllReports()).thenReturn(expected))) {

            NewsReportServiceImpl service = new NewsReportServiceImpl();
            List<NewsArticle> result = service.getAllReports();

            assertEquals(2, result.size());
            ArticleReportDAO mockDao = mocked.constructed().get(0);
            verify(mockDao).getAllReports();
        }
    }
}
