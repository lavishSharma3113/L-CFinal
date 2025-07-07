package com.newsaggregator.dao.impl;

import com.newsaggregator.dao.IUserDAO;
import com.newsaggregator.exception.DatabaseOperationException;
import com.newsaggregator.model.User;
import com.newsaggregator.utils.DBConnectionManager;

import java.sql.*;

public class UserDAOImpl implements IUserDAO {
    @Override
    public User getUserByEmail(String email) {
        try (Connection conn = DBConnectionManager.getConnection()) {
            String sql = "SELECT * FROM users WHERE email=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password_hash"));
                user.setRole(rs.getString("role"));
                return user;
            }
        } catch (SQLException e) {
           throw new DatabaseOperationException(e.getMessage());
        }
        return null;
    }

    @Override
    public boolean createUser(User user) {
        try (Connection conn = DBConnectionManager.getConnection()) {
            String sql = "INSERT INTO users (username, email, password_hash, role) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getRole());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseOperationException(e.getMessage());
        }
    }

    @Override
    public boolean validateLogin(String email, String password) {
        try (Connection conn = DBConnectionManager.getConnection()) {
            String sql = "SELECT * FROM users WHERE email=? AND password_hash=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new DatabaseOperationException(e.getMessage());
        }
    }
}
