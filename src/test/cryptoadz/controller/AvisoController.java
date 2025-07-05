package com.cryptoadz.controller;

import com.cryptoadz.model.Aviso;
import com.cryptoadz.service.AvisoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/avisos")
@CrossOrigin(origins = "*") // 🔓 Permite requisições de qualquer origem (ideal ajustar em produção)
public class AvisoController {

    private final AvisoService avisoService;

    // ✅ Injeção de dependência via construtor
    public AvisoController(AvisoService avisoService) {
        this.avisoService = avisoService;
    }

    // 🔎 Lista todos os avisos
    @GetMapping
    public ResponseEntity<List<Aviso>> listar() {
        List<Aviso> avisos = avisoService.listarTodos();
        return ResponseEntity.ok(avisos);
    }

    // 🔎 Busca aviso por ID
    @GetMapping("/{id}")
    public ResponseEntity<Aviso> buscarPorId(@PathVariable Long id) {
        return avisoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ➕ Cria novo aviso
    @PostMapping
    public ResponseEntity<Aviso> criar(@RequestBody Aviso aviso) {
        Aviso salvo = avisoService.salvar(aviso);
        return ResponseEntity.ok(salvo);
    }

    // ✏️ Atualiza aviso existente
    @PutMapping("/{id}")
    public ResponseEntity<Aviso> atualizar(@PathVariable Long id, @RequestBody Aviso aviso) {
        try {
            Aviso atualizado = avisoService.atualizar(id, aviso);
            return ResponseEntity.ok(atualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ❌ Deleta aviso por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        avisoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}

