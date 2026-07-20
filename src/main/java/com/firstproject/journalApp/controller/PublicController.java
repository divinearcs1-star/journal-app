package com.firstproject.journalApp.controller;

import com.firstproject.journalApp.cache.AppCache;
import com.firstproject.journalApp.dto.UserDTO;
import com.firstproject.journalApp.entity.User;
import com.firstproject.journalApp.service.UserDetailsServiceImpl;
import com.firstproject.journalApp.service.UserService;
import com.firstproject.journalApp.utils.JwtUtil;
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

@Slf4j
@RestController
@RequestMapping("/public")
@Tag( name ="Public APIs")
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

    @GetMapping("/health-check")
    public String healthcheck() {
        return "health ok";
    }

    @PostMapping("/signup")
    @Operation(summary = "User signup")
    public void signup(@RequestBody UserDTO user) {
        User newUser = new User();
        newUser.setEmail(user.getEmail());
        newUser.setUserName(user.getUserName());
        newUser.setPassWord(user.getPassWord());
        newUser.setSentimentAnalysis(user.isSentimentAnalysis());
        userService.saveNewUser(newUser);
    }

    @GetMapping("clear-app-cache")
    @Operation(summary = "Clear App Cache")
    public void clearcache(){
        appCache.init();
    }

    @PostMapping("/login")
    @Operation(summary = "User login")
    public ResponseEntity<String> login(@RequestBody UserDTO user) {
        try {
            User newUser = new User();
            newUser.setUserName(user.getUserName());
            newUser.setPassWord(user.getPassWord());
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(newUser.getUserName(),newUser.getPassWord()));
            UserDetails userDetails = userDetailsService.loadUserByUsername(newUser.getUserName());
            String jwt = jwtUtil.generateToken(userDetails.getUsername());
            return new ResponseEntity<>(jwt, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception occurred while createAuthentication token", e);
            return new ResponseEntity<>("Incorrect username or password" , HttpStatus.BAD_REQUEST);
        }
    }
}
