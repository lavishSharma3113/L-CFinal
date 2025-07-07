package com.newsaggregator.dao;

import com.newsaggregator.model.ExternalServer;
import com.newsaggregator.utils.DBConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExternalServerDAO {
    public boolean save(ExternalServer server) {
        String sql = "INSERT INTO ExternalServers (name, api_key, base_url, is_active, last_accessed) VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE last_accessed = VALUES(last_accessed), is_active = VALUES(is_active)";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, server.getName());
            ps.setString(2, server.getApiKey());
            ps.setString(3, server.getBaseUrl());
            ps.setBoolean(4, server.isActive());
            ps.setString(5, server.getLastAccessed());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<ExternalServer> findAll() {
        List<ExternalServer> servers = new ArrayList<>();
        String sql = "SELECT * FROM ExternalServers";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ExternalServer server = new ExternalServer();
                server.setId(rs.getInt("server_id"));
                server.setName(rs.getString("name"));
                server.setApiKey(rs.getString("api_key"));
                server.setBaseUrl(rs.getString("base_url"));
                server.setActive(rs.getBoolean("is_active"));
                server.setLastAccessed(rs.getString("last_accessed"));
                servers.add(server);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return servers;
    }
}
