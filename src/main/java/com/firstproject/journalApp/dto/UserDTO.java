package com.firstproject.journalApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    @NotEmpty
    private String userName;
    private String email;
    private boolean sentimentAnalysis;
    @NotEmpty
    private String passWord;

}

