package com.newsaggregator.newsfetcher;

import com.newsaggregator.model.NewsArticle;
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
import java.util.ArrayList;
import java.util.List;

public class NewsApiOrgFetcher implements INewsFetcher {
    private static final String API_KEY = "1ca596677f1c45babd9403548a302261";
    private static final String ENDPOINT = "https://newsapi.org/v2/top-headlines?country=us&category=general&apiKey=" + API_KEY;

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
            JSONArray articlesJson = json.getJSONArray("articles");
            for (int i = 0; i < articlesJson.length(); i++) {
                JSONObject obj = articlesJson.getJSONObject(i);
                NewsArticle a = new NewsArticle();
                a.setTitle(obj.getString("title"));
                a.setContent(obj.optString("description"));
                a.setSource(obj.getJSONObject("source").getString("name"));
                a.setUrl(obj.getString("url"));
                String isoDate = obj.getString("publishedAt");
                try {
                    ZonedDateTime zonedDateTime = ZonedDateTime.parse(isoDate);
                    LocalDateTime localDateTime = zonedDateTime.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
                    a.setPublishedAt(localDateTime.toString());
                } catch (DateTimeParseException e) {
                    a.setPublishedAt(null);
                }

                a.setCategory("general");
                articles.add(a);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return articles;
    }
}
