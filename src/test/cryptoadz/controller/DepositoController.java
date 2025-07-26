package com.cryptoadz.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cryptoadz.dto.DepositoHistoricoDTO;
import com.cryptoadz.dto.EnderecoDepositoResponse;
import com.cryptoadz.dto.SolicitacaoDepositoRequest;
import com.cryptoadz.model.DepositoHistorico;
import com.cryptoadz.model.DepositoPendente;
import com.cryptoadz.model.Usuario;
import com.cryptoadz.repository.DepositoHistoricoRepository;
import com.cryptoadz.repository.DepositoPendenteRepository;
import com.cryptoadz.repository.UsuarioRepository;
import com.cryptoadz.service.DepositoService;

@RestController
@RequestMapping("/api/depositos")
public class DepositoController {


    @Autowired
    private DepositoPendenteRepository depositoRepo;

    @Autowired
    private DepositoHistoricoRepository depositoHistoricoRepository;
 

    
    
    @Autowired
    private DepositoService depositoService;

    @PostMapping("/fazer")
    public ResponseEntity<?> fazerDeposito(@RequestBody SolicitacaoDepositoRequest request) {
        depositoService.solicitarDeposito(request.getUserId(), request.getValor());
        return ResponseEntity.ok("Depósito registrado com sucesso");
    }

    
    @GetMapping("/historico/{userId}")
    public ResponseEntity<List<DepositoHistoricoDTO>> listarHistorico(@PathVariable Long userId) {
        List<DepositoHistorico> lista = depositoHistoricoRepository.findByUserId(userId);
        List<DepositoHistoricoDTO> dtoList = lista.stream()
            .map(DepositoHistoricoDTO::new)
            .toList();

        return ResponseEntity.ok(dtoList);
    }
    
    @GetMapping("/status/{userId}")
    public ResponseEntity<String> obterStatusDeposito(@PathVariable Long userId) {
       
		Optional<DepositoPendente> depositoOpt = depositoRepo.findTopByUserIdAndStatusInOrderByDataSolicitacaoDesc(
            userId, List.of("PENDENTE", "CONFIRMADO", "REJEITADO"));
        
        if (depositoOpt.isEmpty()) {
            return ResponseEntity.ok("Nenhum depósito ativo");
        }
        
        return ResponseEntity.ok(depositoOpt.get().getStatus());
    }


    
}
