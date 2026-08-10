package com.firstproject.journalApp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    private ObjectId id;
    @Indexed(unique = true)

    @NotBlank(message = "Username is required")
    private String userName;

    @JsonIgnore
    @NotBlank(message = "Password is required")
    private String passWord;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email")
    private String email;

    private boolean sentimentAnalysis;

    @DBRef
    private List<JournalEntry> journalentries = new ArrayList<>();

    private List<String> roles;

    private String refreshToken;
}
