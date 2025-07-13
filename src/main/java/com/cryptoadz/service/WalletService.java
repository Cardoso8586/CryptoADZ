package com.cryptoadz.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.cryptoadz.model.Usuario;
import com.cryptoadz.repository.UsuarioRepository;

import jakarta.transaction.Transactional;

@Service
public class WalletService {

    private final UsuarioRepository usuarioRepository;

    public WalletService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public void creditarDeposito(Long userId, BigDecimal amount) {
        Usuario user = buscarUsuario(userId);
        BigDecimal saldoAtual = user.getUsdtWallet() != null ? user.getUsdtWallet() : BigDecimal.ZERO;
        user.setUsdtWallet(saldoAtual.add(amount));
        usuarioRepository.save(user);
    }

    @Transactional
    public boolean sacar(Long userId, BigDecimal amount) {
        Usuario user = buscarUsuario(userId);
        BigDecimal saldoAtual = user.getUsdtWallet() != null ? user.getUsdtWallet() : BigDecimal.ZERO;
        if (saldoAtual.compareTo(amount) < 0) {
            return false;
        }
        user.setUsdtWallet(saldoAtual.subtract(amount));
        usuarioRepository.save(user);
        return true;
    }

    private Usuario buscarUsuario(Long userId) {
        return usuarioRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + userId));
    }
}

