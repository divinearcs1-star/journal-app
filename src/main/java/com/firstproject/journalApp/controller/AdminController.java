package com.firstproject.journalApp.controller;

import com.firstproject.journalApp.dto.UserDTO;
import com.firstproject.journalApp.entity.User;
import com.firstproject.journalApp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin")
@Tag(name = "Admin APIs")
public class AdminController {

    @Autowired
    private UserService userService;

    @GetMapping("/all-users")
    @Operation(summary = "Get All User's List")
    public ResponseEntity<?> getAllUsers() {
        List<User> all = userService.getAll();
        if (all != null && !all.isEmpty()) {
            return new ResponseEntity<>(all, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/create-admin-user")
    @Operation(summary = "Create a new User")
    public ResponseEntity<String> createUser(@RequestBody UserDTO user) {
        try {
            User newUser = new User();
            newUser.setEmail(user.getEmail());
            newUser.setUserName(user.getUserName());
            newUser.setPassWord(user.getPassWord());
            newUser.setSentimentAnalysis(user.isSentimentAnalysis());
            userService.saveAdmin(newUser);
            return new ResponseEntity<>("Admin User created Successfully", HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception while creating admin user", e);
            return new ResponseEntity<>("Error while creating User", HttpStatus.BAD_REQUEST);
        }
    }
}
