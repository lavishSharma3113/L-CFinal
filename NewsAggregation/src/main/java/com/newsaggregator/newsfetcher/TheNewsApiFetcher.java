package com.newsaggregator.newsfetcher;

import com.newsaggregator.model.NewsArticle;
import com.newsaggregator.model.ExternalServer;
import com.newsaggregator.service.IAdminService;
import com.newsaggregator.service.impl.AdminServiceImpl;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;

public class TheNewsApiFetcher implements INewsFetcher {

    private static final String LOCALE = "us";
    private static final int LIMIT = 3;
    private static final int SERVER_ID = 11;

    private final IAdminService adminService;

    public TheNewsApiFetcher() {
        this(new AdminServiceImpl());
    }

    public TheNewsApiFetcher(IAdminService adminService) {
        this.adminService = adminService;
    }

    @Override
    public List<NewsArticle> fetchNews() {
        List<NewsArticle> articles = new ArrayList<>();

        try {
            String apiKey = getApiKey();
            if (apiKey == null || apiKey.isEmpty()) {
                System.err.println("[TheNewsApiFetcher] API key not found.");
                return articles;
            }

            String endpoint = buildEndpoint(apiKey);
            String jsonResponse = sendHttpGet(endpoint);
            JSONArray dataArray = new JSONObject(jsonResponse).optJSONArray("data");

            if (dataArray != null) {
                for (int i = 0; i < dataArray.length(); i++) {
                    JSONObject obj = dataArray.getJSONObject(i);
                    articles.add(parseArticle(obj));
                }
            }
        } catch (Exception e) {
            System.err.println("[TheNewsApiFetcher] Failed to fetch or parse news: " + e.getMessage());
            e.printStackTrace();
        }

        return articles;
    }

    private String getApiKey() {
        ExternalServer server = adminService.getExternalServerById(SERVER_ID);
        return server != null ? server.getApiKey() : null;
    }

    private String buildEndpoint(String apiKey) {
        return String.format("https://api.thenewsapi.com/v1/news/top?api_token=%s&locale=%s&limit=%d", apiKey, LOCALE, LIMIT);
    }

    private String sendHttpGet(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            StringBuilder responseContent = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                responseContent.append(line);
            }
            return responseContent.toString();
        }
    }

    private NewsArticle parseArticle(JSONObject obj) {
        NewsArticle article = new NewsArticle();

        article.setTitle(obj.optString("title"));
        article.setContent(obj.optString("description"));
        article.setSource(obj.optString("source", "Unknown"));
        article.setUrl(obj.optString("url"));
        article.setPublishedAt(parsePublishedAt(obj.optString("published_at")));
        article.setCategory(extractCategory(obj, article.getTitle(), article.getContent()));

        return article;
    }

    private String parsePublishedAt(String isoDate) {
        try {
            ZonedDateTime zonedDateTime = ZonedDateTime.parse(isoDate);
            LocalDateTime localDateTime = zonedDateTime.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
            return localDateTime.toString();
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private String extractCategory(JSONObject obj, String title, String description) {
        JSONArray categories = obj.optJSONArray("categories");
        if (categories != null && categories.length() > 0) {
            return categories.length() > 1
                    ? categories.optString(1, "general").toLowerCase()
                    : categories.optString(0, "general").toLowerCase();
        }
        return classifyCategory(title, description);
    }

    private String classifyCategory(String title, String description) {
        String content = (title + " " + description).toLowerCase();

        Map<String, List<String>> categoryKeywords = Map.of(
                "business", List.of("market", "finance", "stock", "business", "economy", "trade"),
                "sports", List.of("football", "cricket", "tennis", "goal", "score", "tournament", "player"),
                "entertainment", List.of("movie", "music", "celebrity", "film", "show", "series"),
                "technology", List.of("tech", "gadget", "ai", "robot", "software", "app", "machine learning", "AI"),
                "health", List.of("covid", "health", "doctor", "hospital", "medicine", "vaccine", "virus"),
                "science", List.of("nasa", "space", "science", "research", "discovery"),
                "crime", List.of("murder", "murdered", "kill", "suicide")
        );

        for (Map.Entry<String, List<String>> entry : categoryKeywords.entrySet()) {
            for (String keyword : entry.getValue()) {
                if (content.contains(keyword)) {
                    return entry.getKey();
                }
            }
        }

        return "general";
    }
}
