package com.firstproject.journalApp.controller;

import com.firstproject.journalApp.api.response.WeatherResponse;
import com.firstproject.journalApp.dto.ApiResponse;
import com.firstproject.journalApp.dto.ChangePasswordDTO;
import com.firstproject.journalApp.entity.User;
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

import javax.validation.Valid;

//@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
@RestController
@RequestMapping("/user")
@Tag(name = "User APIs")    // , description = "Read Update and Delete a User"
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private WeatherService weatherService;

    @PutMapping("/password")
    @Operation(summary = "Update User Password")
    public ResponseEntity<ApiResponse> updateUser(@Valid @RequestBody ChangePasswordDTO changePasswordDTO) {

        String loggedUser = getLoggedInUserName();
        User user = userService.findbyUserName(loggedUser);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, "User not found"));
        }
        userService.saveNewPassword(user, changePasswordDTO.getPassWord());
        user.setRefreshToken(null);
        userService.saveNewToken(user);
        return ResponseEntity.ok(
                new ApiResponse(true,"Password updated successfully. Please login again."));
    }

    @DeleteMapping
    @Operation(summary = "Delete a User")
    public ResponseEntity<ApiResponse> deleteUserById() {

        String loggedUser = getLoggedInUserName();
        User user = userService.findbyUserName(loggedUser);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, "User not found"));
        }
        userService.deletebyUserName(loggedUser);
        return ResponseEntity.ok(
                new ApiResponse(true, "User deleted successfully")
        );
    }

    @GetMapping("/weather/{city}")
    @Operation(summary = "Get Weather for city")
    public ResponseEntity<ApiResponse> greeting(@PathVariable String city) {

        String loggedUser = getLoggedInUserName();
        WeatherResponse weatherResponse = weatherService.getWeather(city);
        String greeting = "";
        if (weatherResponse != null && weatherResponse.getCurrent() != null) {
            greeting = "Hi " + loggedUser + ", weather feels like "
                    + weatherResponse.getCurrent().getFeelslike() + "°C in " + city;
        } else {
            greeting = "Hi " + loggedUser + ", weather information is currently unavailable for " + city;
        }
        return ResponseEntity.ok(
                new ApiResponse(true, greeting)
        );
    }

    private String getLoggedInUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
