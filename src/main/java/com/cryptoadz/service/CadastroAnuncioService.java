package com.cryptoadz.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.stream.Collectors;

import com.cryptoadz.dto.AnuncioDTO;
import com.cryptoadz.dto.AnuncioEdicaoDTO;
import com.cryptoadz.dto.AnuncioEdicaoResponseDTO;
import com.cryptoadz.dto.AnuncioListagemDTO;
import com.cryptoadz.dto.AnuncioResponseDTO;
import com.cryptoadz.model.Anuncios;
import com.cryptoadz.model.Usuario;
import com.cryptoadz.repository.AnuncioRepository;
import com.cryptoadz.repository.UsuarioRepository;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
@Service
@Transactional

public class CadastroAnuncioService {

    @Autowired
    private EntityManager entityManager;
    @Autowired
    private AnuncioRepository anuncioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmailService emailService;

    @Transactional
    public AnuncioResponseDTO cadastrarAnuncio(AnuncioDTO dto, String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
       
        BigDecimal tokensPorVisualizacao;
        int bloqueioHoras;

        if (dto.getTempoVisualizacao() >= 45) {
            tokensPorVisualizacao = new BigDecimal("0.50");
            bloqueioHoras = 16;
        } 
        else if (dto.getTempoVisualizacao() >= 30) {
        tokensPorVisualizacao = new BigDecimal("0.40");
        bloqueioHoras = 18;
        }
        else if (dto.getTempoVisualizacao() >= 20) {
        tokensPorVisualizacao = new BigDecimal("0.30");
        bloqueioHoras = 20;
       } 
        else if (dto.getTempoVisualizacao() >= 10) {
            tokensPorVisualizacao = new BigDecimal("0.20");
            bloqueioHoras =24;
       } 
       else {
       tokensPorVisualizacao = new BigDecimal("0.15");
       bloqueioHoras =15;
         
       
       }
        
        // aqui desconta do saldo do usuário logado
        BigDecimal saldoAtual = Optional.ofNullable(usuario.getSaldoTokens()).orElse(BigDecimal.ZERO);
        BigDecimal tokensGastos = Optional.ofNullable(dto.getTokensGastos()).orElse(BigDecimal.ZERO);
        BigDecimal meusAnuncios = Optional.ofNullable(usuario.getMeusAnuncios()).orElse(BigDecimal.ZERO);
       
        
        if (saldoAtual.compareTo(tokensGastos) < 0) {
            throw new ResponseStatusException(HttpStatus.SC_BAD_REQUEST, "Saldo insuficiente", null);
        }

        
        Anuncios anuncio = new Anuncios();
        anuncio.setTitulo(dto.getTitulo());
        anuncio.setDescricao(dto.getDescricao());
        anuncio.setUrl(dto.getUrl());
        anuncio.setDataPublicacao(LocalDate.now());
        anuncio.setTempoVisualizacao(dto.getTempoVisualizacao());
        anuncio.setMaxVisualizacoes(dto.getMaxVisualizacoes());
        anuncio.setTokensPorVisualizacao(tokensPorVisualizacao);
        anuncio.setBloqueio_horas(bloqueioHoras);
        anuncio.setUsuario(usuario);

      
   
        // salvar anúncio
        usuario.setSaldoTokens(saldoAtual.subtract(tokensGastos));
        usuario.setMeusAnuncios(meusAnuncios.add(BigDecimal.ONE));
        usuarioRepository.save(usuario);
        anuncioRepository.save(anuncio);
        
        try {
            emailService.enviarConfirmacaoAnuncioHtml(
                usuario.getUsername(),
                usuario.getEmail(),
                anuncio.getTitulo(),
                anuncio.getDescricao(), 
                anuncio.getUrl()
            );	
        } catch (Exception e) {
            System.err.println("Erro ao enviar e-mail de confirmação de criação de anúncio: " + e.getMessage());
        }


        return new AnuncioResponseDTO(anuncio, tokensGastos, usuario.getSaldoTokens());
    }

    public BigDecimal getQuantidadeMeusAnuncios(String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.SC_NOT_FOUND, "Usuário não encontrado", null));
        return Optional.ofNullable(usuario.getMeusAnuncios()).orElse(BigDecimal.ZERO);
    }

//=========================================
  
    @Transactional
    public AnuncioEdicaoResponseDTO editarAnuncio(Long id, AnuncioEdicaoDTO dto, String username) {
        Anuncios anuncio = anuncioRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.SC_NOT_FOUND, "Anúncio não encontrado", null));

        if (!anuncio.getUsuario().getUsername().equals(username)) {
            throw new ResponseStatusException(HttpStatus.SC_FORBIDDEN, "Você não tem permissão para editar este anúncio", null);
        }

        anuncio.setTitulo(dto.getTitulo());
        anuncio.setDescricao(dto.getDescricao());
        anuncio.setUrl(dto.getUrl());

        anuncioRepository.save(anuncio);

        return new AnuncioEdicaoResponseDTO(
            anuncio.getId(),
            anuncio.getTitulo(),
            anuncio.getDescricao(),
            anuncio.getUrl(),
            "Anúncio editado com sucesso"
        );
    }

    
    //===========================================================================================

    @Transactional
    public List<AnuncioListagemDTO> listarMeusAnuncios(String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        List<Anuncios> anuncios = anuncioRepository.findByUsuario(usuario);

        entityManager.flush();
        entityManager.clear();

        return anuncios.stream()
            .map(AnuncioListagemDTO::new)
            .collect(Collectors.toList());
    }




}