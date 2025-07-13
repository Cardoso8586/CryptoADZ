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

import com.cryptoadz.dto.EnderecoDepositoResponse;
import com.cryptoadz.dto.SolicitacaoDepositoRequest;
import com.cryptoadz.model.DepositoPendente;
import com.cryptoadz.model.Usuario;
import com.cryptoadz.repository.DepositoPendenteRepository;
import com.cryptoadz.repository.UsuarioRepository;
import com.cryptoadz.service.DepositoService;

@RestController
@RequestMapping("/api/depositos")
public class DepositoController {

    @Value("${deposito.endereco.fixo}")
    private String enderecoFixo;

    @Autowired
    private DepositoPendenteRepository depositoRepo;

    @Autowired
    private UsuarioRepository usuarioRepository;

    
    @PostMapping("/fazer")
    public ResponseEntity<?> fazerDeposito(@RequestBody SolicitacaoDepositoRequest request) {
        Optional<Usuario> userOpt = usuarioRepository.findById(request.getUserId());
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Usuário não encontrado");
        }

        Usuario usuario = userOpt.get();

        DepositoPendente deposito = new DepositoPendente();
        deposito.setUser(usuario);
        deposito.setValorEsperado(request.getValor());
        deposito.setConfirmado(false);
        deposito.setDataSolicitacao(LocalDateTime.now());
        deposito.setEnderecoDeposito(enderecoFixo);

        depositoRepo.save(deposito);

        return ResponseEntity.ok(new EnderecoDepositoResponse(enderecoFixo));
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
