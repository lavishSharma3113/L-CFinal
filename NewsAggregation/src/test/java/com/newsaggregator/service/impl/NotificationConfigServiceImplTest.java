package com.newsaggregator.service.impl;

import com.newsaggregator.dao.NotificationConfigDAO;
import com.newsaggregator.model.NotificationConfig;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationConfigServiceImplTest {

    @Test
    void testUpdate_shouldReturnTrue() {
        NotificationConfig config = new NotificationConfig();

        try (MockedConstruction<NotificationConfigDAO> mocked = mockConstruction(NotificationConfigDAO.class,
                (mock, context) -> when(mock.saveOrUpdate(config)).thenReturn(true))) {

            NotificationConfigServiceImpl service = new NotificationConfigServiceImpl();
            boolean result = service.update(config);

            assertTrue(result);
            NotificationConfigDAO mockDao = mocked.constructed().get(0);
            verify(mockDao).saveOrUpdate(config);
        }
    }

    @Test
    void testUpdate_shouldReturnFalse() {
        NotificationConfig config = new NotificationConfig();

        try (MockedConstruction<NotificationConfigDAO> mocked = mockConstruction(NotificationConfigDAO.class,
                (mock, context) -> when(mock.saveOrUpdate(config)).thenReturn(false))) {

            NotificationConfigServiceImpl service = new NotificationConfigServiceImpl();
            boolean result = service.update(config);

            assertFalse(result);
            NotificationConfigDAO mockDao = mocked.constructed().get(0);
            verify(mockDao).saveOrUpdate(config);
        }
    }

    @Test
    void testGetConfigsByUser_shouldReturnList() {
        NotificationConfig config1 = new NotificationConfig();
        NotificationConfig config2 = new NotificationConfig();
        List<NotificationConfig> mockList = Arrays.asList(config1, config2);

        try (MockedConstruction<NotificationConfigDAO> mocked = mockConstruction(NotificationConfigDAO.class,
                (mock, context) -> when(mock.getConfigsByUser(1)).thenReturn(mockList))) {

            NotificationConfigServiceImpl service = new NotificationConfigServiceImpl();
            List<NotificationConfig> configs = service.getConfigsByUser(1);

            assertEquals(2, configs.size());
            NotificationConfigDAO mockDao = mocked.constructed().get(0);
            verify(mockDao).getConfigsByUser(1);
        }
    }

    @Test
    void testInsertUserKeyword_shouldReturnTrue() {
        try (MockedConstruction<NotificationConfigDAO> mocked = mockConstruction(NotificationConfigDAO.class,
                (mock, context) -> when(mock.insertUserKeyword(1, "technology")).thenReturn(true))) {

            NotificationConfigServiceImpl service = new NotificationConfigServiceImpl();
            boolean result = service.insertUserKeyword(1, "technology");

            assertTrue(result);
            NotificationConfigDAO mockDao = mocked.constructed().get(0);
            verify(mockDao).insertUserKeyword(1, "technology");
        }
    }

    @Test
    void testInsertUserKeyword_shouldReturnFalse() {
        try (MockedConstruction<NotificationConfigDAO> mocked = mockConstruction(NotificationConfigDAO.class,
                (mock, context) -> when(mock.insertUserKeyword(1, "sports")).thenReturn(false))) {

            NotificationConfigServiceImpl service = new NotificationConfigServiceImpl();
            boolean result = service.insertUserKeyword(1, "sports");

            assertFalse(result);
            NotificationConfigDAO mockDao = mocked.constructed().get(0);
            verify(mockDao).insertUserKeyword(1, "sports");
        }
    }
}
