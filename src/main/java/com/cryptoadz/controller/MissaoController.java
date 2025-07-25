package com.cryptoadz.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.cryptoadz.dto.MissaoDTO;
import com.cryptoadz.model.Usuario;
import com.cryptoadz.repository.UsuarioRepository;
import com.cryptoadz.service.MissaoService;

@RestController
@RequestMapping("/api/missoes")

public class MissaoController {

    @Autowired
    private MissaoService missaoService;

    @Autowired
    private UsuarioRepository usuarioRepo;

    @GetMapping("/status/{usuarioId}")
    public ResponseEntity<MissaoDTO> getStatus(@PathVariable Long usuarioId) {
        Usuario usuario = usuarioRepo.findById(usuarioId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Usuário não encontrado"));
        MissaoDTO dto = missaoService.getStatus(usuario);
    
        
        return ResponseEntity.ok(dto);
    }


   
 // Incrementar contador de visualizações
    @PostMapping("/incrementar-assistir/{usuarioId}")
    public ResponseEntity<Void> incrementarAssistir(@PathVariable Long usuarioId) {
        if (!usuarioRepo.existsById(usuarioId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado");
        }
        missaoService.incrementarAssistir(usuarioId);
        return ResponseEntity.ok().build();
    }

    // Reivindicar recompensa por assistir
    @PostMapping("/reivindicar-assistir/{usuarioId}")
    public ResponseEntity<String> reivindicarAssistir(@PathVariable Long usuarioId) {
        if (!usuarioRepo.existsById(usuarioId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado");
        }
        return ResponseEntity.ok(missaoService.reivindicarAssistir(usuarioId));
    }

    // Reivindicar recompensa por cadastro
    @PostMapping("/reivindicar-cadastro/{usuarioId}")
    public ResponseEntity<String> reivindicarCadastro(@PathVariable Long usuarioId) {
        if (!usuarioRepo.existsById(usuarioId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado");
        }
        return ResponseEntity.ok(missaoService.reivindicarCadastro(usuarioId));
    }
    
    @PostMapping("/incrementar-cadastro/{usuarioId}")
    public ResponseEntity<Void> incrementarCadastro(@PathVariable Long usuarioId) {
        if (!usuarioRepo.existsById(usuarioId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado");
        }
        missaoService.incrementarCadastro(usuarioId);
        return ResponseEntity.ok().build();
    }
}
