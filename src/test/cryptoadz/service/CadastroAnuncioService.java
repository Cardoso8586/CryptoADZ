package com.cryptoadz.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.cryptoadz.dto.AnuncioDTO;
import com.cryptoadz.dto.AnuncioResponseDTO;
import com.cryptoadz.model.Anuncios;
import com.cryptoadz.model.Usuario;
import com.cryptoadz.repository.AnuncioRepository;
import com.cryptoadz.repository.UsuarioRepository;

import jakarta.transaction.Transactional;
@Service
@Transactional

public class CadastroAnuncioService {

    @Autowired
    private AnuncioRepository anuncioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

	

    @Transactional
    public AnuncioResponseDTO cadastrarAnuncio(AnuncioDTO dto, String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
       
        BigDecimal tokensPorVisualizacao;
        int bloqueioHoras;

        if (dto.getTempoVisualizacao() >= 45) {
            tokensPorVisualizacao = new BigDecimal("0.90");
            bloqueioHoras = 16;
        } 
        else if (dto.getTempoVisualizacao() >= 30) {
        tokensPorVisualizacao = new BigDecimal("0.65");
        bloqueioHoras = 22;
        }
        else if (dto.getTempoVisualizacao() >= 20) {
        tokensPorVisualizacao = new BigDecimal("0.50");
        bloqueioHoras = 26;
       } 
        else if (dto.getTempoVisualizacao() >= 10) {
            tokensPorVisualizacao = new BigDecimal("0.35");
            bloqueioHoras =30;
       } 
       else {
       tokensPorVisualizacao = new BigDecimal("0.20");
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
        
      
   
        // salvar anúncio
        usuario.setSaldoTokens(saldoAtual.subtract(tokensGastos));
        usuario.setMeusAnuncios(meusAnuncios.add(BigDecimal.ONE));
        usuarioRepository.save(usuario);
        anuncioRepository.save(anuncio);

        return new AnuncioResponseDTO(anuncio, tokensGastos, usuario.getSaldoTokens());
    }

    public BigDecimal getQuantidadeMeusAnuncios(String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.SC_NOT_FOUND, "Usuário não encontrado", null));
        return Optional.ofNullable(usuario.getMeusAnuncios()).orElse(BigDecimal.ZERO);
    }



}
