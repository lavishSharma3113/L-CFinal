package com.newsaggregator.controller;

import com.google.gson.Gson;
import com.newsaggregator.model.NewsArticle;
import com.newsaggregator.model.NewsCategory;
import com.newsaggregator.service.IArticleReactionService;
import com.newsaggregator.service.INewsArticleService;
import com.newsaggregator.service.INewsCategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import javax.servlet.http.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NewsArticleServletTest {

    @InjectMocks
    private NewsArticleServlet servlet;

    @Mock
    private INewsArticleService articleService;
    @Mock
    private INewsCategoryService categoryService;
    @Mock
    private IArticleReactionService reactionService;
    @Spy
    private Gson gson = new Gson();

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    private StringWriter responseWriter;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Inject mocks into servlet using reflection (fields are final)
        java.lang.reflect.Field f1 = NewsArticleServlet.class.getDeclaredField("articleService");
        f1.setAccessible(true);
        f1.set(servlet, articleService);
        java.lang.reflect.Field f2 = NewsArticleServlet.class.getDeclaredField("categoryService");
        f2.setAccessible(true);
        f2.set(servlet, categoryService);
        java.lang.reflect.Field f3 = NewsArticleServlet.class.getDeclaredField("reactionService");
        f3.setAccessible(true);
        f3.set(servlet, reactionService);
        java.lang.reflect.Field f4 = NewsArticleServlet.class.getDeclaredField("gson");
        f4.setAccessible(true);
        f4.set(servlet, gson);

        responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
    }

    @Test
    void testDoGet_today() throws Exception {
        when(request.getParameter("action")).thenReturn("today");
        when(request.getParameter("userId")).thenReturn("1");
        List<NewsArticle> articles = List.of(new NewsArticle());
        when(articleService.getArticlesByToday(1)).thenReturn(articles);

        servlet.doGet(request, response);

        verify(articleService).getArticlesByToday(1);
        assertTrue(responseWriter.toString().contains("["));
    }

    @Test
    void testDoGet_keyword() throws Exception {
        when(request.getParameter("action")).thenReturn("keyword");
        when(request.getParameter("keyword")).thenReturn("test");
        List<NewsArticle> articles = List.of(new NewsArticle());
        when(articleService.getArticlesByKeyword("test")).thenReturn(articles);

        servlet.doGet(request, response);

        verify(articleService).getArticlesByKeyword("test");
        assertTrue(responseWriter.toString().contains("["));
    }

    @Test
    void testDoGet_range() throws Exception {
        when(request.getParameter("action")).thenReturn("range");
        when(request.getParameter("start")).thenReturn("2024-01-01");
        when(request.getParameter("end")).thenReturn("2024-01-31");
        when(request.getParameter("category")).thenReturn("sports");
        List<NewsArticle> articles = List.of(new NewsArticle());
        when(articleService.getArticlesByDateRange("2024-01-01", "2024-01-31", "sports")).thenReturn(articles);

        servlet.doGet(request, response);

        verify(articleService).getArticlesByDateRange("2024-01-01", "2024-01-31", "sports");
        assertTrue(responseWriter.toString().contains("["));
    }

    @Test
    void testDoGet_categories() throws Exception {
        when(request.getParameter("action")).thenReturn("categories");
        List<NewsCategory> categories = List.of(new NewsCategory());
        when(categoryService.getAllNewsCategories()).thenReturn(categories);

        servlet.doGet(request, response);

        verify(categoryService).getAllNewsCategories();
        assertTrue(responseWriter.toString().contains("["));
    }

    @Test
    void testDoGet_mostLiked() throws Exception {
        when(request.getParameter("action")).thenReturn("mostLiked");
        List<NewsArticle> articles = List.of(new NewsArticle());
        when(articleService.findMostLiked()).thenReturn(articles);

        servlet.doGet(request, response);

        verify(articleService).findMostLiked();
        assertTrue(responseWriter.toString().contains("["));
    }

    @Test
    void testDoGet_all() throws Exception {
        when(request.getParameter("action")).thenReturn("all");
        when(request.getParameter("start")).thenReturn("2024-01-01");
        when(request.getParameter("end")).thenReturn("2024-01-31");
        when(request.getParameter("userId")).thenReturn("2");
        List<NewsArticle> articles = List.of(new NewsArticle());
        when(articleService.getAllArticles("2024-01-01", "2024-01-31", 2)).thenReturn(articles);

        servlet.doGet(request, response);

        verify(articleService).getAllArticles("2024-01-01", "2024-01-31", 2);
        assertTrue(responseWriter.toString().contains("["));
    }

    @Test
    void testDoGet_invalidAction() throws Exception {
        when(request.getParameter("action")).thenReturn("invalid");

        servlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertTrue(responseWriter.toString().contains("Invalid action"));
    }

    @Test
    void testDoPost_like_success() throws Exception {
        when(request.getParameter("action")).thenReturn("like");
        when(request.getParameter("userId")).thenReturn("1");
        when(request.getParameter("articleId")).thenReturn("10");
        when(reactionService.saveReaction(1, 10, "like")).thenReturn(true);

        servlet.doPost(request, response);

        verify(reactionService).saveReaction(1, 10, "like");
        assertTrue(responseWriter.toString().contains("Success"));
    }

    @Test
    void testDoPost_dislike_failed() throws Exception {
        when(request.getParameter("action")).thenReturn("dislike");
        when(request.getParameter("userId")).thenReturn("1");
        when(request.getParameter("articleId")).thenReturn("10");
        when(reactionService.saveReaction(1, 10, "dislike")).thenReturn(false);

        servlet.doPost(request, response);

        verify(reactionService).saveReaction(1, 10, "dislike");
        assertTrue(responseWriter.toString().contains("Failed"));
    }

    @Test
    void testDoPost_invalidAction() throws Exception {
        when(request.getParameter("action")).thenReturn("foo");
        when(request.getParameter("userId")).thenReturn("1");
        when(request.getParameter("articleId")).thenReturn("10");

        servlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertTrue(responseWriter.toString().contains("Invalid action type"));
    }

    @Test
    void testDoPost_invalidUserIdOrArticleId() throws Exception {
        when(request.getParameter("action")).thenReturn("like");
        when(request.getParameter("userId")).thenReturn("abc");
        when(request.getParameter("articleId")).thenReturn("10");

        servlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertTrue(responseWriter.toString().contains("Invalid articleId or userId"));
    }
}