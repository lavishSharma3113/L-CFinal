package com.newsaggregator.service;

import com.newsaggregator.model.NewsCategory;

import java.util.List;

public interface INewsCategoryService {
    List<NewsCategory> getAllNewsCategories();
    boolean hideCategory(int categoryId, boolean isHidden);
    boolean insertNewsCategories();
    boolean insertCategoriesInCategoryTable();
}
