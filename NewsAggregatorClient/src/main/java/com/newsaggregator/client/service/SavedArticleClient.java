package com.newsaggregator.client.service;

import com.newsaggregator.client.model.NewsArticle;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class SavedArticleClient {
    private static final String BASE_URL = "http://localhost:8080/NewsAggregation/saved";
    private static final String BASE_URL_HISTORY = "http://localhost:8080/NewsAggregation/saveHistory";
    private final Gson gson = new Gson();

    public boolean saveArticle(int userId, int articleId) {
        try {
            URL url = new URL(BASE_URL + "?articleId=" + articleId + "&userId=" + userId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String json = in.readLine();
            return json.contains("\"success\"");
        } catch (Exception e) {
            System.err.println("Error saving article: " + e.getMessage());
            return false;
        }
    }

    public List<NewsArticle> getSavedArticles(int userId) {
        try {
            URL url = new URL(BASE_URL + "?userId=" + userId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder json = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) json.append(line);

            return gson.fromJson(json.toString(), new TypeToken<List<NewsArticle>>() {}.getType());
        } catch (Exception e) {
            System.err.println("Error fetching saved articles: " + e.getMessage());
            return List.of();
        }
    }

    public boolean deleteSavedArticle(int userId, int articleId) {
        try {
            URL url = new URL(BASE_URL + "?userId=" + userId + "&articleId=" + articleId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String response = in.readLine();
            return response != null && response.contains("success");
        } catch (Exception e) {
            System.err.println("Error deleting article: " + e.getMessage());
            return false;
        }
    }

    public boolean saveUserArticleHistory(int userId, int articleId) {
        try {
            URL url = new URL(BASE_URL_HISTORY + "?articleId=" + articleId + "&userId=" + userId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String json = in.readLine();
            return json.contains("\"success\"");
        } catch (Exception e) {
            System.err.println("Error saving article: " + e.getMessage());
            return false;
        }
    }

}