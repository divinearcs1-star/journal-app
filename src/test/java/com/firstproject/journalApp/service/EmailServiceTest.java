package com.firstproject.journalApp.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EmailServiceTest {

    @Autowired
    private EmailService emailService;

    @Test
    public void sendMail(){
        emailService.sendmail ("divinearcs1@gmail.com","Testing java mail sender", "AAP Kaise he !!");
    }
}
