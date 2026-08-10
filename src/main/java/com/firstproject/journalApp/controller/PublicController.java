package com.firstproject.journalApp.controller;

import com.firstproject.journalApp.cache.AppCache;
import com.firstproject.journalApp.dto.*;
import com.firstproject.journalApp.entity.User;
import com.firstproject.journalApp.service.UserDetailsServiceImpl;
import com.firstproject.journalApp.service.UserService;
import com.firstproject.journalApp.utils.JwtUtil;
import com.firstproject.journalApp.utils.RefreshTokenUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

//@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
@RestController
@RequestMapping("/public")
@Tag(name = "Public APIs")
public class PublicController {

    @Autowired
    private UserService userService;

    @Autowired
    private AppCache appCache;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RefreshTokenUtil refreshTokenUtil;

    @GetMapping("/health-check")
    public String healthcheck() {
        log.info("Health is OK");
        return "health ok";
    }

    @PostMapping("/signup")
    @Operation(summary = "User signup")
    public ResponseEntity<?> signup(@Valid @RequestBody UserDTO userDTO) {

        User user = new User();
        user.setUserName(userDTO.getUserName().trim());
        user.setEmail(userDTO.getEmail().trim());
        user.setPassWord(userDTO.getPassWord());
        user.setSentimentAnalysis(userDTO.isSentimentAnalysis());

        boolean created = userService.saveNewUser(user);
        if (created) {
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new ApiResponse(true, "User registered successfully."));
        }
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(false, "User already exists"));
    }

    @GetMapping("/clear-app-cache")
    @Operation(summary = "Clear App Cache")
    public void clearcache() {
        appCache.init();
    }

    @PostMapping("/login")
    @Operation(summary = "User login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDTO.getUserName(), loginRequestDTO.getPassWord()));
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequestDTO.getUserName());
        String username = userDetails.getUsername();

        String accessToken = jwtUtil.generateToken(username);
        String refreshToken = jwtUtil.generateRefreshToken(username);
        User dbUser = userService.findbyUserName(username);
        if (dbUser == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, "User not found"));
        }
        String refreshTokenHash = refreshTokenUtil.hashToken(refreshToken);
        dbUser.setRefreshToken(refreshTokenHash);
        userService.saveNewToken(dbUser);
        AuthResponseDTO response =
                new AuthResponseDTO(accessToken, refreshToken);
        log.info("Health is OK", response.getAccessToken());
        log.info("Health is OK", response.getRefreshToken());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Generate new access token using refresh token")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequestDTO request) {

        String refreshToken = request.getRefreshToken();
        String username = jwtUtil.extractUsername(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (!jwtUtil.validateToken(refreshToken, userDetails)) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Refresh token is expired or invalid"));
        }
        User user = userService.findbyUserName(username);
        if (user == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "User not found"));
        }
        String receivedTokenHash = refreshTokenUtil.hashToken(refreshToken);
        if (user.getRefreshToken() == null ||
                !user.getRefreshToken().equals(receivedTokenHash)) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Invalid refresh token"));
        }
        String newAccessToken = jwtUtil.generateToken(username);
        String newRefreshToken = jwtUtil.generateRefreshToken(username);
        String newRefreshTokenHash = refreshTokenUtil.hashToken(newRefreshToken);
        user.setRefreshToken(newRefreshTokenHash);
        userService.saveNewToken(user);
        log.info("Access and refresh tokens rotated for user: {}", username);
        AuthResponseDTO response = new AuthResponseDTO(newAccessToken, newRefreshToken);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout user and revoke refresh token")
    public ResponseEntity<ApiResponse> logout(@RequestBody RefreshTokenRequestDTO request) {

        String refreshToken = request.getRefreshToken();
        if (refreshToken == null || refreshToken.trim().isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(new ApiResponse(false, "Refresh token is required"));
        }
        String username;
        try {
            username = jwtUtil.extractUsername(refreshToken);
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse(true, "Logged out successfully"));
        }
        User user = userService.findbyUserName(username);
        if (user != null) {
            String refreshTokenHash = refreshTokenUtil.hashToken(refreshToken);
            if (refreshTokenHash.equals(user.getRefreshToken())) {
                user.setRefreshToken(null);
                userService.saveNewToken(user);
            }
        }
        return ResponseEntity.ok(
                new ApiResponse(true, "Logged out successfully"));
    }
}
