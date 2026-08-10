package com.firstproject.journalApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse {

    private boolean success;
    private String message;
    private Map<String, String> errors;

    public ApiResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}


