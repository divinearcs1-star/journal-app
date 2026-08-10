package com.firstproject.journalApp.controller;

import lombok.extern.slf4j.Slf4j;
import com.firstproject.journalApp.entity.User;
import com.firstproject.journalApp.repository.UserRepository;
import com.firstproject.journalApp.service.UserDetailsServiceImpl;
import com.firstproject.journalApp.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

//@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/auth/google")
@Slf4j
public class GoogleAuthController {

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @Value("${redirect.url}")
    private String redirect_url;

    @Value("${frontend.url}")
    private String frontend_url;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/callback")
    public ResponseEntity<?> handleGoogleCallback(@RequestParam String code) {

        String tokenEndpoint = "https://oauth2.googleapis.com/token";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
//            params.add("redirect_uri", "http://localhost:8081/journalapp/auth/google/callback");
        params.add("redirect_uri", redirect_url + "/auth/google/callback");

        params.add("grant_type", "authorization_code");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(tokenEndpoint, request, Map.class);

        String idToken = (String) tokenResponse.getBody().get("id_token");

        String userInfoUrl = "https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken;

        ResponseEntity<Map> userInfoResponse = restTemplate.getForEntity(userInfoUrl, Map.class);

        if (userInfoResponse.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> userInfo = userInfoResponse.getBody();
            String email = (String) userInfo.get("email");
            UserDetails userDetails = null;
            try {
                userDetails = userDetailsService.loadUserByUsername(email);
            } catch (Exception e) {
                User user = new User();
                user.setEmail(email);
                user.setUserName(email);
                user.setSentimentAnalysis(true);
                user.setPassWord(passwordEncoder.encode(UUID.randomUUID().toString()));
                user.setRoles(Arrays.asList("USER"));
                userRepository.save(user);
            }
            String jwtToken = jwtUtil.generateToken(email);
//                return ResponseEntity.ok(Collections.singletonMap("token", jwtToken));
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header(
                            HttpHeaders.LOCATION,
//                                "http://localhost:4200/auth/callback?token="
                            frontend_url + "/auth/callback?token="
                                    + URLEncoder.encode(jwtToken, StandardCharsets.UTF_8)
                    )
                    .build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/login")
    public void login(HttpServletResponse response) throws IOException {
        String url =
                "https://accounts.google.com/o/oauth2/v2/auth"
                        + "?client_id=" + clientId
                        + "&redirect_uri="
                        + URLEncoder.encode(
                        redirect_url + "/auth/google/callback",
                        StandardCharsets.UTF_8)
                        + "&response_type=code"
                        + "&scope=openid email profile"
                        + "&access_type=offline"
                        + "&prompt=consent";
        response.sendRedirect(url);
    }
}