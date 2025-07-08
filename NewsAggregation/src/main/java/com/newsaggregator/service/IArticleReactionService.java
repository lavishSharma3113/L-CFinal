package com.newsaggregator.service;

public interface IArticleReactionService {
    boolean saveReaction(int userId, int articleId, String reactionType);
}

