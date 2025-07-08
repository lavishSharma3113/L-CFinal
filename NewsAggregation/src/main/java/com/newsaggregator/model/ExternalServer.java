package com.newsaggregator.model;

public class ExternalServer {
    private int id;
    private String name;
    private String apiKey;
    private String baseUrl;
    private boolean active;
    private String lastAccessed;


    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }


    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }

    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public String getLastAccessed() { return lastAccessed; }
    public void setLastAccessed(String lastAccessed) { this.lastAccessed = lastAccessed; }
}

