package com.newsaggregator.client.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.newsaggregator.client.model.ExternalServer;
import com.newsaggregator.client.model.NewsArticle;

import java.io.*;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

public class NewsReportClient {
    private static final String BASE_URL = "http://localhost:8080/NewsAggregation/report";
    private final Gson gson = new Gson();

    public List<NewsArticle> getAllNewsReport() {
        try {
            URL url = new URL(BASE_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder json = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }

            Type listType = new TypeToken<List<NewsArticle>>() {
            }.getType();

            return gson.fromJson(json.toString(), listType);
        } catch (Exception e) {
            System.err.println("Error fetching reports");
            return List.of();
        }
    }

    public boolean saveReport(int userId , int articleId) {
        try {
            URL url = new URL(BASE_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            String body =  "userId=" + userId +
                    "&articleId=" + articleId;


            try (OutputStream os = conn.getOutputStream()) {
                os.write(body.getBytes());
            }

            int status = conn.getResponseCode();
            return status == HttpURLConnection.HTTP_OK || status == HttpURLConnection.HTTP_NO_CONTENT;

        } catch (Exception e) {
            System.err.println("Error in reporting article");
            return false;
        }
    }

}
