package com.newsaggregator.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class NewsConfig {

    private static final Properties properties = new Properties();

    static {
        try (InputStream input = NewsConfig.class.getClassLoader().getResourceAsStream("news.properties")) {
            if (input != null) {
                properties.load(input);
            } else {
                throw new RuntimeException("news.configurations file not found in classpath");
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load news.configurations", e);
        }
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }
}
