package com.cryptoadz.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cryptoadz.dto.SwapRequest;
import com.cryptoadz.model.Usuario;
import com.cryptoadz.repository.UsuarioRepository;

@Service
public class SwapService {

    private final UsuarioRepository usuarioRepository;

    private static final BigDecimal TAXA = new BigDecimal("0.02"); // 2%
    private static final BigDecimal MIL = new BigDecimal("1000");
    private static final BigDecimal UM = BigDecimal.ONE;

    public SwapService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public BigDecimal realizarSwap(SwapRequest request) {
        Usuario usuario = usuarioRepository.findById(request.getUserId())
        		
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        BigDecimal valor = BigDecimal.valueOf(request.getFromAmount());

        // Validação valor mínimo conforme tipo
        if ("token".equalsIgnoreCase(request.getFrom()) && valor.compareTo(MIL) < 0) {
            throw new IllegalArgumentException("O valor mínimo para troca de token é 1000.");
        }

        if ("usdt".equalsIgnoreCase(request.getFrom()) && valor.compareTo(UM) < 0) {
            throw new IllegalArgumentException("O valor mínimo para troca de USDT é 1.");
        }

        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor inválido para troca");
        }

        if ("usdt".equalsIgnoreCase(request.getFrom()) && "token".equalsIgnoreCase(request.getTo())) {
            if (usuario.getUsdtSaldo().compareTo(valor) < 0) {
                throw new IllegalArgumentException("Saldo USDT insuficiente");
            }

            // tokensRecebidos = (valor * 1000) * (1 - taxa)
            BigDecimal tokensRecebidos = valor
                .multiply(MIL)
                .multiply(BigDecimal.ONE.subtract(TAXA))
                .setScale(8, RoundingMode.HALF_UP); // ajustar casas decimais conforme necessidade

            usuario.setUsdtSaldo(usuario.getUsdtSaldo().subtract(valor));
            usuario.setSaldoTokens(usuario.getSaldoTokens().add(tokensRecebidos));
            usuarioRepository.save(usuario);

            return tokensRecebidos;

        } else if ("token".equalsIgnoreCase(request.getFrom()) && "usdt".equalsIgnoreCase(request.getTo())) {
            if (usuario.getSaldoTokens().compareTo(valor) < 0) {
                throw new IllegalArgumentException("Saldo Token insuficiente");
            }

            // usdtRecebido = (valor / 1000) * (1 - taxa)
            BigDecimal usdtRecebido = valor
                .divide(MIL, 8, RoundingMode.HALF_UP)
                .multiply(BigDecimal.ONE.subtract(TAXA))
                .setScale(8, RoundingMode.HALF_UP);

            usuario.setSaldoTokens(usuario.getSaldoTokens().subtract(valor));
            usuario.setUsdtSaldo(usuario.getUsdtSaldo().add(usdtRecebido));
            usuarioRepository.save(usuario);

            return usdtRecebido;

        } else {
            throw new IllegalArgumentException("Tipos inválidos para troca. Use 'usdt' para 'token' ou vice-versa.");
        }
    }
}
