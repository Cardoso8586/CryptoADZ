package com.cryptoadz.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cryptoadz.service.BonusDiarioService;

@RestController
@RequestMapping("/api/bonus")
public class BonusDiarioController {

    @Autowired
    private BonusDiarioService bonusService;

    // 🔥 Coletar bônus diário
    @PostMapping("/coletar/{usuarioId}")
    public ResponseEntity<String> coletarBonus(@PathVariable Long usuarioId) {
        String resposta = bonusService.coletarBonusDiario(usuarioId);
        return ResponseEntity.ok(resposta);
    }

    // 🟢 Verificar se bônus está disponível (opcional, mas útil no frontend)
    @GetMapping("/verificar/{usuarioId}")
    public ResponseEntity<?> verificarBonus(@PathVariable Long usuarioId) {
        boolean disponivel = bonusService.verificarDisponibilidade(usuarioId);
        String mensagem = disponivel 
            ? "Bônus diário disponível para coletar!" 
            : "Você já coletou seu bônus hoje.";

        return ResponseEntity.ok(Map.of(
            "disponivel", disponivel,
            "mensagem", mensagem
        ));
    }
}
