package com.cryptoadz.service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cryptoadz.dto.VisualizacaoResponseDTO;
import com.cryptoadz.model.AnuncioVisualizacao;
import com.cryptoadz.model.Anuncios;
import com.cryptoadz.model.Usuario;
import com.cryptoadz.repository.AnuncioRepository;
import com.cryptoadz.repository.AnuncioVisualizacaoRepository;
import com.cryptoadz.repository.UsuarioRepository;

import jakarta.transaction.Transactional;
@Service
public class AnuncioVisualizacaoService {

	  @Autowired
	    private AnuncioVisualizacaoRepository visualizacaoRepo;

	    @Autowired
	    private AnuncioRepository anuncioRepo;

	    @Autowired
	    private UsuarioRepository usuarioRepo;

	    @Autowired
	    private RecompensaService recompensaService;
	  
	    @Transactional
	    public int registrarVisualizacao(String username, Long anuncioId) {
	        Anuncios anuncio = anuncioRepo.findById(anuncioId)
	                .orElseThrow(() -> new RuntimeException("Anúncio não encontrado"));

	        int tempoBloqueioHoras = anuncio.getBloqueio_horas();
	        if (tempoBloqueioHoras <= 0) tempoBloqueioHoras = 3;

	        LocalDateTime agora = LocalDateTime.now();

	        AnuncioVisualizacao existente = visualizacaoRepo.findByUsernameAndAnuncio_Id(username, anuncioId);

	        Optional<Usuario> usuarioOpt = usuarioRepo.findByUsername(username);
	        if (usuarioOpt.isEmpty()) {
	            throw new RuntimeException("Usuário não encontrado para creditar tokens.");
	        }
	        Usuario usuario = usuarioOpt.get();

	        if (existente != null) {
	            if (existente.getBloqueioExpiraEm() != null && agora.isBefore(existente.getBloqueioExpiraEm())) {
	                long minutosRestantes = Duration.between(agora, existente.getBloqueioExpiraEm()).toMinutes();
	                System.out.println("[BLOQUEADO] Usuário " + username + " ainda bloqueado por " + minutosRestantes + " minutos.");
	                return -1;
	            } else {
	                existente.setBloqueioExpiraEm(agora.plusHours(tempoBloqueioHoras));

	                // Incrementa totalClicks na visualização existente
	                existente.setTotalClicks(existente.getTotalClicks() + 1);

	                // Credita tokens na renovação
	                creditarTokens(usuario, anuncioId, BigDecimal.ZERO);

	                // Incrementa quantidadeVisualizacaoSemanal do usuário
	                usuario.setQuantidadeVisualizacaoSemanal(usuario.getQuantidadeVisualizacaoSemanal() + 1);

	                BigDecimal valorDoAnuncio = Optional.ofNullable(anuncio.getTokensPorVisualizacao()).orElse(BigDecimal.ZERO);
	                recompensaService.adicionarGanho(usuario, valorDoAnuncio);
	                visualizacaoRepo.save(existente);
	                usuarioRepo.save(usuario);

	                processarVisualizacaoRestante(anuncio);
	                return anuncio.getTempoVisualizacao() != null ? anuncio.getTempoVisualizacao() : 20;
	            }
	        } else {
	            AnuncioVisualizacao nova = new AnuncioVisualizacao();
	            nova.setUsername(username);
	            nova.setAnuncio(anuncio);
	            nova.setBloqueioExpiraEm(agora.plusHours(tempoBloqueioHoras));
	            nova.setTotalClicks(1L);

	            creditarTokens(usuario, anuncioId, BigDecimal.ZERO);

	         
	            BigDecimal valorDoAnuncio = Optional.ofNullable(anuncio.getTokensPorVisualizacao()).orElse(BigDecimal.ZERO);
	             // adiciona o ganho para o usuário e para quem o indicou
	                recompensaService.adicionarGanho(usuario, valorDoAnuncio);
	            usuario.setQuantidadeVisualizacaoSemanal(usuario.getQuantidadeVisualizacaoSemanal() + 1);

	            visualizacaoRepo.save(nova);
	            usuarioRepo.save(usuario);
                processarVisualizacaoRestante(anuncio);
	            return anuncio.getTempoVisualizacao() != null ? anuncio.getTempoVisualizacao() : 20;
	        }
	    }

//===================================== processarVisualizacaoRestante =============================================
	    private void processarVisualizacaoRestante(Anuncios anuncio) {
	        int max = anuncio.getMaxVisualizacoes();
	        if (max <= 0) {
	            throw new RuntimeException("Anúncio sem visualizações restantes");
	        }

	        if (max == 1) {
	       
	            visualizacaoRepo.deleteByAnuncio_Id(anuncio.getId());
	            anuncioRepo.delete(anuncio);
	        } else {
	            anuncio.setMaxVisualizacoes(max - 1);
	            anuncioRepo.save(anuncio);
	        }
	    }

//============================================================================


