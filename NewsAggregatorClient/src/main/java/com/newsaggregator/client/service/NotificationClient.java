package com.newsaggregator.client.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.newsaggregator.client.model.NewsArticle;
import com.newsaggregator.client.model.NewsCategory;
import com.newsaggregator.client.model.Notification;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class NotificationClient {
    private static final String BASE_URL = "http://localhost:8080/NewsAggregation/notifications";
    private final Gson gson = new Gson();

    public List<NewsArticle> fetchUserNotifications(int userId) {
        try {
            URL url = new URL(BASE_URL + "?userId=" + userId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            return gson.fromJson(reader, new TypeToken<List<NewsArticle>>() {}.getType());
        } catch (Exception e) {
            System.err.println("Error fetching notifications: " + e.getMessage());
            return List.of();
        }
    }


}
