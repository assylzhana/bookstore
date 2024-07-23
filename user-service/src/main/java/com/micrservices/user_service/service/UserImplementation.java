package com.micrservices.user_service.service;

import com.micrservices.user_service.model.User;
import com.micrservices.user_service.model.Token;
import com.micrservices.user_service.repository.TokenRepository;
import com.micrservices.user_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserImplementation {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
    public void deleteUser(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        List<Token> token = tokenRepository.findAllByUser(user);
        tokenRepository.deleteAll(token);
        userRepository.delete(user);
    }

    public void deleteAccount() {
        User user = getCurrentUser();
        List<Token> token = tokenRepository.findAllByUser(user);
        tokenRepository.deleteAll(token);
        userRepository.delete(user);
    }

    public void changePassword( String oldPassword, String newPassword) {
        User user = userRepository.findByEmail(getCurrentUser().getEmail()).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
