package com.newsaggregator.model;

public class NewsCategory {
    private int id;
    private String name;
    private boolean isHidden;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public boolean getIsHidden(){return isHidden;}
    public void setIsHidden(boolean isHide){ this.isHidden = isHide;}
}

