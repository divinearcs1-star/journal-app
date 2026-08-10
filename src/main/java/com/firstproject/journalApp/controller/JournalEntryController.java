package com.firstproject.journalApp.controller;

import com.firstproject.journalApp.dto.ApiResponse;
import com.firstproject.journalApp.dto.JournalEntryDTO;
import com.firstproject.journalApp.entity.JournalEntry;
import com.firstproject.journalApp.entity.User;
import com.firstproject.journalApp.service.JournalEntryService;
import com.firstproject.journalApp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

//@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
@RestController
@RequestMapping("/journal")
@Tag(name = "Journal APIs")
public class JournalEntryController {

    @Autowired
    private JournalEntryService journalEntryService;

    @Autowired
    private UserService userService;

    @GetMapping
    @Operation(summary = "Get all Journal entries of a User")
    public ResponseEntity<List<JournalEntry>> getAllEntriesOfUser() {
        String userName = getLoggedInUserName();
        User user = userService.findbyUserName(userName);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        List<JournalEntry> journals = user.getJournalentries();
        if (journals == null || journals.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(journals);
    }

    @PostMapping
    @Operation(summary = "Create new Journal entry of a User")
    public ResponseEntity<?> createEntry(@Valid @RequestBody JournalEntryDTO myentry) {
        JournalEntry journalEntry = new JournalEntry();
        journalEntry.setTitle(myentry.getTitle().trim());
        journalEntry.setContent(myentry.getContent().trim());
        journalEntry.setSentiment(myentry.getSentiment());
        String userName = getLoggedInUserName();
        journalEntryService.saveEntry(journalEntry, userName);
        log.info("Journal created by {}", userName);
        return ResponseEntity.status(HttpStatus.CREATED).body(journalEntry);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Journal entry of User by ID")
    public ResponseEntity<?> getJournalEntryByID(@PathVariable String id) {
        ObjectId objectId = new ObjectId(id);
        String userName = getLoggedInUserName();
        User user = userService.findbyUserName(userName);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        Optional<JournalEntry> journalEntry = journalEntryService.findbyID(objectId);
        if (journalEntry.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        boolean userEntries = user.getJournalentries().stream().anyMatch(entry -> entry.getId().equals(objectId));
        if (!userEntries) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse(false, "Access Denied"));
        }
        return ResponseEntity.ok(journalEntry.get());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Journal entry of User by ID")
    public ResponseEntity<ApiResponse> DeleteJournalByID(@PathVariable String id) {
        ObjectId objectId = new ObjectId(id);
        String userName = getLoggedInUserName();
        boolean removed = journalEntryService.deletebyId(objectId, userName);
        if (!removed) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, "Journal not found"));
        }
        log.info("Journal deleted by {}", userName);
        return ResponseEntity.ok(
                new ApiResponse(true, "Journal deleted successfully")
        );
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Journal entry of User by ID")
    public ResponseEntity<?> updateJournalByID(@PathVariable String id, @Valid @RequestBody JournalEntryDTO myentry) {

        ObjectId objectId = new ObjectId(id);
        String userName = getLoggedInUserName();
        User user = userService.findbyUserName(userName);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        Optional<JournalEntry> journalEntry = journalEntryService.findbyID(objectId);
        if (journalEntry.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        boolean userentries = user.getJournalentries().stream().anyMatch(entry -> entry.getId().equals(objectId));
        if (!userentries) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse(false, "Access Denied"));
        }
        JournalEntry oldentry = journalEntry.get();
        if (myentry.getTitle() != null && !myentry.getTitle().trim().isEmpty()) {
            oldentry.setTitle(myentry.getTitle().trim());
        }
        if (myentry.getContent() != null && !myentry.getContent().trim().isEmpty()) {
            oldentry.setContent(myentry.getContent().trim());
        }
        if (myentry.getSentiment() != null) {
            oldentry.setSentiment(myentry.getSentiment());
        }
        journalEntryService.saveUpdateEntry(oldentry);
        log.info("Journal updated by {}", userName);
        return ResponseEntity.ok(oldentry);
    }

    private String getLoggedInUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
