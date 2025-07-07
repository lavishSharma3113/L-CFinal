package com.newsaggregator.email.emailservice;

import com.newsaggregator.email.MailSender;
import com.newsaggregator.model.NewsArticle;
import com.newsaggregator.model.User;
import com.newsaggregator.service.INewsArticleService;
import com.newsaggregator.service.INotificationService;
import com.newsaggregator.service.IUserService;
import com.newsaggregator.service.impl.NewsArticleServiceImpl;
import com.newsaggregator.service.impl.NotificationServiceImpl;
import com.newsaggregator.service.impl.UserServiceImpl;
import com.newsaggregator.utils.PropertyUtil;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class EmailNewsNotifierService {

    private static final String EMAIL_USERNAME = PropertyUtil.get("email.username");
    private static final String EMAIL_PASSWORD = PropertyUtil.get("email.password");
    private static final String EMAIL_SUBJECT_TEMPLATE = PropertyUtil.get("email.subject");
    private static final int EMAIL_SEND_DELAY_MS = Integer.parseInt(PropertyUtil.get("email.send.delay.ms", "1000"));

    public static void sendTodaysNewsToAllUsers() {
        IUserService userService = new UserServiceImpl();
        INotificationService notificationService = new NotificationServiceImpl();
        MailSender mailSender = new MailSender(EMAIL_USERNAME, EMAIL_PASSWORD);

        List<User> users = userService.getSubscribedUsers();
        System.out.println("Found " + users.size() + " users to notify");

        int successCount = 0;
        int failureCount = 0;

        for (User user : users) {
            try {
                List<NewsArticle> newsToSend = notificationService.getUserNotifications(user.getId());

                if (!newsToSend.isEmpty()) {
                    String emailBody = buildEmailContent(user, newsToSend);
                    String subject = String.format(EMAIL_SUBJECT_TEMPLATE, LocalDate.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));

                    mailSender.sendEmail(user.getEmail(), subject, emailBody);
                    notificationService.updateLastNotificationSeen(user.getId());

                    successCount++;
                    System.out.println("Email sent to: " + user.getEmail());
                } else {
                    System.out.println("No relevant news for user: " + user.getEmail());
                }

                Thread.sleep(EMAIL_SEND_DELAY_MS);

            } catch (Exception e) {
                failureCount++;
                System.err.println("Failed to send email to " + user.getEmail() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }

        System.out.printf("""
                Email Notification Summary:
                - Success: %d
                - Failed: %d
                - Total: %d
                """, successCount, failureCount, users.size());
    }

    private static String buildEmailContent(User user, List<NewsArticle> articles) {
        StringBuilder sb = new StringBuilder();

        sb.append("Hello ").append(user.getUsername()).append(",\n\n")
                .append("Here's your personalized news digest for today:\n\n");

        int count = 1;
        for (NewsArticle article : articles) {
            sb.append(" Article ").append(count++).append(":\n")
                    .append("Title: ").append(article.getTitle()).append("\n");

            if (isNotEmpty(article.getCategory())) sb.append("Category: ").append(article.getCategory()).append("\n");
            if (isNotEmpty(article.getSource())) sb.append("Source: ").append(article.getSource()).append("\n");
            if (isNotEmpty(article.getPublishedAt())) sb.append("Published: ").append(article.getPublishedAt()).append("\n");
            if (isNotEmpty(article.getContent())) {
                String content = article.getContent().length() > 200
                        ? article.getContent().substring(0, 200) + "..."
                        : article.getContent();
                sb.append("Summary: ").append(content).append("\n");
            }
            if (isNotEmpty(article.getUrl())) sb.append("Read more: ").append(article.getUrl()).append("\n");

            sb.append("\n").append("=".repeat(50)).append("\n\n");
        }

        sb.append("Stay informed with our daily news digest!\n\n")
                .append("Best regards,\nNews Aggregator Team\n\n")
                .append("---\nTo unsubscribe or manage your preferences, please log in to your account.\n");

        return sb.toString();
    }

    private static boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }
}
