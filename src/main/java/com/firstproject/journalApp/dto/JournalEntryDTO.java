package com.firstproject.journalApp.dto;

import com.firstproject.journalApp.enums.Sentiment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JournalEntryDTO {

    @NotEmpty
    private String title;
    private String content;
    private Sentiment sentiment;
}




