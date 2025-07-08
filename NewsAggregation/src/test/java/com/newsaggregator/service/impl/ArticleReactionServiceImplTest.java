package com.newsaggregator.service.impl;

import com.newsaggregator.dao.ArticleReactionDAO;
import com.newsaggregator.service.impl.ArticleReactionServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ArticleReactionServiceImplTest {

    @Test
    void testSaveReaction_success() {
        int userId = 1;
        int articleId = 1;
        String reactionType = "like";

        try (MockedConstruction<ArticleReactionDAO> mocked = mockConstruction(ArticleReactionDAO.class,
                (mock, context) -> when(mock.saveReaction(userId, articleId, reactionType)).thenReturn(true))) {

            ArticleReactionServiceImpl service = new ArticleReactionServiceImpl();
            boolean result = service.saveReaction(userId, articleId, reactionType);

            assertTrue(result);

            ArticleReactionDAO mockDao = mocked.constructed().get(0);
            verify(mockDao).saveReaction(userId, articleId, reactionType);
        }
    }

    @Test
    void testSaveReaction_failure() {
        int userId = 2;
        int articleId = 5;
        String reactionType = "dislike";

        try (MockedConstruction<ArticleReactionDAO> mocked = mockConstruction(ArticleReactionDAO.class,
                (mock, context) -> when(mock.saveReaction(userId, articleId, reactionType)).thenReturn(false))) {

            ArticleReactionServiceImpl service = new ArticleReactionServiceImpl();
            boolean result = service.saveReaction(userId, articleId, reactionType);

            assertFalse(result);

            ArticleReactionDAO mockDao = mocked.constructed().get(0);
            verify(mockDao).saveReaction(userId, articleId, reactionType);
        }
    }
}
