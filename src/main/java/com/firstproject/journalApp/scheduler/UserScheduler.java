package com.firstproject.journalApp.scheduler;

import com.firstproject.journalApp.cache.AppCache;
import com.firstproject.journalApp.entity.JournalEntry;
import com.firstproject.journalApp.entity.User;
import com.firstproject.journalApp.enums.Sentiment;
import com.firstproject.journalApp.model.SentimentData;
import com.firstproject.journalApp.repository.UserRepositoryImpl;
import com.firstproject.journalApp.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class UserScheduler {

    private final EmailService emailService;
    private final UserRepositoryImpl userRepository;
    private final AppCache appCache;
    private final KafkaTemplate<String, SentimentData> kafkaTemplate;

    public UserScheduler(EmailService emailService, UserRepositoryImpl userRepository,
                         AppCache appCache, KafkaTemplate<String, SentimentData> kafkaTemplate) {
        this.emailService = emailService;
        this.userRepository = userRepository;
        this.appCache = appCache;
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Runs every Monday at 9:00 AM
     * Calculates the most frequent sentiment from the
     * previous 7 days and sends it for sentiment analysis.
     */
//    @Scheduled(cron = "0 0 9 ? * MON")
    @Scheduled(cron = "0 0/25 * ? * *")
    public void sendWeeklySentimentEmails() {

        log.info("Starting weekly sentiment analysis");
        try {
            List<User> users = userRepository.getUSerForSA();
            if (users == null || users.isEmpty()) {
                log.info("No users found for sentiment analysis");
                return;
            }
            LocalDateTime sevenDaysAgo = LocalDateTime.now().minus(7, ChronoUnit.DAYS);
            for (User user : users) {
                try {
                    processUserSentiment(user, sevenDaysAgo);
                } catch (Exception e) {
                    log.error("Error while processing sentiment for user: {}", user.getUserName(), e);
                }
            }
            log.info("Weekly sentiment analysis completed");
        } catch (Exception e) {
            log.error("Error while fetching users for sentiment analysis", e);
        }
    }

    // Processes sentiment for one user.
    private void processUserSentiment(User user, LocalDateTime sevenDaysAgo) {
        List<JournalEntry> journalEntries = user.getJournalentries();
        if (journalEntries == null || journalEntries.isEmpty()) {
            log.debug("No journal entries found for user: {}", user.getUserName()
            );
            return;
        }
        Map<Sentiment, Integer> sentimentCounts = new HashMap<>();
        for (JournalEntry journalEntry : journalEntries) {
            if (journalEntry == null) {
                continue;
            }
            if (journalEntry.getDate() == null) {
                continue;
            }
            if (journalEntry.getSentiment() == null) {
                continue;
            }
            if (journalEntry.getDate().isAfter(sevenDaysAgo)) {
                Sentiment sentiment = journalEntry.getSentiment();
                sentimentCounts.put(sentiment, sentimentCounts.getOrDefault(sentiment, 0) + 1
                );
            }
        }
        if (sentimentCounts.isEmpty()) {
            log.debug("No sentiment data available for user: {}", user.getUserName());
            return;
        }
        Sentiment mostFrequentSentiment = getMostFrequentSentiment(sentimentCounts);
        if (mostFrequentSentiment == null) {
            return;
        }
        SentimentData sentimentData =
                SentimentData.builder()
                        .email(user.getEmail())
                        .sentiment("Sentiment for last 7 days: " + mostFrequentSentiment).build();
        sendSentimentData(sentimentData);
    }

    // Finds the sentiment having the highest count.
    private Sentiment getMostFrequentSentiment(
            Map<Sentiment, Integer> sentimentCounts) {
        Sentiment mostFrequentSentiment = null;
        int maxCount = 0;
        for (Map.Entry<Sentiment, Integer> entry
                : sentimentCounts.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                mostFrequentSentiment = entry.getKey();
            }
        }
        return mostFrequentSentiment;
    }

    // Sends sentiment information to Kafka.
    // If Kafka cannot be used, sends email directly.
    private void sendSentimentData(SentimentData sentimentData) {
        try {
            kafkaTemplate
                    .send("weekly-sentiments", sentimentData.getEmail(), sentimentData)
                    .addCallback(result -> {
                                log.info("Sentiment data successfully sent to Kafka for: {}", sentimentData.getEmail());
                            },
                            exception -> {
                                log.error("Kafka failed for email: {}. Sending email directly.", sentimentData.getEmail(), exception);
                                sendEmailFallback(sentimentData);
                            }
                    );
        } catch (Exception e) {
            log.error("Exception while sending sentiment to Kafka for: {}", sentimentData.getEmail(), e);
            sendEmailFallback(sentimentData);
        }
    }

    // Sends email when Kafka is unavailable.
    private void sendEmailFallback(SentimentData sentimentData) {
        try {
            emailService.sendmail(
                    sentimentData.getEmail(), "Sentiment for previous week", sentimentData.getSentiment()
            );
            log.info("Fallback email sent to: {}", sentimentData.getEmail()
            );
        } catch (Exception e) {
            log.error("Failed to send fallback email to: {}", sentimentData.getEmail(), e);
        }
    }

    // Refresh application cache every 1 hour.
    @Scheduled(cron = "0 0 * ? * *")
    public void clearAppCache() {
        try {
            log.info("Refreshing application cache");
            appCache.init();
            log.info("Application cache refreshed successfully");
        } catch (Exception e) {
            log.error("Error while refreshing application cache", e);
        }
    }
}