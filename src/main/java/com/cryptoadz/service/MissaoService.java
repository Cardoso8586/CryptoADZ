package com.cryptoadz.service;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cryptoadz.dto.MissaoDTO;
import com.cryptoadz.model.MissaoDiaria;
import com.cryptoadz.model.Usuario;
import com.cryptoadz.repository.MissaoDiariaRepository;
import com.cryptoadz.repository.UsuarioRepository;

@Service
public class MissaoService {

    @Autowired
    private MissaoDiariaRepository missaoRepo;

    @Autowired
    private UsuarioRepository usuarioRepo;

    private static final int REQUISITO_ASSISTIR = 20;
    private static final int REQUISITO_CADASTRAR = 1;

    private static final BigDecimal RECOMPENSA_ASSISTIR = new BigDecimal("10");
    private static final BigDecimal RECOMPENSA_CADASTRAR = new BigDecimal("100");

    public MissaoDiaria getOuCriarMissao(Usuario usuario) {
        LocalDate hoje = LocalDate.now();
        return missaoRepo.findByUsuarioAndDataMissao(usuario, hoje)
                .orElseGet(() -> {
                    MissaoDiaria m = new MissaoDiaria();
                    m.setUsuario(usuario);
                    m.setDataMissao(hoje);
                    return missaoRepo.save(m);
                });
    }

    public MissaoDTO getStatus(Usuario usuario) {
        MissaoDiaria m = getOuCriarMissao(usuario);
        MissaoDTO dto = new MissaoDTO();
        dto.setAssistirCompleted(m.getContadorAssistir() >= REQUISITO_ASSISTIR);
        dto.setCadastrarCompleted(m.getContadorCadastrar() >= REQUISITO_CADASTRAR);
        dto.setContadorAssistir(m.getContadorAssistir());
        dto.setContadorCadastrar(m.getContadorCadastrar());
        return dto;
    }

    // ✅ Incrementa apenas o contador de visualizações (sem recompensa)
    public void incrementarAssistir(Long usuarioId) {
        Usuario usuario = usuarioRepo.findById(usuarioId).orElseThrow();
        MissaoDiaria missao = getOuCriarMissao(usuario);
        missao.setContadorAssistir(missao.getContadorAssistir() + 1);
        missaoRepo.save(missao);
    }

    // ✅ Reivindica a recompensa da missão "assistir"
    public String reivindicarAssistir(Long usuarioId) {
        Usuario usuario = usuarioRepo.findById(usuarioId).orElseThrow();
        MissaoDiaria missao = getOuCriarMissao(usuario);

        if (missao.isRecompensaAssistiu()) {
            return "⚠️ You have already claimed this reward today.";
        }

        if (missao.getContadorAssistir() >= REQUISITO_ASSISTIR) {
            usuario.setSaldoTokens(usuario.getSaldoTokens().add(RECOMPENSA_ASSISTIR));
            missao.setRecompensaAssistiu(true);
            usuarioRepo.save(usuario);
            missaoRepo.save(missao);
            return "🎉 Congratulations! You’ve completed 20 views and earned +10 tokens!";
        }

        return "You have watched" + missao.getContadorAssistir() + "/" + REQUISITO_ASSISTIR + ". Keep going!";
    }

    // ✅ Incrementa apenas o contador de cadastro (sem recompensa)
    public void incrementarCadastro(Long usuarioId) {
        Usuario usuario = usuarioRepo.findById(usuarioId).orElseThrow();
        MissaoDiaria missao = getOuCriarMissao(usuario);
        missao.setContadorCadastrar(missao.getContadorCadastrar() + 1);
        missaoRepo.save(missao);
    }

    // ✅ Reivindica a recompensa da missão "cadastrar"
    public String reivindicarCadastro(Long usuarioId) {
        Usuario usuario = usuarioRepo.findById(usuarioId).orElseThrow();
        MissaoDiaria missao = getOuCriarMissao(usuario);

        if (missao.isRecompensaCadastrou()) {
            return "⚠️ You have already claimed this reward today.";
        }

        if (missao.getContadorCadastrar() >= REQUISITO_CADASTRAR) {
            usuario.setSaldoTokens(usuario.getSaldoTokens().add(RECOMPENSA_CADASTRAR));
            missao.setRecompensaCadastrou(true);
            usuarioRepo.save(usuario);
            missaoRepo.save(missao);
            return "🎉 Congrats! Your ad is live and you earned +100 tokens!";
        }

        return "You registered successfully." + missao.getContadorCadastrar() + "/" + REQUISITO_CADASTRAR + ". Keep going!";
    }
}
