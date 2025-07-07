package com.newsaggregator.service.impl;

import com.newsaggregator.dao.SavedArticleDAO;
import com.newsaggregator.model.NewsArticle;
import com.newsaggregator.model.SavedArticle;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SavedArticleServiceImplTest {

    @Test
    void testSaveArticle_shouldReturnTrue() {
        SavedArticle article = new SavedArticle();

        try (MockedConstruction<SavedArticleDAO> mocked = mockConstruction(SavedArticleDAO.class,
                (mock, context) -> when(mock.save(article)).thenReturn(true))) {

            SavedArticleServiceImpl service = new SavedArticleServiceImpl();
            boolean result = service.saveArticle(article);

            assertTrue(result);
            SavedArticleDAO dao = mocked.constructed().get(0);
            verify(dao).save(article);
        }
    }

    @Test
    void testSaveArticle_shouldReturnFalse() {
        SavedArticle article = new SavedArticle();

        try (MockedConstruction<SavedArticleDAO> mocked = mockConstruction(SavedArticleDAO.class,
                (mock, context) -> when(mock.save(article)).thenReturn(false))) {

            SavedArticleServiceImpl service = new SavedArticleServiceImpl();
            boolean result = service.saveArticle(article);

            assertFalse(result);
            SavedArticleDAO dao = mocked.constructed().get(0);
            verify(dao).save(article);
        }
    }

    @Test
    void testGetSavedByUser_shouldReturnList() {
        NewsArticle a1 = new NewsArticle();
        NewsArticle a2 = new NewsArticle();
        List<NewsArticle> mockList = Arrays.asList(a1, a2);

        try (MockedConstruction<SavedArticleDAO> mocked = mockConstruction(SavedArticleDAO.class,
                (mock, context) -> when(mock.findByUserId(1)).thenReturn(mockList))) {

            SavedArticleServiceImpl service = new SavedArticleServiceImpl();
            List<NewsArticle> result = service.getSavedByUser(1);

            assertEquals(2, result.size());
            SavedArticleDAO dao = mocked.constructed().get(0);
            verify(dao).findByUserId(1);
        }
    }

    @Test
    void testRemoveSavedArticle_shouldReturnTrue() {
        try (MockedConstruction<SavedArticleDAO> mocked = mockConstruction(SavedArticleDAO.class,
                (mock, context) -> when(mock.delete(1, 101)).thenReturn(true))) {

            SavedArticleServiceImpl service = new SavedArticleServiceImpl();
            boolean result = service.removeSavedArticle(1, 101);

            assertTrue(result);
            SavedArticleDAO dao = mocked.constructed().get(0);
            verify(dao).delete(1, 101);
        }
    }

    @Test
    void testRemoveSavedArticle_shouldReturnFalse() {
        try (MockedConstruction<SavedArticleDAO> mocked = mockConstruction(SavedArticleDAO.class,
                (mock, context) -> when(mock.delete(1, 404)).thenReturn(false))) {

            SavedArticleServiceImpl service = new SavedArticleServiceImpl();
            boolean result = service.removeSavedArticle(1, 404);

            assertFalse(result);
            SavedArticleDAO dao = mocked.constructed().get(0);
            verify(dao).delete(1, 404);
        }
    }

    @Test
    void testSaveUserArticleHistory_shouldReturnTrue() {
        try (MockedConstruction<SavedArticleDAO> mocked = mockConstruction(SavedArticleDAO.class,
                (mock, context) -> when(mock.saveUserArticleHistory(1, 202)).thenReturn(true))) {

            SavedArticleServiceImpl service = new SavedArticleServiceImpl();
            boolean result = service.saveUserArticleHistory(1, 202);

            assertTrue(result);
            SavedArticleDAO dao = mocked.constructed().get(0);
            verify(dao).saveUserArticleHistory(1, 202);
        }
    }

    @Test
    void testSaveUserArticleHistory_shouldReturnFalse() {
        try (MockedConstruction<SavedArticleDAO> mocked = mockConstruction(SavedArticleDAO.class,
                (mock, context) -> when(mock.saveUserArticleHistory(2, 505)).thenReturn(false))) {

            SavedArticleServiceImpl service = new SavedArticleServiceImpl();
            boolean result = service.saveUserArticleHistory(2, 505);

            assertFalse(result);
            SavedArticleDAO dao = mocked.constructed().get(0);
            verify(dao).saveUserArticleHistory(2, 505);
        }
    }
}
