package com.cryptoadz.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
	private UsuarioRepository  usuarioRepository;
    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private RecompensaService recompensaService;
    
    private static final int REQUISITO_ASSISTIR = 20;
    private static final int REQUISITO_CADASTRAR = 1;

    private static final BigDecimal RECOMPENSA_ASSISTIR = new BigDecimal("5");
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
        dto.setRecompensa_Assistir(getRecompensa_Assistir());
        dto.setRecompensa_Registrar(getRecompensa_Registrar());
        return dto;
    }

    private BigDecimal getRecompensa_Registrar() {
		
		return RECOMPENSA_CADASTRAR;
	}

	public BigDecimal getRecompensa_Assistir() {
  		return RECOMPENSA_ASSISTIR;
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
            return "⚠️ Você já resgatou essa recompensa hoje.";
        }

        if (missao.getContadorAssistir() >= REQUISITO_ASSISTIR) {
            usuario.setSaldoTokens(usuario.getSaldoTokens().add(RECOMPENSA_ASSISTIR));
            missao.setRecompensaAssistiu(true);
            
            BigDecimal valor_reconspensa_assistir = RECOMPENSA_ASSISTIR;
            recompensaService.adicionarGanho(usuario, valor_reconspensa_assistir);
            
            usuarioRepo.save(usuario);
            missaoRepo.save(missao);
            return "🎉 Parabéns! Você completou " + REQUISITO_ASSISTIR + " visualizações e ganhou " + RECOMPENSA_ASSISTIR + " ADZ tokens!";

        }

        return "Você assistiu" + missao.getContadorAssistir() + "/" + REQUISITO_ASSISTIR + ". Continue assim!" ;
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
            return "⚠️ Você já coletou esta recompensa hoje.";
        }

        if (missao.getContadorCadastrar() >= REQUISITO_CADASTRAR) {
            usuario.setSaldoTokens(usuario.getSaldoTokens().add(RECOMPENSA_CADASTRAR));
            missao.setRecompensaCadastrou(true);
            
            BigDecimal valor_reconspensa_cadastro = RECOMPENSA_CADASTRAR;
            recompensaService.adicionarGanho(usuario, valor_reconspensa_cadastro);
            
            usuarioRepo.save(usuario);
            missaoRepo.save(missao);
            return "🎉 Parabéns! Seu anúncio está ativo e você ganhou +" + RECOMPENSA_CADASTRAR + " ADZ tokens!";

        }

        return "Você se registrou com sucesso." + missao.getContadorCadastrar() + "/" + REQUISITO_CADASTRAR + ". Continue assim!";
    }
    
    //========================
    public BigDecimal getQuantidadeMeusAnuncios(String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.SC_NOT_FOUND, "Usuário não encontrado", null));
        return Optional.ofNullable(usuario.getMeusAnuncios()).orElse(BigDecimal.ZERO);
    }
}
