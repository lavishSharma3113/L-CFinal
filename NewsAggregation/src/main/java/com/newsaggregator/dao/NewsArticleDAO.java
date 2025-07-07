package com.newsaggregator.dao;

import com.newsaggregator.model.NewsArticle;
import com.newsaggregator.utils.DBConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;

public class NewsArticleDAO {
    private static final Logger logger = LoggerFactory.getLogger(NewsArticleDAO.class);
    private final NewsArticleRecommendDAO recommendDao = new NewsArticleRecommendDAO();

    public void save(NewsArticle article) {
        String sql = "INSERT INTO news_articles (title, content, source, category, url, published_at) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, article.getTitle());
            ps.setString(2, article.getContent());
            ps.setString(3, article.getSource());
            ps.setString(4, article.getCategory());
            ps.setString(5, article.getUrl());
            ps.setString(6, article.getPublishedAt());
            ps.executeUpdate();

        } catch (SQLException e) {
            logger.error("Error saving article", e);
        }
    }

    public List<NewsArticle> findAll(String startDate, String endDate, int userId) {
        String sql = "SELECT * FROM news_articles WHERE is_hidden = FALSE AND DATE(published_at) BETWEEN ? AND ? AND category_id IN (SELECT category_id FROM NewsCategories WHERE is_hidden = FALSE) ORDER BY published_at DESC";
        Map<Integer, NewsArticle> articleMap = new LinkedHashMap<>();

        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, startDate);
            ps.setString(2, endDate);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    NewsArticle article = extractArticle(rs);
                    articleMap.put(article.getId(), article);
                }
            }

            List<NewsArticle> recommended = recommendDao.getRecommendedArticlesForDateRange(userId, startDate, endDate);
            if (recommended != null) {
                for (NewsArticle article : recommended) {
                    articleMap.putIfAbsent(article.getId(), article);
                }
            }
        } catch (SQLException e) {
            logger.error("Error fetching articles for date range", e);
        }
        return new ArrayList<>(articleMap.values());
    }

    public List<NewsArticle> findByDateRangeAndCategory(String startDate, String endDate, String category) {
        String sql = "SELECT * FROM news_articles WHERE is_hidden = FALSE AND DATE(published_at) BETWEEN ? AND ? AND category = ? AND category_id IN (SELECT category_id FROM NewsCategories WHERE is_hidden = FALSE) ORDER BY published_at DESC";
        List<NewsArticle> articles = new ArrayList<>();

        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, startDate);
            ps.setString(2, endDate);
            ps.setString(3, category);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    articles.add(extractArticle(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error fetching articles by category", e);
        }
        return articles;
    }

    public List<NewsArticle> findToday(int userId) {
        String sql = "SELECT na.* FROM news_articles na JOIN NewsCategories nc ON na.category_id = nc.category_id WHERE na.is_hidden = FALSE AND nc.is_hidden = FALSE AND DATE(na.created_at) = CURDATE() ORDER BY na.published_at DESC";
        Map<Integer, NewsArticle> articleMap = new HashMap<>();

        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                NewsArticle article = extractArticle(rs);
                articleMap.put(article.getId(), article);
            }

            List<NewsArticle> recommended = getRecommendedNews(userId);
            if (recommended != null) {
                for (NewsArticle article : recommended) {
                    articleMap.putIfAbsent(article.getId(), article);
                }
            }
        } catch (SQLException e) {
            logger.error("Error fetching today's articles", e);
        }
        return new ArrayList<>(articleMap.values());
    }

    public List<NewsArticle> findByKeyword(String keyword) {
        String sql = "SELECT na.* FROM news_articles na JOIN NewsCategories nc ON na.category_id = nc.category_id WHERE na.is_hidden = FALSE AND nc.is_hidden = FALSE AND (na.title LIKE ? OR na.content LIKE ?) ORDER BY na.published_at DESC";
        List<NewsArticle> articles = new ArrayList<>();

        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    articles.add(extractArticle(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error searching articles by keyword", e);
        }
        return articles;
    }

    public List<NewsArticle> findMostLiked() {
        String sql = "SELECT na.*, COUNT(ar.id) AS like_count FROM news_articles na JOIN article_reactions ar ON na.id = ar.article_id WHERE na.is_hidden = FALSE AND ar.reaction_type = 'like' GROUP BY na.id ORDER BY like_count DESC LIMIT 10";
        List<NewsArticle> articles = new ArrayList<>();

        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                articles.add(extractArticle(rs));
            }
        } catch (SQLException e) {
            logger.error("Error fetching most liked articles", e);
        }
        return articles;
    }

    private List<NewsArticle> getRecommendedNews(int userId) {
        List<NewsArticle> recommended = new ArrayList<>();

        List<NewsArticle> likeBased = recommendDao.getRecommendedArticlesForToday(userId);
        List<NewsArticle> historyBased = recommendDao.getUserHistoryRecommendedArticles(userId);
        List<NewsArticle> savedBased = recommendDao.getUserSavedRecommendedArticles(userId);

        if (likeBased != null) recommended.addAll(likeBased);
        if (historyBased != null) recommended.addAll(historyBased);
        if (savedBased != null) recommended.addAll(savedBased);

        return recommended;
    }

    private NewsArticle extractArticle(ResultSet rs) throws SQLException {
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
