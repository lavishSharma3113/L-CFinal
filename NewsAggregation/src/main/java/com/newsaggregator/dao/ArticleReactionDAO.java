package com.newsaggregator.dao;

import com.newsaggregator.utils.DBConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class ArticleReactionDAO {
    public boolean saveReaction(int userId, int articleId, String reactionType) {
        String sql = """
                INSERT INTO article_reactions (user_id, article_id, reaction_type)
                VALUES (?, ?, ?)
                ON DUPLICATE KEY UPDATE reaction_type = VALUES(reaction_type), reacted_at = CURRENT_TIMESTAMP
                """;

        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, articleId);
            ps.setString(3, reactionType.toLowerCase());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
