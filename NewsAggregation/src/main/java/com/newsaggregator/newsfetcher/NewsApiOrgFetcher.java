package com.newsaggregator.newsfetcher;

import com.newsaggregator.model.NewsArticle;
import com.newsaggregator.utils.NewsConfig;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.*;

public class NewsApiOrgFetcher implements INewsFetcher {

    private static final String API_KEY = NewsConfig.get("news.api.key");
    private static final String COUNTRY = NewsConfig.get("news.api.country");
    private static final String CATEGORY = NewsConfig.get("news.api.category");
    private static final String BASE_ENDPOINT = NewsConfig.get("news.api.endpoint");

    private static final String FULL_ENDPOINT = BASE_ENDPOINT
            + "?country=" + COUNTRY
            + "&category=" + CATEGORY
            + "&apiKey=" + API_KEY;

    @Override
    public List<NewsArticle> fetchNews() {
        List<NewsArticle> articles = new ArrayList<>();

        try {
            String jsonResponse = fetchApiResponse();
            JSONArray articlesArray = extractArticlesArray(jsonResponse);

            for (int i = 0; i < articlesArray.length(); i++) {
                NewsArticle article = parseArticle(articlesArray.getJSONObject(i));
                articles.add(article);
            }

        } catch (Exception e) {
            System.err.println("Error fetching or parsing news: " + e.getMessage());
            e.printStackTrace();
        }

        return articles;
    }

    private String fetchApiResponse() throws Exception {
        URL url = new URL(FULL_ENDPOINT);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder responseBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                responseBuilder.append(line);
            }
            return responseBuilder.toString();
        }
    }

    private JSONArray extractArticlesArray(String jsonResponse) {
        JSONObject json = new JSONObject(jsonResponse);
        return json.getJSONArray("articles");
    }

    private NewsArticle parseArticle(JSONObject obj) {
        NewsArticle article = new NewsArticle();

        article.setTitle(obj.optString("title"));
        article.setContent(obj.optString("description"));
        article.setSource(obj.getJSONObject("source").optString("name"));
        article.setUrl(obj.optString("url"));
        article.setPublishedAt(parseDate(obj.optString("publishedAt")));
        article.setCategory(classifyCategory(article.getTitle(), article.getContent()));

        return article;
    }

    private String parseDate(String isoDate) {
        try {
            ZonedDateTime zonedDateTime = ZonedDateTime.parse(isoDate);
            LocalDateTime localDateTime = zonedDateTime
                    .withZoneSameInstant(ZoneId.systemDefault())
                    .toLocalDateTime();
            return localDateTime.toString();
        } catch (DateTimeParseException e) {
            System.err.println("Failed to parse date: " + isoDate);
            return null;
        }
    }

    private String classifyCategory(String title, String description) {
        String content = (title + " " + description).toLowerCase();

        Map<String, List<String>> categoryKeywords = getCategoryKeywords();

        for (Map.Entry<String, List<String>> entry : categoryKeywords.entrySet()) {
            for (String keyword : entry.getValue()) {
                if (content.contains(keyword)) {
                    return entry.getKey();
                }
            }
        }

        return "general";
    }

    private Map<String, List<String>> getCategoryKeywords() {
        return Map.of(
                "business", List.of("market", "finance", "stock", "business", "economy", "trade"),
                "sports", List.of("football", "cricket", "tennis", "goal", "score", "tournament", "player"),
                "entertainment", List.of("movie", "music", "celebrity", "film", "show", "series"),
                "technology", List.of("tech", "gadget", "ai", "robot", "software", "app", "machine learning"),
                "health", List.of("covid", "health", "doctor", "hospital", "medicine", "vaccine", "virus"),
                "science", List.of("nasa", "space", "science", "research", "discovery")
        );
    }
}
