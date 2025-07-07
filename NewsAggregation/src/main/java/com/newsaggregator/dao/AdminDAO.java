package com.newsaggregator.dao;

import com.newsaggregator.model.ExternalServer;
import com.newsaggregator.model.NewsCategory;
import com.newsaggregator.utils.DBConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminDAO {
    public List<ExternalServer> getAllExternalServers() {
        List<ExternalServer> servers = new ArrayList<>();
        String sql = "SELECT * FROM ExternalServers";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ExternalServer s = new ExternalServer();
                s.setId(rs.getInt("server_id"));
                s.setName(rs.getString("name"));
                s.setApiKey(rs.getString("api_key"));
                s.setBaseUrl(rs.getString("base_url"));
                s.setActive(rs.getBoolean("is_active"));
                s.setLastAccessed(rs.getString("last_accessed"));
                servers.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return servers;
    }

    public ExternalServer getExternalServerById(int id) {
        String sql = "SELECT * FROM ExternalServers WHERE server_id = ?";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ExternalServer s = new ExternalServer();
                s.setId(rs.getInt("server_id"));
                s.setName(rs.getString("name"));
                s.setApiKey(rs.getString("api_key"));
                s.setBaseUrl(rs.getString("base_url"));
                s.setActive(rs.getBoolean("is_active"));
                s.setLastAccessed(rs.getString("last_accessed"));
                return s;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateExternalServer(ExternalServer s) {
        String sql = "UPDATE ExternalServers SET name = ?, api_key = ?, base_url = ?, is_active = ? WHERE server_id = ?";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getName());
            ps.setString(2, s.getApiKey());
            ps.setString(3, s.getBaseUrl());
            ps.setBoolean(4, s.isActive());
            ps.setInt(5, s.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
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
            e.printStackTrace();
            return false;
        }
    }
}

