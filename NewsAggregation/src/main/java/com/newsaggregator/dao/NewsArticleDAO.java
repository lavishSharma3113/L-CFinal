package com.newsaggregator.dao;

import com.newsaggregator.model.NewsArticle;
import com.newsaggregator.utils.DBConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NewsArticleDAO {
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
            e.printStackTrace();
        }
    }

    public List<NewsArticle> findAll() {
        List<NewsArticle> articles = new ArrayList<>();
        String sql = "SELECT * FROM news_articles ORDER BY published_at DESC";

        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                NewsArticle article = new NewsArticle();
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
        }
        return articles;
    }

    public List<NewsArticle> findByDateRangeAndCategory(String startDate, String endDate, String category) {
        List<NewsArticle> articles = new ArrayList<>();
        String sql = "SELECT * FROM news_articles WHERE DATE(published_at) BETWEEN ? AND ? AND category = ? ORDER BY published_at DESC";

        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, startDate);
            ps.setString(2, endDate);
            ps.setString(3, category);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    NewsArticle article = new NewsArticle();
                    article.setTitle(rs.getString("title"));
                    article.setContent(rs.getString("content"));
                    article.setSource(rs.getString("source"));
                    article.setCategory(rs.getString("category"));
                    article.setUrl(rs.getString("url"));
                    article.setPublishedAt(rs.getString("published_at"));
                    articles.add(article);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return articles;
    }

    public List<NewsArticle> findToday() {
        List<NewsArticle> articles = new ArrayList<>();
        String sql = "SELECT * FROM news_articles WHERE DATE(published_at) = CURDATE() ORDER BY published_at DESC";

        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                NewsArticle article = new NewsArticle();
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
        }
        return articles;
    }

    public List<NewsArticle> findByKeyword(String keyword) {
        List<NewsArticle> articles = new ArrayList<>();
        String sql = "SELECT * FROM news_articles WHERE title LIKE ? OR content LIKE ? ORDER BY published_at DESC";

        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    NewsArticle article = new NewsArticle();
                    article.setTitle(rs.getString("title"));
                    article.setContent(rs.getString("content"));
                    article.setSource(rs.getString("source"));
                    article.setCategory(rs.getString("category"));
                    article.setUrl(rs.getString("url"));
                    article.setPublishedAt(rs.getString("published_at"));
                    articles.add(article);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return articles;
    }
}
