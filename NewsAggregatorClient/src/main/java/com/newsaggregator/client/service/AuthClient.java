package com.newsaggregator.client.service;

import com.newsaggregator.client.model.User;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

public class AuthClient {

    private static final String BASE_URL = "http://localhost:8080/NewsAggregation/login";
    private final Gson gson = new Gson();

    public User login(String email, String password) throws Exception {
        String query = String.format("email=%s&password=%s",
                URLEncoder.encode(email, "UTF-8"),
                URLEncoder.encode(password, "UTF-8"));

        URL url = new URL(BASE_URL + "?" + query);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        int status = conn.getResponseCode();
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder json = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            json.append(line);
        }
        in.close();

        if (status == 200) {
            return gson.fromJson(json.toString(), User.class);
        } else {
            throw new Exception("Login failed: " + json);
        }
    }

    public boolean register(String username, String email, String password) {
        try {
            String query = String.format("username=%s&email=%s&password=%s",
                    URLEncoder.encode(username, "UTF-8"),
                    URLEncoder.encode(email, "UTF-8"),
                    URLEncoder.encode(password, "UTF-8"));

            URL url = new URL("http://localhost:8080/NewsAggregation/signup?" + query);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder json = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }

            Gson gson = new Gson();
            Map<String, String> responseMap = gson.fromJson(json.toString(), Map.class);
            return "success".equalsIgnoreCase(responseMap.get("status"));
        } catch (Exception e) {
            System.err.println("Signup failed: " + e.getMessage());
            return false;
        }
    }

}

