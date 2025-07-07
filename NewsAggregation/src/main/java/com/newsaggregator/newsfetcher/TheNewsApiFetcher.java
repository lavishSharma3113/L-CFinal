package com.newsaggregator.newsfetcher;

import com.newsaggregator.model.NewsArticle;
import com.newsaggregator.newsfetcher.INewsFetcher;
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
    private static final String API_KEY = "i3N4cKdEkJSHqhW9CET8Mz1OSDjiQJB3X0619NKj";
    private static final String ENDPOINT = "https://api.thenewsapi.com/v1/news/top?api_token=" + API_KEY + "&locale=us&limit=3";

    @Override
    public List<NewsArticle> fetchNews() {
        List<NewsArticle> articles = new ArrayList<>();
        try {
            URL url = new URL(ENDPOINT);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder content = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            JSONObject json = new JSONObject(content.toString());
            JSONArray dataArray = json.getJSONArray("data");

            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject obj = dataArray.getJSONObject(i);
                NewsArticle article = new NewsArticle();

                article.setTitle(obj.optString("title"));
                article.setContent(obj.optString("description"));
                article.setSource(obj.optString("source", "Unknown"));
                article.setUrl(obj.optString("url"));


                String isoDate = obj.optString("published_at");
                try {
                    ZonedDateTime zonedDateTime = ZonedDateTime.parse(isoDate);
                    LocalDateTime localDateTime = zonedDateTime.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
                    article.setPublishedAt(localDateTime.toString());
                } catch (DateTimeParseException e) {
                    article.setPublishedAt(null);
                }


                JSONArray categories = obj.optJSONArray("categories");
                if (categories != null && categories.length() > 0) {
                    if(categories.length() > 1){
                        article.setCategory(categories.getString(1).toLowerCase());
                    }else {
                        article.setCategory(categories.getString(0).toLowerCase());
                    }
                } else {
                    article.setCategory(classifyCategory(article.getTitle(), article.getContent()));
                }

                articles.add(article);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return articles;
    }

    private String classifyCategory(String title, String description) {
        String content = (title + " " + description).toLowerCase();

        Map<String, List<String>> categoryKeywords = Map.of(
                "business", List.of("market", "finance", "stock", "business", "economy", "trade"),
                "sports", List.of("football", "cricket", "tennis", "goal", "score", "tournament", "player"),
                "entertainment", List.of("movie", "music", "celebrity", "film", "show", "series"),
                "technology", List.of("tech", "gadget", "ai", "robot", "software", "app", "machine learning","AI"),
                "health", List.of("covid", "health", "doctor", "hospital", "medicine", "vaccine", "virus"),
                "science", List.of("nasa", "space", "science", "research", "discovery"),
                "crime" , List.of("murder","murdered","kill", "suicide")

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
