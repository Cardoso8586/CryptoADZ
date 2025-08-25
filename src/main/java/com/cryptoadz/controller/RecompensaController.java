package com.cryptoadz.controller;

import com.cryptoadz.model.Usuario;
import com.cryptoadz.service.RecompensaService;
import com.cryptoadz.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.security.core.Authentication;

@Controller
public class RecompensaController {

    @Autowired
    private RecompensaService recompensaService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public RecompensaController() {}

    // Endpoint para o usuário "claimar" os ganhos acumulados
    @PostMapping("/claim")
    public String claimGanhos(Authentication authentication) {
        String username = authentication.getName();
        Usuario usuario = usuarioRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        recompensaService.claimGanhos(usuario);
        return "redirect:/dashboard";
    }

}
