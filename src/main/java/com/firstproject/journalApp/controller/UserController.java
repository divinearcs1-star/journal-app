package com.firstproject.journalApp.controller;

import com.firstproject.journalApp.api.response.WeatherResponse;
import com.firstproject.journalApp.dto.UserDTO;
import com.firstproject.journalApp.entity.User;
import com.firstproject.journalApp.repository.UserRepository;
import com.firstproject.journalApp.service.UserService;
import com.firstproject.journalApp.service.WeatherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/user")
@Tag(name = "User APIs")    // , description = "Read Update and Delete a User"
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WeatherService weatherService;

    @PutMapping
    @Operation(summary = "Update user details")
    public ResponseEntity<?> updateUser(@RequestBody UserDTO user) {
        User newUser = new User();
        newUser.setUserName(user.getUserName());
        newUser.setPassWord(user.getPassWord());
        System.out.println("update user");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User userInDb = userService.findbyUserName(userName);
        userInDb.setUserName(newUser.getUserName());
        userInDb.setPassWord(newUser.getPassWord());
        boolean update = false;
        update = userService.saveNewUser(userInDb);
        if (update) {
            return new ResponseEntity<>("User Details Updated", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("User Not Found", HttpStatus.NO_CONTENT);
        }
    }

    @DeleteMapping
    @Operation(summary = "Delete a User")
    public ResponseEntity<?> deleteUserById() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            userRepository.deleteByUserName(authentication.getName());
            return new ResponseEntity<>("User Deleted Successfully", HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception while deleting user ", e);
            return new ResponseEntity<>("Error while deleting User", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("city/{city}")
    @Operation(summary = "Get Weather for city")
    public ResponseEntity<?> greeting(@PathVariable String city) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        WeatherResponse weatherResponse = weatherService.getWeather(city);
        String greeting = "";
        if (weatherResponse != null) {
            greeting = " , weather feels like " + weatherResponse.getCurrent().getFeelslike();
        }
        return new ResponseEntity<>("Hi " + authentication.getName() + greeting + " in " + city, HttpStatus.OK);
    }
}
