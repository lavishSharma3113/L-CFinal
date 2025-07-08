package com.newsaggregator.model;

public class ArticleReport {
    private int id;
    private int articleId;
    private int userId;
    private String reportedAt;

    public int getId() {return id;}
    public void setId(int id) {this.id = id;}

    public int getArticleId() {return articleId;}
    public void setArticleId(int articleId) {this.articleId = articleId;}

    public int getUserId() {return userId;}
    public void setUserId(int userId) {this.userId = userId;}


}

