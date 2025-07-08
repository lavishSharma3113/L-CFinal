package com.newsaggregator.service.impl;

import com.newsaggregator.dao.AdminDAO;
import com.newsaggregator.model.ExternalServer;
import com.newsaggregator.model.NewsCategory;
import com.newsaggregator.service.impl.AdminServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AdminServiceImplTest {

    @Test
    void testGetAllExternalServers_returnsServers() {
        ExternalServer server1 = new ExternalServer();
        server1.setId(1);
        server1.setName("NewsAPI");

        ExternalServer server2 = new ExternalServer();
        server2.setId(2);
        server2.setName("The Guardian");

        List<ExternalServer> mockServers = Arrays.asList(server1, server2);

        try (MockedConstruction<AdminDAO> mocked = mockConstruction(AdminDAO.class,
                (mock, context) -> when(mock.getAllExternalServers()).thenReturn(mockServers))) {

            AdminServiceImpl service = new AdminServiceImpl();
            List<ExternalServer> result = service.getAllExternalServers();

            assertEquals(2, result.size());
            assertEquals("NewsAPI", result.get(0).getName());

            AdminDAO mockDao = mocked.constructed().get(0);
            verify(mockDao).getAllExternalServers();

        }
    }

    @Test
    void testGetExternalServerById_returnsServer() {
        ExternalServer server = new ExternalServer();
        server.setId(1);
        server.setName("BBC");

        try (MockedConstruction<AdminDAO> mocked = mockConstruction(AdminDAO.class,
                (mock, context) -> when(mock.getExternalServerById(1)).thenReturn(server))) {

            AdminServiceImpl service = new AdminServiceImpl();
            ExternalServer result = service.getExternalServerById(1);

            assertNotNull(result);
            assertEquals("BBC", result.getName());

            AdminDAO mockDao = mocked.constructed().get(0);
            verify(mockDao).getExternalServerById(1);

        }
    }

    @Test
    void testUpdateExternalServer_returnsTrue() {
        ExternalServer server = new ExternalServer();
        server.setId(1);
        server.setApiKey("new-api-key");

        try (MockedConstruction<AdminDAO> mocked = mockConstruction(AdminDAO.class,
                (mock, context) -> when(mock.updateExternalServer(server)).thenReturn(true))) {

            AdminServiceImpl service = new AdminServiceImpl();
            boolean result = service.updateExternalServer(server);

            assertTrue(result);

            AdminDAO mockDao = mocked.constructed().get(0);
            verify(mockDao).updateExternalServer(server);
        }
    }

    @Test
    void testAddNewsCategory_returnsTrue() {
        NewsCategory category = new NewsCategory();
        category.setName("Science");

        try (MockedConstruction<AdminDAO> mocked = mockConstruction(AdminDAO.class,
                (mock, context) -> when(mock.addNewsCategory(category)).thenReturn(true))) {

            AdminServiceImpl service = new AdminServiceImpl();
            boolean result = service.addNewsCategory(category);

            assertTrue(result);

            AdminDAO mockDao = mocked.constructed().get(0);
            verify(mockDao).addNewsCategory(category);
        }
    }

    @Test
    void testHideArticleById_returnsTrue() {
        try (MockedConstruction<AdminDAO> mocked = mockConstruction(AdminDAO.class,
                (mock, context) -> when(mock.hideArticle(10)).thenReturn(true))) {

            AdminServiceImpl service = new AdminServiceImpl();
            boolean result = service.hideArticle(10);

            assertTrue(result);

            AdminDAO mockDao = mocked.constructed().get(0);
            verify(mockDao).hideArticle(10);
        }
    }

    @Test
    void testHideArticleByKeyword_returnsTrue() {
        String keyword = "fake";

        try (MockedConstruction<AdminDAO> mocked = mockConstruction(AdminDAO.class,
                (mock, context) -> when(mock.hideArticle(keyword)).thenReturn(true))) {

            AdminServiceImpl service = new AdminServiceImpl();
            boolean result = service.hideArticle(keyword);

            assertTrue(result);

            AdminDAO mockDao = mocked.constructed().get(0);
            verify(mockDao).hideArticle(keyword);
        }
    }

    @Test
    void testHideCategory_returnsTrue() {
        int categoryId = 5;
        boolean isHidden = true;

        try (MockedConstruction<AdminDAO> mocked = mockConstruction(AdminDAO.class,
                (mock, context) -> when(mock.hideCategory(categoryId, isHidden)).thenReturn(true))) {

            AdminServiceImpl service = new AdminServiceImpl();
            boolean result = service.hideCategory(categoryId, isHidden);

            assertTrue(result);

            AdminDAO mockDao = mocked.constructed().get(0);
            verify(mockDao).hideCategory(categoryId, isHidden);
        }
    }
}
