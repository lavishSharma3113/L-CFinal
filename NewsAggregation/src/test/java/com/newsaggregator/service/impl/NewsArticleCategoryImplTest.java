package com.newsaggregator.service.impl;

import com.newsaggregator.dao.NewsCategoryDAO;
import com.newsaggregator.model.NewsCategory;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NewsArticleCategoryImplTest {

    @Test
    void testGetAllNewsCategories_returnsList() {
        NewsCategory category1 = new NewsCategory();
        category1.setId(1);
        category1.setName("technology");

        NewsCategory category2 = new NewsCategory();
        category2.setId(2);
        category2.setName("science");

        List<NewsCategory> mockList = Arrays.asList(category1, category2);

        try (MockedConstruction<NewsCategoryDAO> mocked = mockConstruction(NewsCategoryDAO.class,
                (mock, context) -> when(mock.getAllNewsCategories()).thenReturn(mockList))) {

            NewsArticleCategoryImpl service = new NewsArticleCategoryImpl();
            List<NewsCategory> result = service.getAllNewsCategories();

            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals("technology", result.get(0).getName());

            NewsCategoryDAO mockDao = mocked.constructed().get(0);
            verify(mockDao).getAllNewsCategories();
        }
    }

    @Test
    void testHideCategory_success() {
        int categoryId = 3;
        boolean isHidden = true;

        try (MockedConstruction<NewsCategoryDAO> mocked = mockConstruction(NewsCategoryDAO.class,
                (mock, context) -> when(mock.hideCategory(categoryId, isHidden)).thenReturn(true))) {

            NewsArticleCategoryImpl service = new NewsArticleCategoryImpl();
            boolean result = service.hideCategory(categoryId, isHidden);

            assertTrue(result);

            NewsCategoryDAO mockDao = mocked.constructed().get(0);
            verify(mockDao).hideCategory(categoryId, isHidden);
        }
    }

    @Test
    void testHideCategory_failure() {
        int categoryId = 4;
        boolean isHidden = false;

        try (MockedConstruction<NewsCategoryDAO> mocked = mockConstruction(NewsCategoryDAO.class,
                (mock, context) -> when(mock.hideCategory(categoryId, isHidden)).thenReturn(false))) {

            NewsArticleCategoryImpl service = new NewsArticleCategoryImpl();
            boolean result = service.hideCategory(categoryId, isHidden);

            assertFalse(result);

            NewsCategoryDAO mockDao = mocked.constructed().get(0);
            verify(mockDao).hideCategory(categoryId, isHidden);
        }
    }
}
