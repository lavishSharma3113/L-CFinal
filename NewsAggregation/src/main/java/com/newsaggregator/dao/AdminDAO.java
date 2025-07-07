package com.newsaggregator.dao;

import com.newsaggregator.model.ExternalServer;
import com.newsaggregator.model.NewsCategory;
import com.newsaggregator.utils.DBConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminDAO {
    private static final Logger logger = LoggerFactory.getLogger(AdminDAO.class);

    public List<ExternalServer> getAllExternalServers() {
        List<ExternalServer> servers = new ArrayList<>();
        String sql = "SELECT * FROM ExternalServers";

        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                servers.add(mapExternalServer(rs));
            }

        } catch (SQLException e) {
            logger.error("Error fetching all external servers", e);
        }
        return servers;
    }

    public ExternalServer getExternalServerById(int id) {
        String sql = "SELECT * FROM ExternalServers WHERE server_id = ?";

        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapExternalServer(rs);
                }
            }

        } catch (SQLException e) {
            logger.error("Error fetching external server by ID: {}", id, e);
        }

        return null;
    }

    public boolean updateExternalServer(ExternalServer server) {
        String sql = "UPDATE ExternalServers SET api_key = ? WHERE server_id = ?";

        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, server.getApiKey());
            ps.setInt(2, server.getId());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            logger.error("Error updating external server with ID: {}", server.getId(), e);
            return false;
        }
    }

    public boolean addNewsCategory(NewsCategory category) {
        String sql = "INSERT INTO NewsCategories (name) VALUES (?)";

        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, category.getName());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            logger.error("Error adding news category: {}", category.getName(), e);
            return false;
        }
    }

    public boolean hideArticle(int articleId) {
        String sql = "UPDATE news_articles SET is_hidden = TRUE WHERE id = ?";

        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, articleId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            logger.error("Error hiding article with ID: {}", articleId, e);
            return false;
        }
    }

    public boolean hideArticle(String keyword) {
        String sql = "UPDATE news_articles SET is_hidden = TRUE WHERE title LIKE ? OR content LIKE ?";

        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String wildcardKeyword = "%" + keyword + "%";
            ps.setString(1, wildcardKeyword);
            ps.setString(2, wildcardKeyword);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            logger.error("Error hiding articles with keyword: {}", keyword, e);
            return false;
        }
    }

    public boolean hideCategory(int categoryId, boolean isHidden) {
        String sql = "UPDATE NewsCategories SET is_hidden = ? WHERE category_id = ?";

        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBoolean(1, isHidden);
            ps.setInt(2, categoryId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            logger.error("Error hiding category ID: {} with status: {}", categoryId, isHidden, e);
            return false;
        }
    }


    private ExternalServer mapExternalServer(ResultSet rs) throws SQLException {
        ExternalServer server = new ExternalServer();
        server.setId(rs.getInt("server_id"));
        server.setName(rs.getString("name"));
        server.setApiKey(rs.getString("api_key"));
        server.setBaseUrl(rs.getString("base_url"));
        server.setActive(rs.getBoolean("is_active"));
        server.setLastAccessed(rs.getString("last_accessed"));
        return server;
    }
}
