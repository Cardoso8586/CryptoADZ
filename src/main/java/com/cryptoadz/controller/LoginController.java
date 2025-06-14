package com.cryptoadz.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.util.Map;


//site key= 0x4AAAAAABeuEKIL4oQVYiXR
@Controller
public class LoginController {

  
    @Value("${turnstile.secret}")
	private String turnstileSecret;
    @PostMapping("/login")
    public String login(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam(name = "cf-turnstile-response") String captchaToken,
            Model model
    ) {
        if (!verificarCaptcha(captchaToken)) {
            model.addAttribute("error", "Falha na verificação do CAPTCHA.");
            return "login";
        }

        // Aqui você pode colocar sua lógica de autenticação de usuário:
        if (username.equals("admin") && password.equals("1234")) {
            return "redirect:/dashboard"; // ou a página principal
        }

        model.addAttribute("error", "Credenciais inválidas.");
        return "login";
    }

    private boolean verificarCaptcha(String token) {
        String url = "https://challenges.cloudflare.com/turnstile/v0/siteverify";
        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("secret", turnstileSecret);
        body.add("response", token);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, body, Map.class);
            Map<String, Object> json = response.getBody();
            return json != null && Boolean.TRUE.equals(json.get("success"));
        } catch (Exception e) {
            return false;
        }
    }
}

//  Secret Key localhost  turnstile.secret=0x4AAAAAABeuEE4p3cvwuCkha8xhAxFWCJ4

// data-sitekey=  0x4AAAAAABeuEKIL4oQVYiXR


//===============================================================================

///data-sitekey=    0x4AAAAAABhDBlDrEgbq9kdW               
//                   

//                       
// Secret Key Railway   turnstile.secret=0x4AAAAAABhDBvnkevUzbkiH3mxAW-Ct3tg
//          





