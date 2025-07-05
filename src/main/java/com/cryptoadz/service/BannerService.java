package com.cryptoadz.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.cryptoadz.dto.BannerDTO;
import com.cryptoadz.dto.BannerResponseDTO;
import com.cryptoadz.model.Banner;
import com.cryptoadz.model.Usuario;
import com.cryptoadz.repository.BannerRepository;
import com.cryptoadz.repository.BannerVisualizacaoRepository;
import com.cryptoadz.repository.UsuarioRepository;

import jakarta.transaction.Transactional;

@Service
public class BannerService {

    @Autowired
    private BannerRepository bannerRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BannerVisualizacaoRepository bannerVisualizacaoRepository;
   
    @Transactional
    public BannerResponseDTO criarBannerComUsuario(BannerDTO dto, String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + username));


      
        BigDecimal tokensGastos = Optional.ofNullable(dto.getTokensGastos()).orElse(BigDecimal.ZERO);
     
        if (usuario.getSaldoTokens().compareTo(tokensGastos) < 0) {
            throw new ResponseStatusException(
                    HttpStatus.SC_BAD_REQUEST,  
                    "Saldo insuficiente", null);
        }
           
        // Desconta o custo do banner no saldo do usuário
        usuario.setSaldoTokens(usuario.getSaldoTokens().subtract(tokensGastos));
        usuarioRepository.save(usuario);

        // Cria o banner com os dados do DTO e associa ao usuário
        Banner banner = new Banner();
        banner.setUsuario(usuario);
        banner.setDataCriacao(LocalDateTime.now());
        banner.setAtivo(dto.getAtivo());
        banner.setTitulo(dto.getTitulo());
        banner.setUrlDestino(dto.getUrlDestino());
        banner.setImagemUrl(dto.getImagemUrl());
        banner.setDataExpiracao(dto.getDataExpiracao());
        banner.setTempoExibicao(dto.getTempoExibicao());
   

        bannerRepository.save(banner);

        // Retorna DTO com o banner criado, custo descontado e saldo atualizado
        return new BannerResponseDTO(banner, usuario.getSaldoTokens(), tokensGastos, username);
         
       
    }


    // ✅ Listar banners
    public List<Banner> listarTodos() {
        return bannerRepository.findAll();
    }

    public List<Banner> listarAtivos() {
        return bannerRepository.findByAtivoTrue();
    }

    public List<Banner> listarPorUsuario(Long usuarioId) {
        return bannerRepository.findByUsuarioId(usuarioId);
    }

    public Optional<Banner> buscarPorId(Long id) {
        return bannerRepository.findById(id);
    }

  

    // ✅ Deletar banner manual
    public void deletarBanner(Long id) {
        bannerRepository.deleteById(id);
    }

    // ✅ Incrementa clique e verifica se precisa desativar
    public void registrarClique(Long bannerId) {
        Banner banner = bannerRepository.findById(bannerId)
                .orElseThrow(() -> new RuntimeException("Banner não encontrado"));

        banner.setCliquesAtuais(banner.getCliquesAtuais() + 1);

        if (banner.getCliquesAtuais() >= banner.getTotalCliquesPermitidos()) {
            banner.setAtivo(false);
        }

        bannerRepository.save(banner);
    }
    public BannerService(BannerVisualizacaoRepository bannerVisualizacaoRepository, BannerRepository bannerRepository) {
        this.bannerVisualizacaoRepository = bannerVisualizacaoRepository;
        this.bannerRepository = bannerRepository;
    }

    @Scheduled(cron = "*/30 * * * * *")
    @Transactional
    public void verificarEBloquearBannersExpirados() {
        try {
            LocalDateTime agora = LocalDateTime.now();

            bannerVisualizacaoRepository.deleteByBannerDataExpiracaoBefore(agora);
            int deletados = bannerRepository.deleteExpiredBanners(agora);

            System.out.println("Verificação de banners expirada às " + agora);
            System.out.println(deletados + " banners expirados foram removidos.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}

