package com.newsaggregator.dao;

import com.newsaggregator.model.NotificationConfig;
import com.newsaggregator.utils.DBConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationConfigDAO {
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
            return false;
        }
    }

    public List<NotificationConfig> getConfigsByUser(int userId) {
        List<NotificationConfig> list = new ArrayList<>();
        String sql = "SELECT * FROM notification_config WHERE user_id = ?";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                NotificationConfig config = new NotificationConfig();
                config.setId(rs.getInt("id"));
                config.setUserId(rs.getInt("user_id"));
                config.setCategoryId(rs.getInt("category_id"));
                config.setEnabled(rs.getBoolean("is_enabled"));
                list.add(config);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}

