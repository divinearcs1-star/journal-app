package com.firstproject.journalApp.service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
public class RedisTests {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    void testmail(){
        redisTemplate.opsForValue().set("user", "altman");
        Object news = redisTemplate.opsForValue().get("news");
        int a=1;
    }
}
