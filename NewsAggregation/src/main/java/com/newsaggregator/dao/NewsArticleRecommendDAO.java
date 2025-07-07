package com.newsaggregator.dao;

import com.newsaggregator.model.NewsArticle;
import com.newsaggregator.utils.DBConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class NewsArticleRecommendDAO {
    private static final Logger logger = LoggerFactory.getLogger(NewsArticleRecommendDAO.class);

    public List<NewsArticle> getRecommendedArticlesForToday(int userId) {
        String sql = """
            SELECT *
            FROM news_articles
            WHERE category IN (
                SELECT DISTINCT category
                FROM news_articles
                WHERE id IN (
                    SELECT article_id
                    FROM article_reactions
                    WHERE user_id = ? AND reaction_type = 'like'
                )
            )
            AND is_hidden = FALSE
            AND DATE(created_at) = CURDATE()
            AND id NOT IN (
                SELECT article_id
                FROM article_reactions
                WHERE user_id = ?
            )
            ORDER BY published_at DESC
            LIMIT 10
        """;

        return fetchRecommendedArticles(sql, ps -> {
            ps.setInt(1, userId);
            ps.setInt(2, userId);
        });
    }

    public List<NewsArticle> getRecommendedArticlesForDateRange(int userId, String start, String end) {
        String sql = """
            SELECT *
            FROM news_articles
            WHERE category IN (
                SELECT DISTINCT category
                FROM news_articles
                WHERE id IN (
                    SELECT article_id
                    FROM article_reactions
                    WHERE user_id = ? AND reaction_type = 'like'
                )
            )
            AND is_hidden = FALSE
            AND DATE(published_at) BETWEEN ? AND ?
            AND id NOT IN (
                SELECT article_id
                FROM article_reactions
                WHERE user_id = ?
            )
            ORDER BY published_at DESC
            LIMIT 10
        """;

        return fetchRecommendedArticles(sql, ps -> {
            ps.setInt(1, userId);
            ps.setString(2, start);
            ps.setString(3, end);
            ps.setInt(4, userId);
        });
    }

    public List<NewsArticle> getUserSavedRecommendedArticles(int userId) {
        String sql = """
            SELECT *
            FROM news_articles
            WHERE category IN (
                SELECT DISTINCT category
                FROM news_articles
                WHERE id IN (
                    SELECT article_id
                    FROM saved_articles
                    WHERE user_id = ?
                )
            )
            AND is_hidden = FALSE
             AND DATE(created_at) = CURDATE()
            AND id NOT IN (
                SELECT article_id
                FROM saved_articles
                WHERE user_id = ?
            )
            ORDER BY published_at DESC
            LIMIT 10
        """;
        return fetchRecommendedArticles(sql, ps -> {
            ps.setInt(1, userId);
            ps.setInt(2, userId);
        });
    }

    public List<NewsArticle> getUserHistoryRecommendedArticles(int userId) {
        String sql = """
            SELECT *
            FROM news_articles
            WHERE category IN (
                SELECT DISTINCT category
                FROM news_articles
                WHERE id IN (
                    SELECT article_id
                    FROM article_reads
                    WHERE user_id = ?
                )
            )
            AND is_hidden = FALSE
             AND DATE(created_at) = CURDATE()
            AND id NOT IN (
                SELECT article_id
                FROM article_reads
                WHERE user_id = ?
            )
            ORDER BY published_at DESC
            LIMIT 10
        """;
        return fetchRecommendedArticles(sql, ps -> {
            ps.setInt(1, userId);
            ps.setInt(2, userId);
        });
    }

    private List<NewsArticle> fetchRecommendedArticles(String sql, PreparedStatementFiller filler) {
        List<NewsArticle> articles = new ArrayList<>();
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            filler.fill(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    articles.add(mapResultSetToArticle(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("Error fetching recommended articles", e);
        }
        return articles;
    }

    private NewsArticle mapResultSetToArticle(ResultSet rs) throws SQLException {
        NewsArticle article = new NewsArticle();
        article.setId(rs.getInt("id"));
        article.setTitle(rs.getString("title"));
        article.setContent(rs.getString("content"));
        article.setCategory(rs.getString("category"));
        article.setSource(rs.getString("source"));
        article.setUrl(rs.getString("url"));
        article.setPublishedAt(rs.getString("published_at"));
        return article;
    }

    @FunctionalInterface
    private interface PreparedStatementFiller {
        void fill(PreparedStatement ps) throws SQLException;
    }
}