    public boolean estaBloqueado(String username, Long anuncioId) {
        AnuncioVisualizacao existente = visualizacaoRepo.findByUsernameAndAnuncio_Id(username, anuncioId);
        return existente != null &&
               existente.getBloqueioExpiraEm() != null &&
               LocalDateTime.now().isBefore(existente.getBloqueioExpiraEm());
    }
  
  //====================================== getTempoRestanteBloqueio =====================================================================
    
    public Long getTempoRestanteBloqueio(String username, Long anuncioId) {
        AnuncioVisualizacao existente = visualizacaoRepo.findByUsernameAndAnuncio_Id(username, anuncioId);

        if (existente == null || existente.getBloqueioExpiraEm() == null) {
            // Sem bloqueio registrado
            return 0L;
        }

        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime bloqueioExpiraEm = existente.getBloqueioExpiraEm();

        if (agora.isAfter(bloqueioExpiraEm)) {
            // Bloqueio expirou
            return 0L;
        }

        Duration duracao = Duration.between(agora, bloqueioExpiraEm);
        return duracao.getSeconds();
    }

  //========================================================================================================  
    
    @Transactional
    public VisualizacaoResponseDTO pegarTokensCreditadosDTO(String username, Long anuncioId) {
        Anuncios anuncio = anuncioRepo.findById(anuncioId)
                .orElseThrow(() -> new RuntimeException("Anúncio não encontrado"));

        // Só verifica se o usuário existe
        if (usuarioRepo.findByUsername(username).isEmpty()) {
            throw new RuntimeException("Usuário não encontrado");
        }

        BigDecimal tokens = Optional.ofNullable(anuncio.getTokensPorVisualizacao()).orElse(BigDecimal.ZERO);

        return new VisualizacaoResponseDTO(tokens);
    }

    //============================================================================================================
    
    private void creditarTokens(Usuario usuario, Long anuncioId,  BigDecimal tokens) {
    	  Anuncios anuncio = anuncioRepo.findById(anuncioId)
                  .orElseThrow(() -> new RuntimeException("Anúncio não encontrado"));
    	  
        BigDecimal tokensCreditados = Optional.ofNullable(anuncio.getTokensPorVisualizacao()).orElse(BigDecimal.ZERO);
        BigDecimal saldoAtual = Optional.ofNullable(usuario.getSaldoTokens()).orElse(BigDecimal.ZERO);
        usuario.setSaldoTokens(saldoAtual.add(tokensCreditados));
        usuarioRepo.save(usuario);
        usuarioRepo.flush();
      
    }

  //=================================================================================
    
    public int obterTempoDoAnuncio(Long anuncioId) {
        Anuncios anuncio = anuncioRepo.findById(anuncioId)
            .orElseThrow(() -> new RuntimeException("Anúncio não encontrado"));

       
        return anuncio.getTempoVisualizacao() != null ? anuncio.getTempoVisualizacao() : 30;
    }


}
  


