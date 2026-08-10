package com.firstproject.journalApp.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ChangePasswordDTO {

    @NotBlank(message = "Password is required")
    private String passWord;
}


