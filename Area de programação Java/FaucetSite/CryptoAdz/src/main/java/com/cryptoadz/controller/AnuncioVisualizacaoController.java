package com.cryptoadz.controller;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cryptoadz.dto.VisualizacaoResponseDTO;
import com.cryptoadz.service.AnuncioVisualizacaoService;

@RestController
@RequestMapping("/api/visualizacoes")
public class AnuncioVisualizacaoController {

    @Autowired
    private AnuncioVisualizacaoService visualizacaoService;

    /**
     * Registra visualização com username e anúncio passados como parâmetros.
     */
    @PostMapping("/registrar")
    public ResponseEntity<String> registrarVisualizacao(
            @RequestParam String username,
            @RequestParam Long anuncioId
    ) {
        System.out.println("✅ [POST /registrar] username=" + username + ", anuncioId=" + anuncioId);
        try {
            visualizacaoService.registrarVisualizacao(username, anuncioId);
            return ResponseEntity.ok("Visualização registrada com sucesso.");
        } catch (RuntimeException e) {
            System.out.println("❌ Erro ao registrar visualização: " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Verifica se o usuário está bloqueado para visualizar um anúncio.
     */
    @GetMapping("/bloqueado")
    public ResponseEntity<Boolean> estaBloqueado(
            @RequestParam String username,
            @RequestParam Long anuncioId
    ) {
        System.out.println("✅ [GET /bloqueado] username=" + username + ", anuncioId=" + anuncioId);
        try {
            boolean bloqueado = visualizacaoService.estaBloqueado(username, anuncioId);
            System.out.println("🔍 Resultado bloqueado=" + bloqueado);
            return ResponseEntity.ok(bloqueado);
        } catch (Exception e) {
            System.err.println("❌ Erro ao verificar bloqueio: " + e.getMessage());
            return ResponseEntity.status(500).body(false);
        }
    }

    /**
     * Registra visualização com tempo e autenticação (usando o usuário logado via Spring Security).
     */
    @PostMapping("/registrar-visualizacao/{id}")
    public ResponseEntity<Void> registrarVisualizacao(
            @PathVariable Long id,
            Principal principal
    ) {
        System.out.println("📡 [POST /registrar-visualizacao/" + id + "]");

        if (principal == null) {
            System.out.println("❌ Usuário não autenticado. Principal está nulo.");
            return ResponseEntity.status(401).build();
        }

        String username = principal.getName();
        System.out.println("✅ Usuário autenticado: " + username);

        try {
            visualizacaoService.registrarVisualizacao(username, id);
            System.out.println("✅ Visualização registrada com sucesso.");
            return ResponseEntity.ok().build(); // só retorna 200 OK, sem body
        } catch (RuntimeException e) {
            System.out.println("❌ Erro ao registrar visualização: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }


    //=============================
    
    @GetMapping("/status-bloqueio")
    public ResponseEntity<?> getStatusBloqueioComTempo(
            @RequestParam String username,
            @RequestParam Long anuncioId
    ) {
        System.out.println("✅ [GET /status-bloqueio] username=" + username + ", anuncioId=" + anuncioId);
        try {
            boolean bloqueado = visualizacaoService.estaBloqueado(username, anuncioId);
            long tempoRestante = visualizacaoService.getTempoRestanteBloqueio(username, anuncioId); // em segundos

            Map<String, Object> response = new java.util.HashMap<>();  /////    var response = new java.util.HashMap<String, Object>();

            response.put("bloqueado", bloqueado);
            response.put("tempoRestante", tempoRestante);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            System.out.println("❌ Erro ao obter status de bloqueio: " + e.getMessage());
            return ResponseEntity.status(500).body("Erro interno");
        }
    }
//==================================================
    @GetMapping("/tokens-creditados/{id}")
    public ResponseEntity<VisualizacaoResponseDTO> getTokensCreditados(
            @PathVariable Long id,
            Principal principal
    ) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }

        try {
            VisualizacaoResponseDTO dto = visualizacaoService.pegarTokensCreditadosDTO(principal.getName(), id);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(new VisualizacaoResponseDTO(BigDecimal.ZERO));
        }
    }

//=========================================
    
    @GetMapping("/tempo/{id}")
    public ResponseEntity<Map<String, Integer>> obterTempo(@PathVariable Long id) {
        System.out.println("📡 [GET /tempo/" + id + "]");

        try {
            int tempo = visualizacaoService.obterTempoDoAnuncio(id);

            Map<String, Integer> response = new HashMap<>();
            response.put("tempo", tempo);

            return ResponseEntity.ok()
                    .header("Content-Type", "application/json")
                    .body(response);

        } catch (RuntimeException e) {
            System.out.println("❌ Erro ao obter tempo do anúncio: " + e.getMessage());
            return ResponseEntity.status(404).body(Collections.singletonMap("tempo", 0));
        }
    }

    //==============================================================================
}


