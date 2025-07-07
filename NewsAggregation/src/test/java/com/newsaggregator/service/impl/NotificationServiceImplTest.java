package com.newsaggregator.service.impl;

import com.newsaggregator.dao.NotificationDAO;
import com.newsaggregator.model.NewsArticle;
import com.newsaggregator.model.Notification;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationServiceImplTest {

    @Test
    void testSendNotification_shouldReturnTrue() {
        Notification notification = new Notification();

        try (MockedConstruction<NotificationDAO> mocked = mockConstruction(NotificationDAO.class,
                (mock, context) -> when(mock.save(notification)).thenReturn(true))) {

            NotificationServiceImpl service = new NotificationServiceImpl();
            boolean result = service.sendNotification(notification);

            assertTrue(result);
            NotificationDAO mockDao = mocked.constructed().get(0);
            verify(mockDao).save(notification);
        }
    }

    @Test
    void testSendNotification_shouldReturnFalse() {
        Notification notification = new Notification();

        try (MockedConstruction<NotificationDAO> mocked = mockConstruction(NotificationDAO.class,
                (mock, context) -> when(mock.save(notification)).thenReturn(false))) {

            NotificationServiceImpl service = new NotificationServiceImpl();
            boolean result = service.sendNotification(notification);

            assertFalse(result);
            NotificationDAO mockDao = mocked.constructed().get(0);
            verify(mockDao).save(notification);
        }
    }

    @Test
    void testGetUserNotifications_shouldReturnList() {
        NewsArticle article1 = new NewsArticle();
        NewsArticle article2 = new NewsArticle();
        List<NewsArticle> expected = Arrays.asList(article1, article2);

        try (MockedConstruction<NotificationDAO> mocked = mockConstruction(NotificationDAO.class,
                (mock, context) -> when(mock.getByUser(1)).thenReturn(expected))) {

            NotificationServiceImpl service = new NotificationServiceImpl();
            List<NewsArticle> result = service.getUserNotifications(1);

            assertEquals(2, result.size());
            NotificationDAO mockDao = mocked.constructed().get(0);
            verify(mockDao).getByUser(1);
        }
    }

    @Test
    void testUpdateLastNotificationSeen_shouldReturnTrue() {
        try (MockedConstruction<NotificationDAO> mocked = mockConstruction(NotificationDAO.class,
                (mock, context) -> when(mock.updateLastNotificationSeen(1)).thenReturn(true))) {

            NotificationServiceImpl service = new NotificationServiceImpl();
            boolean result = service.updateLastNotificationSeen(1);

            assertTrue(result);
            NotificationDAO mockDao = mocked.constructed().get(0);
            verify(mockDao).updateLastNotificationSeen(1);
        }
    }

    @Test
    void testUpdateLastNotificationSeen_shouldReturnFalse() {
        try (MockedConstruction<NotificationDAO> mocked = mockConstruction(NotificationDAO.class,
                (mock, context) -> when(mock.updateLastNotificationSeen(1)).thenReturn(false))) {

            NotificationServiceImpl service = new NotificationServiceImpl();
            boolean result = service.updateLastNotificationSeen(1);

            assertFalse(result);
            NotificationDAO mockDao = mocked.constructed().get(0);
            verify(mockDao).updateLastNotificationSeen(1);
        }
    }
}
