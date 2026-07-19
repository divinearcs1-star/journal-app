package com.firstproject.journalApp.service;

import com.firstproject.journalApp.entity.User;
import com.firstproject.journalApp.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
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

//    public  static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public void saveEntry(User user) {
        userRepository.save(user);
    }

    public boolean saveNewUser(User user) {
        try {
            user.setPassWord(passwordencoder.encode(user.getPassWord()));
            user.setRoles(Arrays.asList("USER"));
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

    public User findbyUserName(String username) {
        return userRepository.findByUserName(username);
    }
}
