package com.newsaggregator.client.service;

import com.newsaggregator.client.model.NewsArticle;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.newsaggregator.client.model.NewsCategory;
import com.newsaggregator.client.model.NotificationConfig;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class NewsClient {
    private static final String BASE_URL = "http://localhost:8080/NewsAggregation/news";
    private final Gson gson = new Gson();

    public List<NewsArticle> getHeadlines(int userId) {
        return fetchArticles(BASE_URL + "?action=today&userId="+userId);
    }

    public List<NewsArticle> searchByKeyword(String keyword) {
        return fetchArticles(BASE_URL + "?action=keyword&keyword=" + keyword);
    }

    public List<NewsArticle> searchByDateRange(String start, String end, String category) {
        return fetchArticles(BASE_URL + "?action=range&start=" + start + "&end=" + end + "&category=" + category);
    }
    public List<NewsArticle> searchAllCategoryNews(String start, String end, int userId) {
        return fetchArticles(BASE_URL + "?action=all&start=" + start + "&end=" + end+"&userId="+userId);
    }

    public List<NewsArticle> searchMostLiked() {
        return fetchArticles(BASE_URL + "?action=mostLiked");
    }

    private List<NewsArticle> fetchArticles(String urlStr) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder json = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }

            Type listType = new TypeToken<List<NewsArticle>>() {}.getType();

            return gson.fromJson(json.toString(), listType);
        } catch (Exception e) {
            System.err.println("Error fetching articles: " + e.getMessage());
            return List.of();
        }
    }

    public List<NewsCategory> getAllNewsCategories() {
        return fetchNewsCategories(BASE_URL+ "?action=categories");

    }

    private List<NewsCategory> fetchNewsCategories(String urlStr) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder json = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }

            Type listType = new TypeToken<List<NewsCategory>>() {}.getType();

            return gson.fromJson(json.toString(), listType);
        } catch (Exception e) {
            System.err.println("Error getting news categories: " + e.getMessage());
            return List.of();
        }
    }

    public List<NotificationConfig> getNotificationCategories(int userId) {
        String urlStr = "http://localhost:8080/NewsAggregation/notification-config?userId=" + userId;

        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder json = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }

            Type listType = new TypeToken<List<NotificationConfig>>() {}.getType();
            return gson.fromJson(json.toString(), listType);

        } catch (Exception e) {
            System.err.println("Error fetching notification configs: " + e.getMessage());
            return List.of();
        }
    }

}
