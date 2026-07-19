package com.firstproject.journalApp.service;

import com.firstproject.journalApp.entity.JournalEntry;
import com.firstproject.journalApp.entity.User;
import com.firstproject.journalApp.repository.JournalEntryRepository;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class JournalEntryService {

    @Autowired
    private JournalEntryRepository journalEntryRepository;

    @Autowired
    private UserService userService;

    @Transactional
    public void saveEntry(JournalEntry journalEntry, String userName) {
        try {
            User user = userService.findbyUserName(userName);
            journalEntry.setDate(LocalDateTime.now());
            JournalEntry saved = journalEntryRepository.save(journalEntry);
            user.getJournalentries().add(saved);
            userService.saveEntry(user);
        } catch (Exception e) {
            log.error(" error ", e);
            throw new RuntimeException(e);
        }
    }

    public void saveUpdateEntry(JournalEntry journalEntry) {
        journalEntryRepository.save(journalEntry);
    }

    public List<JournalEntry> getAll() {
        return journalEntryRepository.findAll();
    }

    public Optional<JournalEntry> findbyID(ObjectId id) {
        return journalEntryRepository.findById(id);
    }

    @Transactional
    public boolean deletebyId(ObjectId id, String userName) {
        boolean removed = false;
        try {
            User user = userService.findbyUserName(userName);
            removed = user.getJournalentries().removeIf(x -> x.getId().equals(id));
            if (removed) {
                userService.saveEntry(user);
                journalEntryRepository.deleteById(id);
            }
        } catch (Exception e) {
            log.error("error ", e);
            throw new RuntimeException("An error occurred while deleting entry.", e);
        }
        return removed;
    }
}
