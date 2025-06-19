package com.cryptoadz.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cryptoadz.model.BonusDiario;
import com.cryptoadz.model.Usuario;
import com.cryptoadz.repository.BonusDiarioRepository;
import com.cryptoadz.repository.UsuarioRepository;

@Service
public class BonusDiarioService {

    @Autowired
    private BonusDiarioRepository bonusRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Mapeia o streak para um valor específico
    private BigDecimal getValorBonusPorStreak(int streak) {
        switch (streak) {
            case 1: return new BigDecimal("5");
            case 2: return new BigDecimal("10");
            case 3: return new BigDecimal("15");
            case 4: return new BigDecimal("20");
            case 5: return new BigDecimal("25");
            case 6: return new BigDecimal("30");
            case 7: return new BigDecimal("50"); // prêmio máximo
            default: return new BigDecimal("5"); // fallback se algo der errado
        }
    }

    public String coletarBonusDiario(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        LocalDate hoje = LocalDate.now();

        // Verifica se já coletou o bônus hoje
        Optional<BonusDiario> bonusHoje = bonusRepository.findByUsuarioAndDataColeta(usuario, hoje);
        if (bonusHoje.isPresent()) {
            return "Você já coletou seu bônus diário hoje!";
        }

        // Busca o último registro de bônus coletado antes de hoje
        Optional<BonusDiario> ultimoBonusOpt = bonusRepository.findTopByUsuarioAndDataColetaBeforeOrderByDataColetaDesc(usuario, hoje);

        int streak = 1; // streak padrão

        if (ultimoBonusOpt.isPresent()) {
            BonusDiario ultimoBonus = ultimoBonusOpt.get();

            // Se coletou ontem, incrementa streak
            if (ultimoBonus.getDataColeta().plusDays(1).equals(hoje)) {
                streak = Math.min(ultimoBonus.getStreak() + 1, 7); // máximo 7
            } else {
                streak = 1; // se não coletou ontem, zera
            }
        }

        // Obtem o valor específico do bônus baseado no streak
        BigDecimal valorBonus = getValorBonusPorStreak(streak);

        // Atualiza saldo do usuário
        usuario.setSaldoTokens(usuario.getSaldoTokens().add(valorBonus));
        usuarioRepository.save(usuario);

        // Salva registro do bônus diário
        BonusDiario bonus = new BonusDiario();
        bonus.setUsuario(usuario);
        bonus.setDataColeta(hoje);
        bonus.setQuantidadeBonus(valorBonus);
        bonus.setStreak(streak);
        bonusRepository.save(bonus);

        return "Bônus diário de " + valorBonus + " tokens coletado com sucesso! (" 
                + streak + (streak == 1 ? " dia seguido" : " dias seguidos") + ")";
    }

    public boolean verificarDisponibilidade(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        LocalDate hoje = LocalDate.now();

        return bonusRepository.findByUsuarioAndDataColeta(usuario, hoje).isEmpty();
    }
}


