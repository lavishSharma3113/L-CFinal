package com.newsaggregator.service.impl;

import com.newsaggregator.dao.ArticleReactionDAO;
import com.newsaggregator.dao.NewsCategoryDAO;
import com.newsaggregator.service.IArticleReactionService;
import com.newsaggregator.utils.DBConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class ArticleReactionServiceImpl implements IArticleReactionService {

    private final ArticleReactionDAO dao = new ArticleReactionDAO();
    @Override
    public boolean saveReaction(int userId, int articleId, String reactionType) {
        return dao.saveReaction(userId, articleId, reactionType);
    }
}

