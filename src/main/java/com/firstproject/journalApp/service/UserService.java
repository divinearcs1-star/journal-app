package com.firstproject.journalApp.service;

import com.firstproject.journalApp.entity.User;
import com.firstproject.journalApp.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private static final PasswordEncoder passwordencoder = new BCryptPasswordEncoder();

    public void saveEntry(User user) {
        userRepository.save(user);
    }

    public boolean saveNewUser(User user) {
        try {
            if (userRepository.findByUserName(user.getUserName()) != null) {
                return false;
            }
            user.setPassWord(passwordencoder.encode(user.getPassWord()));
            user.setRoles(Arrays.asList("USER"));
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            log.debug("error occurred for {}", user.getUserName(), e);
            return false;
        }
    }

    public boolean saveNewToken(User user) {
        try {
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            log.debug("error occurred for {}", user.getUserName(), e);
            return false;
        }
    }

    public boolean saveNewPassword(User user, String newPassword) {
        try {
            user.setPassWord(passwordencoder.encode(newPassword));
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            log.debug("error occurred for {}", user.getUserName(), e);
            return false;
        }
    }

    public void saveAdmin(User user) {
        user.setPassWord(passwordencoder.encode(user.getPassWord()));
        user.setRoles(Arrays.asList("ADMIN", "USER"));
        userRepository.save(user);
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public Optional<User> findbyID(ObjectId id) {
        return userRepository.findById(id);
    }

    public void deletebyId(ObjectId id) {
        userRepository.deleteById(id);
    }

    public void deletebyUserName(String username) {
        userRepository.deleteByUserName(username);
    }

    public User findbyUserName(String username) {
        return userRepository.findByUserName(username);
    }
}
