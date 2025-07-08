package com.newsaggregator.dao;

import com.newsaggregator.model.NewsArticle;
import com.newsaggregator.model.SavedArticle;
import com.newsaggregator.utils.DBConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SavedArticleDAO {
    private static final Logger logger = LoggerFactory.getLogger(SavedArticleDAO.class);
    public boolean save(SavedArticle saved) {
        String sql = "INSERT INTO saved_articles (user_id, article_id) VALUES (?, ?)";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, saved.getUserId());
            ps.setInt(2, saved.getArticleId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("Error in saving article", e);
            return false;
        }
    }

    public List<NewsArticle> findByUserId(int userId) {
        List<NewsArticle> articles = new ArrayList<>();
        String sql = "SELECT na.* FROM saved_articles sa JOIN news_articles na ON sa.article_id = na.id WHERE sa.user_id = ? ORDER BY sa.saved_at DESC";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                NewsArticle article = new NewsArticle();
                article.setId(rs.getInt("id"));
                article.setTitle(rs.getString("title"));
                article.setContent(rs.getString("content"));
                article.setSource(rs.getString("source"));
                article.setCategory(rs.getString("category"));
                article.setUrl(rs.getString("url"));
                article.setPublishedAt(rs.getString("published_at"));
                articles.add(article);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("Error in getting saved articles", e);
        }
        return articles;
    }

    public boolean delete(int userId, int articleId) {
        String sql = "DELETE FROM saved_articles WHERE user_id = ? AND article_id = ?";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, articleId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("Error in deleting the saved articles", e);
            return false;
        }
    }

    public boolean saveUserArticleHistory(int userId , int articleId) {
        String sql = "INSERT INTO article_reads (user_id, article_id) VALUES (?, ?)";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, articleId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("Error in saving user history", e);
            return false;
        }
    }
}
