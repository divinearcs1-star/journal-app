package com.firstproject.journalApp.controller;

import com.firstproject.journalApp.dto.JournalEntryDTO;
import com.firstproject.journalApp.entity.JournalEntry;
import com.firstproject.journalApp.entity.User;
import com.firstproject.journalApp.service.JournalEntryService;
import com.firstproject.journalApp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public ResponseEntity<?> getAllEntriesOfUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userService.findbyUserName(userName);
        List<JournalEntry> all = user.getJournalentries();
        if (all != null && !all.isEmpty()) {
            return new ResponseEntity<>(all, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    @Operation(summary = "Create new Journal entry of a User")
    public ResponseEntity<JournalEntry> createEntry(@RequestBody JournalEntryDTO myentry) {
        JournalEntry journalEntry = new JournalEntry();
        try {
            journalEntry.setTitle(myentry.getTitle());
            journalEntry.setContent(myentry.getContent());
            journalEntry.setSentiment(myentry.getSentiment());
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();
            journalEntryService.saveEntry(journalEntry, userName);
            return new ResponseEntity<>(journalEntry, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(journalEntry, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("id/{id}")
    @Operation(summary = "Get Journal entry of User by ID")
    public ResponseEntity<JournalEntry> getJournalEntryByID(@PathVariable String id) {
        ObjectId objectId = new ObjectId(id);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userService.findbyUserName(userName);
        List<JournalEntry> collect = user.getJournalentries().stream().filter(x -> x.getId().equals(objectId)).collect(Collectors.toList());
        if (!collect.isEmpty()) {
            Optional<JournalEntry> journalEntry = journalEntryService.findbyID(objectId);
            if (journalEntry.isPresent()) {
                return new ResponseEntity<>(journalEntry.get(), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("id/{id}")
    @Operation(summary = "Delete Journal entry of User by ID")
    public ResponseEntity<?> DeleteJournalByID(@PathVariable String id) {
        ObjectId objectId = new ObjectId(id);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        boolean removed = journalEntryService.deletebyId(objectId, userName);
        if (removed) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("id/{id}")
    @Operation(summary = "Update Journal entry of User by ID")
    public ResponseEntity<?> updateJournalByID(@PathVariable String id, @RequestBody JournalEntryDTO myentry) {
        JournalEntry journalEntrys = new JournalEntry();
        journalEntrys.setTitle(myentry.getTitle());
        journalEntrys.setContent(myentry.getContent());
        journalEntrys.setSentiment(myentry.getSentiment());
        ObjectId objectId = new ObjectId(id);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userService.findbyUserName(userName);
        List<JournalEntry> collect = user.getJournalentries().stream().filter(x -> x.getId().equals(objectId)).collect(Collectors.toList());
        if (!collect.isEmpty()) {
            Optional<JournalEntry> journalEntry = journalEntryService.findbyID(objectId);
            if (journalEntry.isPresent()) {
                JournalEntry old = journalEntry.get();
                old.setTitle(journalEntrys.getTitle() != null && !journalEntrys.getTitle().equals("") ? journalEntrys.getTitle() : old.getTitle());
                old.setContent(journalEntrys.getContent() != null && !journalEntrys.getContent().equals("") ? journalEntrys.getContent() : old.getContent());
                journalEntryService.saveUpdateEntry(old);
                return new ResponseEntity<>(old, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
