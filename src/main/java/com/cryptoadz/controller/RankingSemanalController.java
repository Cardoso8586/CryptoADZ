package com.cryptoadz.controller;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.cryptoadz.dto.UsuarioRankingDTO;
import com.cryptoadz.model.Usuario;
import com.cryptoadz.service.RankingSemanalService;

@RestController
public class RankingSemanalController {

	
    private final RankingSemanalService rankingService;

    public RankingSemanalController(RankingSemanalService rankingService) {
        this.rankingService = rankingService;
    }

    @GetMapping("/api/ranking-semanal")
    public List<Usuario> getRankingSemanal() {
        return rankingService.getRankingSemanalTop(10);
        
    }

    
    @GetMapping("/api/usuario/{id}/premio-pendente")
    public BigDecimal getPremioPendente(@PathVariable Long id) {
        return rankingService.getPremioPendente(id);
    }

    @PostMapping("/api/usuario/{id}/confirmar-premio")
    public ResponseEntity<String> confirmarPremio(@PathVariable Long id) {
        boolean confirmado = rankingService.confirmarPremio(id);
        if (confirmado) {
            return ResponseEntity.ok("Prêmio creditado com sucesso!");
        } else {
            return ResponseEntity.badRequest().body("Nenhum prêmio pendente.");
        }
    }
   
    @GetMapping("/api/minha-posicao")
    public ResponseEntity<UsuarioRankingDTO> getMinhaPosicao(@RequestParam Long usuarioId) {
        UsuarioRankingDTO posicao = rankingService.getMinhaPosicao(usuarioId);
        if (posicao == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(posicao);
    }

  
}
