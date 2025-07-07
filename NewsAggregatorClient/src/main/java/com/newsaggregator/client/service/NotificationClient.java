package com.newsaggregator.client.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NotificationClient {
    private static final String BASE_URL = "http://localhost:8080/notifications";

    public void getNotifications(int userId) {
        String url = BASE_URL + "?userId=" + userId;
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            System.out.println("---- Notifications ----");
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
            System.out.println("------------------------");
        } catch (Exception e) {
            System.err.println("Failed to fetch notifications: " + e.getMessage());
        }
    }
}

