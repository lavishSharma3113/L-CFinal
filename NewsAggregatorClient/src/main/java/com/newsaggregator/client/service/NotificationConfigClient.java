package com.newsaggregator.client.service;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.newsaggregator.client.model.NotificationConfig;

import java.io.*;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class NotificationConfigClient {
    private static final String BASE_URL = "http://localhost:8080/NewsAggregation/notification-config";
    private final Gson gson = new Gson();

    public boolean updateConfig(int userId, int categoryId, boolean isEnabled) {
        try {
            URL url = new URL(BASE_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            String body = "userId=" + userId +
                    "&categoryId=" + categoryId +
                    "&enabled=" + isEnabled;

            try (OutputStream os = conn.getOutputStream()) {
                os.write(body.getBytes());
            }

            int status = conn.getResponseCode();
            return status == HttpURLConnection.HTTP_OK || status == HttpURLConnection.HTTP_NO_CONTENT;

        } catch (Exception e) {
            System.err.println("Error updating config: " + e.getMessage());
            return false;
        }
    }

    public boolean insertUserKeyword(int userId , String keyword) {
        try {
            URL url = new URL(BASE_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            String body = "userId=" + userId +
                    "&keyword=" + keyword;

            try (OutputStream os = conn.getOutputStream()) {
                os.write(body.getBytes());
            }

            int status = conn.getResponseCode();
            return status == HttpURLConnection.HTTP_OK || status == HttpURLConnection.HTTP_NO_CONTENT;

        } catch (Exception e) {
            System.err.println("Error updating config: " + e.getMessage());
            return false;
        }
    }
}
