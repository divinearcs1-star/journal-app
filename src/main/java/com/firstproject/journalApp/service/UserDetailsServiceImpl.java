package com.firstproject.journalApp.service;

import com.firstproject.journalApp.entity.User;
import com.firstproject.journalApp.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        User user = userRepository.findByUserName(userName);
        if (user == null) {
            log.warn("Login failed, User not found : {}", userName);
            throw new UsernameNotFoundException(
                    "User not found with username : " + userName
            );
        }
        log.info("User authenticated successfully : {}", userName);
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUserName())
                .password(user.getPassWord())
                .roles(user.getRoles().toArray(new String[0]))
                .build();
    }
}
