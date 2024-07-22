package com.micrservices.user_service.controller;

import com.micrservices.user_service.exception.InvalidEmailException;
import com.micrservices.user_service.model.AuthenticationResponse;
import com.micrservices.user_service.model.Token;
import com.micrservices.user_service.model.User;
import com.micrservices.user_service.repository.TokenRepository;
import com.micrservices.user_service.repository.UserRepository;
import com.micrservices.user_service.service.AuthenticationService;
import com.micrservices.user_service.service.EmailVerificationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authService;
    private final TokenRepository tokenRepository;
    private final EmailVerificationService emailVerificationService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User request) {
        boolean isEmailValid = emailVerificationService.isEmailValid(request.getEmail());
        if (!isEmailValid) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid email address");
        }
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email in use");
        }
        AuthenticationResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
            @RequestBody User request
    ) {
        return ResponseEntity.ok(authService.authenticate(request));
    }
    @PostMapping("/refresh_token")
    public ResponseEntity refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        return authService.refreshToken(request, response);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization header missing or invalid");
        }

        String token = authHeader.substring(7);
        Token storedToken = tokenRepository.findByAccessToken(token).orElse(null);
        if (storedToken != null) {
            if (storedToken.isLoggedOut()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token already logged out");
            }
            storedToken.setLoggedOut(true);
            tokenRepository.save(storedToken);
            return ResponseEntity.ok("Successfully logged out");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid token");
        }
    }
}
