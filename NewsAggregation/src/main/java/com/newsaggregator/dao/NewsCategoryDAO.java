package com.newsaggregator.dao;

import com.newsaggregator.model.NewsArticle;
import com.newsaggregator.model.NewsCategory;
import com.newsaggregator.utils.DBConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NewsCategoryDAO {
    private static final Logger logger = LoggerFactory.getLogger(NewsCategoryDAO.class);

    public List<NewsCategory> getAllNewsCategories() {
        List<NewsCategory> categories = new ArrayList<>();
        String sql = "SELECT category_id, name, is_hidden FROM NewsCategories";

        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                NewsCategory category = new NewsCategory();
                category.setId(rs.getInt("category_id"));
                category.setName(rs.getString("name"));
                category.setIsHidden(rs.getBoolean("is_hidden"));
                categories.add(category);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("Error in fetching the categories", e);
        }

        return categories;
    }

    public boolean hideCategory(int categoryId, boolean isHidden) {
        String sql = "UPDATE NewsCategories SET is_hidden = ? WHERE category_id = ?";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1,isHidden);
            ps.setInt(2, categoryId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("Error in hiding the category", e);
            return false;
        }
    }

    public boolean insertNewsCategories() {
        String query = "UPDATE news_articles na\n" +
                "JOIN NewsCategories nc ON na.category = nc.name\n" +
                "SET na.category_id = nc.category_id\n" +
                "WHERE na.category_id IS NULL";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("Error in inserting news Category", e);
            return false;
        }
    }

    public boolean insertCategoriesInCategoryTable() {
        String query = "INSERT INTO NewsCategories (name)\n" +
                "SELECT DISTINCT na.category\n" +
                "FROM news_articles na\n" +
                "LEFT JOIN NewsCategories nc ON na.category = nc.name\n" +
                "WHERE nc.category_id IS NULL AND na.category IS NOT NULL";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("Error in inserting category to table", e);
            return false;
        }
    }

}
