package com.newsaggregator.scheduler;

import com.newsaggregator.dao.ExternalServerDAO;
import com.newsaggregator.dao.NewsArticleDAO;
import com.newsaggregator.newsfetcher.INewsFetcher;
import com.newsaggregator.newsfetcher.NewsApiOrgFetcher;
import com.newsaggregator.model.NewsArticle;
import com.newsaggregator.model.ExternalServer;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class NewsFetcherScheduler {
    private final List<INewsFetcher> fetchers = List.of(new NewsApiOrgFetcher());
    private final NewsArticleDAO dao = new NewsArticleDAO();
    private final ExternalServerDAO serverDAO = new ExternalServerDAO();

    public void start() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                for (INewsFetcher fetcher : fetchers) {
                    String serverName = fetcher.getClass().getSimpleName();
                    ExternalServer server = new ExternalServer();
                    server.setName(serverName);


                    List<NewsArticle> articles = fetcher.fetchNews();
                    int savedCount = 0;
                    for (NewsArticle article : articles) {
                        dao.save(article);
                            savedCount++;

                    }

                    serverDAO.save(server);
                    System.out.println("[Scheduler] " + serverName + " fetched and saved " + savedCount + " articles at " );
                }
            }
        }, 0, 3 * 60 * 60 * 1000); // every 3 hours
    }
}