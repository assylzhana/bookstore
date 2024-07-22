package com.micrservices.user_service.config;

import com.micrservices.user_service.repository.TokenRepository;
import com.micrservices.user_service.model.Token;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomLogoutHandler implements LogoutHandler {
    private final TokenRepository tokenRepository;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        String token = authHeader.substring(7);
        Token storedToken = tokenRepository.findByAccessToken(token).orElse(null);
        if (storedToken != null) {
            if (storedToken.isLoggedOut()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                log.info("Attempted logout with already logged out token: {}", token);
                return;
            }
            storedToken.setLoggedOut(true);
            tokenRepository.save(storedToken);
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            log.info("Attempted logout with invalid token: {}", token);
        }
    }

}