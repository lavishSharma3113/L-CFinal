package com.newsaggregator.dao;

import com.newsaggregator.model.ArticleReport;
import com.newsaggregator.model.NewsArticle;
import com.newsaggregator.utils.DBConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ArticleReportDAO {

    private static final Logger logger = LoggerFactory.getLogger(ArticleReportDAO.class);
    private static final int HIDE_THRESHOLD = 3;

    public boolean saveReport(ArticleReport report) {
        String insertSql = "INSERT INTO article_reports (article_id, user_id) VALUES (?, ?)";

        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(insertSql)) {

            ps.setInt(1, report.getArticleId());
            ps.setInt(2, report.getUserId());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                checkAndHideIfThresholdCrossed(conn, report.getArticleId());
                return true;
            }

        } catch (SQLException e) {
            logger.error("Error saving article report for Article ID {}", report.getArticleId(), e);
        }

        return false;
    }

    private void checkAndHideIfThresholdCrossed(Connection conn, int articleId) {
        String countSql = "SELECT COUNT(*) FROM article_reports WHERE article_id = ?";
        String updateSql = "UPDATE news_articles SET is_hidden = TRUE WHERE id = ?";

        try (PreparedStatement countStmt = conn.prepareStatement(countSql)) {
            countStmt.setInt(1, articleId);
            ResultSet rs = countStmt.executeQuery();

            if (rs.next() && rs.getInt(1) >= HIDE_THRESHOLD) {
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setInt(1, articleId);
                    updateStmt.executeUpdate();
                    logger.info("Article ID {} auto-hidden after reaching report threshold.", articleId);
                }
            }

        } catch (SQLException e) {
            logger.error("Error checking or hiding article after threshold for Article ID {}", articleId, e);
        }
    }

    public List<NewsArticle> getAllReports() {
        List<NewsArticle> articles = new ArrayList<>();
        String sql = """
                SELECT na.*
                FROM news_articles na
                JOIN article_reports ar ON na.id = ar.article_id
                GROUP BY na.id
                ORDER BY MAX(ar.reported_at) DESC
                """;

        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                articles.add(mapNewsArticle(rs));
            }

        } catch (SQLException e) {
            logger.error("Error fetching reported articles", e);
        }

        return articles;
    }

    private NewsArticle mapNewsArticle(ResultSet rs) throws SQLException {
        NewsArticle article = new NewsArticle();
        article.setId(rs.getInt("id"));
        article.setTitle(rs.getString("title"));
        article.setContent(rs.getString("content"));
        article.setSource(rs.getString("source"));
        article.setCategory(rs.getString("category"));
        article.setUrl(rs.getString("url"));
        article.setPublishedAt(rs.getString("published_at"));
        return article;
    }
}
