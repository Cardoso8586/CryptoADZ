package com.cryptoadz.service;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.cryptoadz.model.DepositoHistorico;
import com.cryptoadz.model.DepositoPendente;
import com.cryptoadz.model.Usuario;
import com.cryptoadz.repository.DepositoHistoricoRepository;
import com.cryptoadz.repository.DepositoPendenteRepository;
import com.cryptoadz.repository.UsuarioRepository;

import jakarta.transaction.Transactional;

@Service
public class DepositoService {
	
  

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private DepositoHistoricoRepository depositoHistoricoRepository;

    
    @Autowired
    private DepositoPendenteRepository depositoPendenteRepository;

    @Transactional
    public void solicitarDeposito(Long userId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.valueOf(3)) < 0) {
            throw new IllegalArgumentException("O valor mínimo para depósito é 3 USDT.");
        }

        Usuario user = buscarUsuario(userId);

        // Credita saldo
        BigDecimal saldoAtual = user.getUsdtSaldo() != null ? user.getUsdtSaldo() : BigDecimal.ZERO;
        user.setUsdtSaldo(saldoAtual.add(amount));
        usuarioRepository.save(user);

        // Registra depósito
        DepositoPendente deposito = new DepositoPendente();
        deposito.setUser(user);
        deposito.setValorEsperado(amount);
        deposito.setConfirmado(true);
        deposito.setDataSolicitacao(LocalDateTime.now());
        deposito.setStatus("CONFIRMADO");
        depositoPendenteRepository.save(deposito);

        // Histórico
        criarHistorico(user, amount, "CONFIRMADO");
    }

    
    @Transactional
    public void creditarDeposito(Long userId, BigDecimal amount) {
        Usuario user = buscarUsuario(userId);
        BigDecimal saldoAtual = user.getUsdtSaldo() != null ? user.getUsdtSaldo() : BigDecimal.ZERO;
        user.setUsdtSaldo(saldoAtual.add(amount));
        usuarioRepository.save(user);
    }

    private Usuario buscarUsuario(Long userId) {
        return usuarioRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    

    @Transactional
    public void criarHistorico(Usuario user, BigDecimal valor, String status) {
        DepositoHistorico historico = new DepositoHistorico();
        historico.setUser(user);
        historico.setValor(valor);
        historico.setStatus(status);
        historico.setDataDeposito(LocalDateTime.now());
        historico.setIdTransacao(UUID.randomUUID().toString()); // id único interno
       

        depositoHistoricoRepository.save(historico);
    }


    
   

  
    

}
