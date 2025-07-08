package com.newsaggregator.service.impl;

import com.newsaggregator.dao.NewsCategoryDAO;
import com.newsaggregator.model.NewsCategory;
import com.newsaggregator.service.INewsCategoryService;

import java.util.List;

public class NewsArticleCategoryImpl implements INewsCategoryService {
    private final NewsCategoryDAO dao = new NewsCategoryDAO();
    @Override
    public List<NewsCategory> getAllNewsCategories() {
        return dao.getAllNewsCategories();
    }

    public boolean hideCategory(int categoryId, boolean isHidden) {
        return dao.hideCategory(categoryId, isHidden);
    }

    public boolean insertNewsCategories() {
        return dao.insertNewsCategories();
    }

    public boolean insertCategoriesInCategoryTable() {
        return dao.insertCategoriesInCategoryTable();
    }
}
