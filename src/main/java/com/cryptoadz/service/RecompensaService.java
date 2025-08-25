package com.cryptoadz.service;

import com.cryptoadz.model.Usuario;
import com.cryptoadz.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class RecompensaService {

    private final UsuarioRepository usuarioRepository;

    public RecompensaService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public void adicionarGanho(Usuario usuario, BigDecimal valor) {
        // 1️⃣ Adiciona ganho direto ao usuário
        if (usuario.getGanhosPendentes() == null) {
            usuario.setGanhosPendentes(BigDecimal.ZERO);
        }
        usuario.setGanhosPendentes(usuario.getGanhosPendentes().add(valor));
        usuarioRepository.save(usuario);

        // 2️⃣ Dá 3% ao indicante
        if (usuario.getReferredBy() != null) {
            Long referrerId = usuario.getReferredBy();
            usuarioRepository.findById(referrerId).ifPresent(referrer -> {
                if (referrer.getGanhosPendentesReferral() == null) {
                    referrer.setGanhosPendentesReferral(BigDecimal.ZERO);
                }

                // ✅ Corrigido: calcula apenas 3% do valor da ação
                BigDecimal bonus = valor.multiply(new BigDecimal("0.02"));

                referrer.setGanhosPendentesReferral(
                    referrer.getGanhosPendentesReferral().add(bonus)
                );

                usuarioRepository.save(referrer);
                System.out.println("[Ganho] Usuário " + referrer.getUsername()
                        + " ganhou 2% de " + usuario.getUsername() + ": " + bonus);
            });
        }
    }


    // ========================================================================================================

    @Transactional
    public void claimGanhos(Usuario usuario) {
        BigDecimal totalClaim = BigDecimal.ZERO;

        if (usuario.getGanhosPendentes() != null) {
            totalClaim = totalClaim.add(usuario.getGanhosPendentes());
            usuario.setGanhosPendentes(BigDecimal.ZERO);
        }

        if (usuario.getGanhosPendentesReferral() != null) {
            totalClaim = totalClaim.add(usuario.getGanhosPendentesReferral());
            usuario.setGanhosPendentesReferral(BigDecimal.ZERO);
        }

        usuario.setSaldoTokens(usuario.getSaldoTokens().add(totalClaim));
        usuarioRepository.save(usuario);
    }

}
