package com.cryptoadz.controller;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cryptoadz.model.Usuario;
import com.cryptoadz.repository.UsuarioRepository;

@Controller
public class SaldoController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/api/saldo")
    @ResponseBody
    public Map<String, BigDecimal> getSaldoTokens(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();

        Usuario usuario = usuarioRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        return Map.of("saldo", usuario.getSaldoTokens());
    }

    @GetMapping("/api/saldoUsdt")
    @ResponseBody
    public Map<String, BigDecimal> getSaldoUsdt(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();

        Usuario usuario = usuarioRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        return Map.of("saldoUsdt", usuario.getUsdtSaldo());
    }


    
    
    
}
