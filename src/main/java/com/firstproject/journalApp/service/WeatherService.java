package com.firstproject.journalApp.service;

import com.firstproject.journalApp.api.response.WeatherResponse;
import com.firstproject.journalApp.cache.AppCache;
import com.firstproject.journalApp.constants.Placeholders;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class WeatherService {

    private static final String REDIS_KEY_PREFIX = "weather_of_";

    @Value("${weather.api.key}")
    private String apikey;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AppCache appCache;

    @Autowired
    public RedisService redisService;

    public WeatherResponse getWeather(String city) {
        try {
            String redisKey = REDIS_KEY_PREFIX + city.trim().toLowerCase();
            WeatherResponse weatherResponse = redisService.get(redisKey, WeatherResponse.class);
            if (weatherResponse != null) {
                log.info("Weather found in Redis for city : {}", city);
                return weatherResponse;
            }
            log.info("Weather not found in Redis for city : {}", city);
            String weatherApi = appCache.appCache.get(AppCache.keys.WEATHER_API.toString());
            if (weatherApi == null) {
                throw new RuntimeException("Weather API URL not found in AppCache.");
            }
            String finalApi = weatherApi.replace(Placeholders.CITY, city).replace(Placeholders.API_KEY, apikey);
            ResponseEntity<WeatherResponse> response = restTemplate.exchange(finalApi, HttpMethod.GET, null, WeatherResponse.class);
            WeatherResponse weatheresponse = response.getBody();
            if (weatheresponse != null) {
                redisService.set(redisKey, weatheresponse, 1800L);
                log.info("Weather cached in Redis for city : {}", city);
            }
            return weatheresponse;
        } catch (Exception e) {
            log.error("Error while fetching weather for city : {}", city, e);
            return null;
        }
    }
}
