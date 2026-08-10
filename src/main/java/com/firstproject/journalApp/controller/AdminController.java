package com.firstproject.journalApp.controller;

import com.firstproject.journalApp.dto.ApiResponse;
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

import javax.validation.Valid;
import java.util.List;

//@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
@RestController
@RequestMapping("/admin")
@Tag(name = "Admin APIs")
public class AdminController {

    @Autowired
    private UserService userService;

    @GetMapping("/all-users")
    @Operation(summary = "Get All User's List")
    public ResponseEntity<List<User>> getAllUsers() {

        List<User> users = userService.getAll();
        if (users == null || users.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(users);
    }

    @PostMapping("/create-admin-user")
    @Operation(summary = "Create a new User")
    public ResponseEntity<ApiResponse> createUser(@Valid @RequestBody UserDTO user) {
        User newUser = new User();
        newUser.setEmail(user.getEmail().trim());
        newUser.setUserName(user.getUserName().trim());
        newUser.setPassWord(user.getPassWord());
        newUser.setSentimentAnalysis(user.isSentimentAnalysis());

        userService.saveAdmin(newUser);
        log.info("Admin user created successfully : {}", user.getUserName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(true, "Admin user created successfully"));
    }
}
