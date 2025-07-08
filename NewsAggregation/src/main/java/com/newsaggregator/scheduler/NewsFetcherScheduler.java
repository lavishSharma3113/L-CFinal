package com.newsaggregator.scheduler;

import com.newsaggregator.dao.ExternalServerDAO;
import com.newsaggregator.dao.NewsArticleDAO;
import com.newsaggregator.model.ExternalServer;
import com.newsaggregator.model.NewsArticle;
import com.newsaggregator.newsfetcher.INewsFetcher;
import com.newsaggregator.newsfetcher.TheNewsApiFetcher;
import com.newsaggregator.service.INewsCategoryService;
import com.newsaggregator.service.impl.NewsArticleCategoryImpl;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

public class NewsFetcherScheduler {

    private final List<INewsFetcher> fetchers = List.of(new TheNewsApiFetcher());
    private final NewsArticleDAO articleDAO = new NewsArticleDAO();
    private final ExternalServerDAO serverDAO = new ExternalServerDAO();
    private final INewsCategoryService categoryService = new NewsArticleCategoryImpl();
    private Timer timer;

    public void start() {
        long interval = getFetchInterval();

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                for (INewsFetcher fetcher : fetchers) {
                    String serverName = fetcher.getClass().getSimpleName();
                    ExternalServer server = new ExternalServer();
                    server.setName(serverName);
                    server.setLastAccessed(String.valueOf(LocalDateTime.now()));

                    boolean isActive = false;
                    int savedCount = 0;

                    try {
                        List<NewsArticle> articles = fetcher.fetchNews();

                        if (articles != null && !articles.isEmpty()) {
                            for (NewsArticle article : articles) {
                                articleDAO.save(article);
                                savedCount++;
                            }

                            mapCategoryToCategoryId();
                            isActive = true;
                        }

                    } catch (Exception e) {
                        System.err.println("[Scheduler] Failed to fetch news from: " + serverName);
                        e.printStackTrace();
                    }

                    server.setActive(isActive);
                    serverDAO.save(server);
                }
            }
        }, 0, interval);
    }

    private void mapCategoryToCategoryId() {
        if(categoryService.insertCategoriesInCategoryTable()) {
            System.out.println("category id added");
        }
        if(categoryService.insertNewsCategories()) {
            System.out.println("category name added");
        }
    }

    private long getFetchInterval() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("news.configurations")) {
            Properties props = new Properties();
            if (input != null) {
                props.load(input);
                return Long.parseLong(props.getProperty("news.fetch.interval.ms"));
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
        return 3 * 60 * 60 * 1000;
    }

    public void stop() {
        if (timer != null) {
            timer.cancel();
            System.out.println("[Scheduler] NewsFetcherScheduler stopped.");
        }
    }
}
