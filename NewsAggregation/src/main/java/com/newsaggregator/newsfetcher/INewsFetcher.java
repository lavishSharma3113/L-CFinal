package com.newsaggregator.newsfetcher;

import com.newsaggregator.model.NewsArticle;
import java.util.List;

public interface INewsFetcher {
    List<NewsArticle> fetchNews();
}
