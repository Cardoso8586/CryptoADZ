package com.cryptoadz.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.cryptoadz.dto.SaquePendenteDTO;
import com.cryptoadz.dto.SaqueResponseDTO;
import com.cryptoadz.dto.SolicitarSaqueDTO;
import com.cryptoadz.model.SaquePendente;
import com.cryptoadz.repository.SaquePendenteRepository;
import com.cryptoadz.service.SaqueService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/saques")
public class SaqueController {

    private final SaqueService saqueService;
    private final SaquePendenteRepository saquePendenteRepository;

    public SaqueController(SaqueService saqueService, SaquePendenteRepository saquePendenteRepository) {
        this.saqueService = saqueService;
        this.saquePendenteRepository = saquePendenteRepository;
    }

    // Solicitar saque com validação
    @PostMapping("/solicitar")
    public ResponseEntity<?> solicitarSaque(@Valid @RequestBody SolicitarSaqueDTO dto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(Map.of(
                "sucesso", false,
                "mensagem", "Dados inválidos",
                "erros", bindingResult.getAllErrors()
            ));
        }

        try {
            SaqueResponseDTO response = saqueService.solicitarSaque(dto);
            if (response.isSucesso()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("sucesso", false, "mensagem", "Erro inesperado: " + e.getMessage()));
        }
    }

   

    // Listar histórico de saques por usuário
    @GetMapping("/historico/{userId}")
    public ResponseEntity<?> listarHistorico(@PathVariable Long userId) {
        try {
            List<SaquePendente> saques = saquePendenteRepository.findByUserId(userId);
            List<SaquePendenteDTO> dtoList = saques.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
            return ResponseEntity.ok(dtoList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("sucesso", false, "mensagem", "Erro ao buscar histórico: " + e.getMessage()));
        }
    }

    // Verificar status do último saque do usuário
    @GetMapping("/status")
    public ResponseEntity<?> verificarStatusSaque(@RequestParam Long userId) {
        try {
            List<SaquePendente> saques = saquePendenteRepository.findUltimoSaquePorUserId(userId);

            if (saques.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("sucesso", false, "mensagem", "Nenhum saque encontrado."));
            }

            SaquePendente saque = saques.get(0); // Último saque
            return ResponseEntity.ok(Map.of("sucesso", true, "status", saque.getStatus().toLowerCase()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("sucesso", false, "mensagem", "Erro ao consultar status: " + e.getMessage()));
        }
    }

    // Método auxiliar para converter entidade em DTO
    private SaquePendenteDTO toDTO(SaquePendente saque) {
        return new SaquePendenteDTO(
            saque.getId(),
            saque.getValor_solicitado(),
            saque.getEnderecoDestino(),
            saque.getStatus(),
            saque.getDataSolicitacao(),
            saque.getTransactionId(),
            saque.isConfirmado()
        );
    }
    
    
}
