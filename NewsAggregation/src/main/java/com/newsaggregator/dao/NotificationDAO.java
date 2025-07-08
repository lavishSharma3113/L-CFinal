package com.newsaggregator.dao;

import com.newsaggregator.model.NewsArticle;
import com.newsaggregator.model.Notification;
import com.newsaggregator.utils.DBConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {
    private static final Logger logger = LoggerFactory.getLogger(NotificationDAO.class);

    public boolean save(Notification notification) {
        String sql = "INSERT INTO notifications (user_id, message, timestamp, is_read) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, notification.getUserId());
            ps.setString(2, notification.getMessage());
            ps.setString(3, notification.getTimestamp());
            ps.setBoolean(4, notification.isRead());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error saving notification", e);
            return false;
        }
    }

    public List<NewsArticle> getByUser(int userId) {
        List<NewsArticle> list = new ArrayList<>();
        String sql = """
                SELECT na.*
                FROM news_articles na
                INNER JOIN notification_config nc ON na.category_id = nc.category_id
                INNER JOIN users u ON nc.user_id = u.user_id
                WHERE nc.is_enabled = true
                  AND (u.last_notification_seen IS NULL OR na.created_at > u.last_notification_seen)
                  AND u.user_id = ?
                """;

        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractArticleFromResultSet(rs));
                }
            }

            List<NewsArticle> keywordArticles = getNotificationsFromKeywords(userId);
            list.addAll(keywordArticles);

        } catch (SQLException e) {
            logger.error("Error fetching notifications for userId: {}", userId, e);
        }

        return list;
    }

    private List<NewsArticle> getNotificationsFromKeywords(int userId) {
        List<NewsArticle> articles = new ArrayList<>();
        String sql = """
                SELECT DISTINCT na.*
                FROM news_articles na
                JOIN user_keywords gk
                  ON (na.title LIKE CONCAT('%', gk.keyword, '%') OR na.content LIKE CONCAT('%', gk.keyword, '%'))
                JOIN users u ON gk.user_id = u.user_id
                WHERE u.user_id = ?
                  AND (u.last_notification_seen IS NULL OR na.created_at > u.last_notification_seen)
                """;

        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    articles.add(extractArticleFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error fetching keyword-based notifications for userId: {}", userId, e);
        }

        return articles;
    }

    public boolean updateLastNotificationSeen(int userId) {
        String sql = "UPDATE users SET last_notification_seen = NOW() WHERE user_id = ?";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating last_notification_seen for userId: {}", userId, e);
            return false;
        }
    }

    private NewsArticle extractArticleFromResultSet(ResultSet rs) throws SQLException {
        NewsArticle article = new NewsArticle();
        article.setId(rs.getInt("id"));
        article.setTitle(rs.getString("title"));
        article.setContent(rs.getString("content"));
        article.setSource(rs.getString("source"));
        article.setCategoryId(rs.getInt("category_id"));
        article.setUrl(rs.getString("url"));
        article.setPublishedAt(rs.getString("published_at"));
        return article;
    }
}
