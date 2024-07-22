package com.micrservices.user_service.service;

import com.micrservices.user_service.dto.HunterResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    @Value("${hunter.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;


    public boolean isEmailValid(String email) {
        String url = "https://api.hunter.io/v2/email-verifier?email=" + email + "&api_key=" + apiKey;
        ResponseEntity<HunterResponse> response = restTemplate.getForEntity(url, HunterResponse.class);
        if (response.getBody() != null) {
            return "valid".equals(response.getBody().getData().getStatus());
        }
        return false;
    }
}
