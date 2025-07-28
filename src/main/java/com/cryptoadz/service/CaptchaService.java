package com.cryptoadz.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.cryptoadz.config.AppConfig;

import java.util.Map;

@Service
public class CaptchaService {

   // @Value("${turnstile.secret}")
  //  private String secretKey;

	private final String secretKey = AppConfig.getTurnstileSecret();

    public boolean isValid(String token) {
        String url = "https://challenges.cloudflare.com/turnstile/v0/siteverify";
        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("secret", secretKey);
        params.add("response", token);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            System.out.println("Resposta do CAPTCHA: " + response.getBody());
            return response.getBody() != null && Boolean.TRUE.equals(response.getBody().get("success"));
        } catch (Exception e) {
            e.printStackTrace(); // Temporário
            return false;
        }
    }

}
