package com.newsaggregator.dao;

import com.newsaggregator.model.NotificationConfig;
import com.newsaggregator.utils.DBConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationConfigDAO {
    private static final Logger logger = LoggerFactory.getLogger(NotificationDAO.class);
    public boolean saveOrUpdate(NotificationConfig config) {
        String sql = "REPLACE INTO notification_config (user_id, category_id, is_enabled) VALUES (?, ?, ?)";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, config.getUserId());
            ps.setInt(2, config.getCategoryId());
            ps.setBoolean(3, config.isEnabled());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("Error updating notification", e);
            return false;
        }
    }

    public List<NotificationConfig> getConfigsByUser(int userId) {
        List<NotificationConfig> list = new ArrayList<>();

        String sql = "SELECT c.category_id, c.name AS category_name, " +
                "nc.id, nc.is_enabled " +
                "FROM NewsCategories c " +
                "LEFT JOIN notification_config nc ON c.category_id = nc.category_id AND nc.user_id = ?";

        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int categoryId = rs.getInt("category_id");
                String categoryName = rs.getString("category_name");

                boolean isEnabled;
                int configId = rs.getInt("id");
                boolean hasConfig = !rs.wasNull();

                if (hasConfig) {
                    isEnabled = rs.getBoolean("is_enabled");
                } else {

                    insertDefaultDisabledConfig(userId, categoryId);
                    isEnabled = false;
                }

                NotificationConfig config = new NotificationConfig();
                config.setId(configId);
                config.setUserId(userId);
                config.setCategoryId(categoryId);
                config.setCategoryName(categoryName);
                config.setEnabled(isEnabled);

                list.add(config);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("Error fetching recommended articles", e);
        }

        return list;
    }


    private void insertDefaultDisabledConfig(int userId, int categoryId) {
        String insertSql = "INSERT INTO notification_config (user_id, category_id, is_enabled) VALUES (?, ?, false)";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(insertSql)) {

            ps.setInt(1, userId);
            ps.setInt(2, categoryId);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("Error fetching recommended articles", e);
        }
    }

    public boolean insertUserKeyword(int userId, String keyword) {
        String insertSql = "Insert INTO user_keywords (user_id, keyword) VALUES (? , ?)";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(insertSql)) {

            ps.setInt(1, userId);
            ps.setString(2, keyword);
            return ps.executeUpdate()  > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("Error fetching recommended articles", e);
            return false;
        }
    }

}

