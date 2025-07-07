package com.newsaggregator.client.service;


import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class ArticleReactionClient {
    private static final String BASE_URL = "http://localhost:8080/NewsAggregation/news";
    public boolean likeArticle(int userId, int articleId) {
        return sendReaction(userId, articleId, "like");
    }

    public boolean dislikeArticle(int userId, int articleId) {
        return sendReaction(userId, articleId, "dislike");
    }

    private boolean sendReaction(int userId, int articleId, String type) {
        try {
            String urlStr = BASE_URL + "?action=" + type;
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            String postData = "userId=" + userId + "&articleId=" + articleId;
            try (OutputStream os = conn.getOutputStream()) {
                os.write(postData.getBytes());
            }

            return conn.getResponseCode() == 200;

        } catch (Exception e) {
            System.err.println("Error sending " + type + ": " + e.getMessage());
            return false;
        }
    }


}
