package com.newsaggregator.client.service;

import com.newsaggregator.client.model.NewsArticle;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class NewsClient {
    private static final String BASE_URL = "http://localhost:8080/NewsAggregation/news";
    private final Gson gson = new Gson();

    public List<NewsArticle> getHeadlines() {
        return fetchArticles(BASE_URL + "?action=today");
    }

    public List<NewsArticle> searchByKeyword(String keyword) {
        return fetchArticles(BASE_URL + "?action=keyword&keyword=" + keyword);
    }

    public List<NewsArticle> searchByDateRange(String start, String end, String category) {
        return fetchArticles(BASE_URL + "?action=range&start=" + start + "&end=" + end + "&category=" + category);
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
}
