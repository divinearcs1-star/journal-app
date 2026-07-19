package com.firstproject.journalApp.service;

import com.firstproject.journalApp.model.SentimentData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class SentimentConsumerService {

    @Autowired
    private EmailService emailService;

    @KafkaListener(topics = "weekly-sentiments", groupId = "weekly-sentiment-group")
    public void consume (SentimentData sentimentData){
        sendMail(sentimentData);
    }

    private void sendMail(SentimentData sentimentData){
        emailService.sendmail(sentimentData.getEmail(), "Sentiment for previous week", sentimentData.getSentiment());
    }


}
