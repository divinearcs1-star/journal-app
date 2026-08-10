package com.firstproject.journalApp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.firstproject.journalApp.api.response.WeatherResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RedisService {

    @Autowired
    public RedisTemplate<String, String> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public <T> T get(String key, Class<T> entityclass) {
        try {
            String value = redisTemplate.opsForValue().get(key);
            if (value == null) {
                log.info("Cache miss for key: {}", key);
                return null;
            }
            log.info("Cache hit for key : {}", key);
            return objectMapper.readValue(value, entityclass);
        } catch (Exception e) {
            log.error("Redis Exception while getting key", e);
            return null;
        }
    }

    public void set(String key, Object object, Long ttl) {
        try {
            String Jasonvalue = objectMapper.writeValueAsString(object);
            redisTemplate.opsForValue().set(key, Jasonvalue, ttl, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Redis Exception while writing key", e);
        }
    }
}
