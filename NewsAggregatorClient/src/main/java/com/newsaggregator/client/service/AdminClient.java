package com.newsaggregator.client.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.newsaggregator.client.model.ExternalServer;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

public class AdminClient {
    private static final String BASE_URL = "http://localhost:8080/NewsAggregation/admin";
    private final Gson gson = new Gson();

    public List<ExternalServer> getAllServers() {
        try {
            URL url = new URL(BASE_URL + "?action=listServers");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            return gson.fromJson(reader, new TypeToken<List<ExternalServer>>() {}.getType());
        } catch (Exception e) {
            System.err.println("Error fetching server list: " + e.getMessage());
            return List.of();
        }
    }

    public ExternalServer getServerById(int serverId) {
        try {
            URL url = new URL(BASE_URL + "?action=viewServer&serverId=" + serverId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            return gson.fromJson(reader, ExternalServer.class);
        } catch (Exception e) {
            System.err.println("Error fetching server details: " + e.getMessage());
            return null;
        }
    }

    public boolean updateServer(ExternalServer server) {
        try {
            URL url = new URL(BASE_URL + "?action=editServer");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");

            String json = gson.toJson(server);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes());
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String result = in.readLine();
            return result != null && result.contains("\"status\":\"success\"");
        } catch (Exception e) {
            System.err.println("Error updating server: " + e.getMessage());
            return false;
        }
    }

    public boolean addCategory(String categoryName) {
        try {
            String encodedName = URLEncoder.encode(categoryName, "UTF-8");
            URL url = new URL(BASE_URL + "?action=addCategory&name=" + encodedName);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String result = in.readLine();
            return result != null && result.contains("\"status\":\"success\"");
        } catch (Exception e) {
            System.err.println("Error adding category: " + e.getMessage());
            return false;
        }
    }

    public boolean hideArticle(int articleId) {
        try {
            URL url = new URL(BASE_URL + "?action=hideArticle&articleId=" + articleId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String result = in.readLine();
            return result != null && result.contains("\"status\":\"success\"");
        } catch (Exception e) {
            System.err.println("Error hiding article: " + e.getMessage());
            return false;
        }
    }

    public boolean hideArticleKeyword( String keyword) {
        try {
            URL url = new URL(BASE_URL + "?action=hideArticleWithKeyword&keyword=" +keyword);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String result = in.readLine();
            return result != null && result.contains("\"status\":\"success\"");
        } catch (Exception e) {
            System.err.println("Error hiding article: " + e.getMessage());
            return false;
        }
    }

    public boolean hideCategory(int categoryId, boolean isHidden) {
        try{
            URL url = new URL(BASE_URL + "?action=hideCategory&categoryId=" +categoryId + "&isHidden=" + isHidden);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String result = in.readLine();
            return result != null && result.contains("\"status\":\"success\"");
        } catch (Exception e) {
            System.err.println("Error hiding category:");
            return false;
        }
    }

}

