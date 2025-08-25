package com.cryptoadz.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Random;

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
    
    @Autowired
    private RecompensaService recompensaService;

    private BigDecimal getValorBonusPorStreak(int streak) {
        switch (streak) {
            case 1: return new BigDecimal("0.10");
            case 2: return new BigDecimal("0.20");
            case 3: return new BigDecimal("0.30");
            case 4: return new BigDecimal("0.40");
            case 5: return new BigDecimal("0.50");
            case 6: return new BigDecimal("0.60");
            case 7: return new BigDecimal("0.70");
            default: return new BigDecimal("0.70");
        }
    }

    public String coletarBonusDiario(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        LocalDate hoje = LocalDate.now();

        Optional<BonusDiario> bonusHoje = bonusRepository.findByUsuarioAndDataColeta(usuario, hoje);
        if (bonusHoje.isPresent()) {
            return "Você já coletou seu bônus diário hoje!";
        }

        Optional<BonusDiario> ultimoBonusOpt = bonusRepository.findTopByUsuarioAndDataColetaBeforeOrderByDataColetaDesc(usuario, hoje);

        int streak = 1;

        if (ultimoBonusOpt.isPresent()) {
            BonusDiario ultimoBonus = ultimoBonusOpt.get();

            if (ultimoBonus.getDataColeta().plusDays(1).equals(hoje)) {
                Integer ultimoStreak = ultimoBonus.getStreak();
                if (ultimoStreak == null) { // tratamento para null
                    ultimoStreak = 0;
                }
                streak = Math.min(ultimoStreak + 1, 7);
            } else {
                streak = 1;
            }
        }

        
        BigDecimal valorBonus = getValorBonusPorStreak(streak);
        usuario.setSaldoTokens(usuario.getSaldoTokens().add(valorBonus));
        
        BigDecimal valor_bonus_diario = valorBonus;
        recompensaService.adicionarGanho(usuario, valor_bonus_diario);
        
        usuarioRepository.save(usuario);

        BonusDiario bonus = new BonusDiario();
        bonus.setUsuario(usuario);
        bonus.setDataColeta(hoje);
        bonus.setQuantidadeBonus(valorBonus);
        bonus.setStreak(streak);
        bonusRepository.save(bonus);

        if (streak == 7) {
            String[] mensagensMotivacionais = {
                "Parabéns! 7 dias consecutivos! Você está construindo um ótimo hábito! 🚀",
                "Incrível! 7 dias de foco total! Mantenha esse ritmo! 🔥",
                "Você chegou longe! 7 dias seguidos — isso é comprometimento de verdade! 🎯",
                "Excelente! 7 dias firmes! Continue nessa jornada de sucesso! 🌟",
                "Show! Você completou 7 dias! Siga firme e vá além! 🏆"
            };

            Random random = new Random();
            int indice = random.nextInt(mensagensMotivacionais.length);
            return mensagensMotivacionais[indice];
        }

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


