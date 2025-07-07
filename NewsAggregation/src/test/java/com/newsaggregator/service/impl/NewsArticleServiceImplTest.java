package com.newsaggregator.service.impl;

import com.newsaggregator.dao.NewsArticleDAO;
import com.newsaggregator.model.NewsArticle;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NewsArticleServiceImplTest {

    @Test
    void testSaveArticle_callsDaoSave() {
        NewsArticle article = new NewsArticle();
        article.setTitle("Test Title");

        try (MockedConstruction<NewsArticleDAO> mocked = mockConstruction(NewsArticleDAO.class)) {
            NewsArticleServiceImpl service = new NewsArticleServiceImpl();
            service.saveArticle(article);

            NewsArticleDAO mockDao = mocked.constructed().get(0);
            verify(mockDao).save(article);
        }
    }

    @Test
    void testGetAllArticles_returnsList() {
        List<NewsArticle> mockArticles = Arrays.asList(new NewsArticle(), new NewsArticle());

        try (MockedConstruction<NewsArticleDAO> mocked = mockConstruction(NewsArticleDAO.class,
                (mock, context) -> when(mock.findAll("2023-01-01", "2023-12-31", 1)).thenReturn(mockArticles))) {

            NewsArticleServiceImpl service = new NewsArticleServiceImpl();
            List<NewsArticle> result = service.getAllArticles("2023-01-01", "2023-12-31", 1);

            assertEquals(2, result.size());

            NewsArticleDAO mockDao = mocked.constructed().get(0);
            verify(mockDao).findAll("2023-01-01", "2023-12-31", 1);
        }
    }

    @Test
    void testGetArticlesByDateRange_returnsArticles() {
        List<NewsArticle> expected = List.of(new NewsArticle());

        try (MockedConstruction<NewsArticleDAO> mocked = mockConstruction(NewsArticleDAO.class,
                (mock, context) -> when(mock.findByDateRangeAndCategory("2023-01-01", "2023-01-05", "Tech"))
                        .thenReturn(expected))) {

            NewsArticleServiceImpl service = new NewsArticleServiceImpl();
            List<NewsArticle> result = service.getArticlesByDateRange("2023-01-01", "2023-01-05", "Tech");

            assertEquals(1, result.size());

            NewsArticleDAO mockDao = mocked.constructed().get(0);
            verify(mockDao).findByDateRangeAndCategory("2023-01-01", "2023-01-05", "Tech");
        }
    }

    @Test
    void testGetArticlesByToday_returnsArticles() {
        List<NewsArticle> expected = List.of(new NewsArticle());

        try (MockedConstruction<NewsArticleDAO> mocked = mockConstruction(NewsArticleDAO.class,
                (mock, context) -> when(mock.findToday(1)).thenReturn(expected))) {

            NewsArticleServiceImpl service = new NewsArticleServiceImpl();
            List<NewsArticle> result = service.getArticlesByToday(1);

            assertEquals(1, result.size());

            NewsArticleDAO mockDao = mocked.constructed().get(0);
            verify(mockDao).findToday(1);
        }
    }

    @Test
    void testGetArticlesByKeyword_returnsArticles() {
        List<NewsArticle> expected = List.of(new NewsArticle());

        try (MockedConstruction<NewsArticleDAO> mocked = mockConstruction(NewsArticleDAO.class,
                (mock, context) -> when(mock.findByKeyword("politics")).thenReturn(expected))) {

            NewsArticleServiceImpl service = new NewsArticleServiceImpl();
            List<NewsArticle> result = service.getArticlesByKeyword("politics");

            assertEquals(1, result.size());

            NewsArticleDAO mockDao = mocked.constructed().get(0);
            verify(mockDao).findByKeyword("politics");
        }
    }

    @Test
    void testFindMostLiked_returnsArticles() {
        List<NewsArticle> expected = Arrays.asList(new NewsArticle(), new NewsArticle());

        try (MockedConstruction<NewsArticleDAO> mocked = mockConstruction(NewsArticleDAO.class,
                (mock, context) -> when(mock.findMostLiked()).thenReturn(expected))) {

            NewsArticleServiceImpl service = new NewsArticleServiceImpl();
            List<NewsArticle> result = service.findMostLiked();

            assertEquals(2, result.size());

            NewsArticleDAO mockDao = mocked.constructed().get(0);
            verify(mockDao).findMostLiked();
        }
    }
}
