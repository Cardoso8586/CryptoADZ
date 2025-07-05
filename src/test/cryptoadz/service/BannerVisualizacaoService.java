package com.cryptoadz.service;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cryptoadz.dto.BannerVisualizacaoStatusDTO;
import com.cryptoadz.model.Banner;
import com.cryptoadz.model.BannerVisualizacao;
import com.cryptoadz.model.Usuario;
import com.cryptoadz.repository.BannerRepository;
import com.cryptoadz.repository.BannerVisualizacaoRepository;
import com.cryptoadz.repository.UsuarioRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class BannerVisualizacaoService {

    private static final Logger log = LoggerFactory.getLogger(BannerVisualizacaoService.class);

    @Autowired
    private BannerVisualizacaoRepository repository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BannerRepository bannerRepository;

    private static final int LIMITE_MISSAO = 15;
    private static final int RECOMPENSA_MISSAO = 5;
    
    
    public BannerVisualizacaoStatusDTO registrarVisualizacao(Long bannerId, Long usuarioId) {
        log.info("Iniciando registro de visualização: banner={} usuário={}", bannerId, usuarioId);

        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        Banner banner = bannerRepository.findById(bannerId)
            .orElseThrow(() -> new RuntimeException("Banner não encontrado"));

        LocalDate hoje = LocalDate.now();
        // reset diário, se necessário
        if (usuario.getDataUltimaVisualizacao() == null
            || !usuario.getDataUltimaVisualizacao().isEqual(hoje)) {
            log.info("Reset diário de bannersVistos para usuário {}", usuarioId);
            usuario.setBannersVistos(0);
            usuario.setDataUltimaVisualizacao(hoje);
        }

        // verifica limite diário
        if (usuario.getBannersVistos() >= LIMITE_MISSAO) {
            log.warn("Usuário {} atingiu limite diário de {}", usuarioId, LIMITE_MISSAO);
            throw new RuntimeException("Limite diário de visualizações atingido. Volte amanhã!");
        }

        // salva visualização
        BannerVisualizacao visualizacao = new BannerVisualizacao(banner, usuario);
        repository.save(visualizacao);

        // incrementa contador
        int vistos = usuario.getBannersVistos() + 1;
        usuario.setBannersVistos(vistos);
        usuario.setDataUltimaVisualizacao(hoje);

        // recompensa se bateu missão
        if (vistos == LIMITE_MISSAO) {
            BigDecimal antigo = usuario.getSaldoTokens();
            usuario.setSaldoTokens(antigo.add(BigDecimal.valueOf(RECOMPENSA_MISSAO)));
            log.info("Missão diária completada por {}: +{} tokens ({} → {})",
                     usuarioId, RECOMPENSA_MISSAO, antigo, usuario.getSaldoTokens());
        }

        usuarioRepository.save(usuario);

        // monta DTO de retorno
        BannerVisualizacaoStatusDTO dto = new BannerVisualizacaoStatusDTO();
        dto.setBannersVistos(vistos);
        dto.setLimitePorDia(LIMITE_MISSAO);
        dto.setTokensAtualizados(usuario.getSaldoTokens());
        return dto;
    }

    public boolean podeRegistrarVisualizacao(Long bannerId, Long usuarioId, int limitePorDiaIgnorado) {
        long totalHoje = contarVisualizacoesHoje(bannerId, usuarioId);
        int limitePorBanner = calcularLimitePorBanner();

        log.info("Verificando permissão: usuário {} viu o banner {} vezes hoje (limite individual: {})",
                usuarioId, totalHoje, limitePorBanner);

        
        
        return totalHoje < limitePorBanner;
    }

    public long contarVisualizacoesHoje(Long bannerId, Long usuarioId) {
        LocalDate hoje = LocalDate.now();
        return repository.countVisualizacoesPorDia(usuarioId, bannerId, hoje);
    }

    public BannerVisualizacaoStatusDTO getStatusMissao(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        log.info("Consultando status da missão para usuário {}", usuarioId);

        BannerVisualizacaoStatusDTO dto = new BannerVisualizacaoStatusDTO();
        dto.setBannersVistos(usuario.getBannersVistos());
        dto.setLimitePorDia(LIMITE_MISSAO);
        dto.setTokensAtualizados(usuario.getSaldoTokens());

        return dto;
    }

    public BannerVisualizacaoStatusDTO coletarRecompensa(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        LocalDate hoje = LocalDate.now();

        log.info("Usuário {} solicitou recompensa da missão", usuarioId);

        int bannersVistos = usuario.getBannersVistos();

        BannerVisualizacaoStatusDTO dto = new BannerVisualizacaoStatusDTO();
        dto.setLimitePorDia(LIMITE_MISSAO);
        dto.setBannersVistos(bannersVistos);
        dto.setTokensAtualizados(usuario.getSaldoTokens());

        if (bannersVistos < LIMITE_MISSAO) {
            log.warn("Usuário {} tentou coletar recompensa sem completar missão (banners vistos: {})", usuarioId, bannersVistos);
            throw new RuntimeException("Missão não completada ainda. Continue assistindo banners.");
        }

        // Verifica se já coletou recompensa hoje
        if (usuario.getDataUltimaColeta() != null && usuario.getDataUltimaColeta().isEqual(hoje)) {
            log.warn("Usuário {} já coletou recompensa hoje ({})", usuarioId, hoje);
            throw new RuntimeException("Recompensa já coletada hoje. Volte amanhã!");
        }

        // Paga recompensa e atualiza data da última coleta
        usuario.setSaldoTokens(usuario.getSaldoTokens().add(BigDecimal.valueOf(RECOMPENSA_MISSAO)));
        usuario.setDataUltimaColeta(hoje);
        usuarioRepository.save(usuario);

        log.info("Recompensa entregue: usuário {} recebeu {} tokens. Novo saldo: {}",
                usuarioId, RECOMPENSA_MISSAO, usuario.getSaldoTokens());

        dto.setTokensAtualizados(usuario.getSaldoTokens());

        return dto;
    }

    private int calcularLimitePorBanner() {
        int bannersAtivos = bannerRepository.countBannersAtivos();
        if (bannersAtivos == 0) return LIMITE_MISSAO;
        return LIMITE_MISSAO / bannersAtivos + 1;
        


    }

    
    
}

