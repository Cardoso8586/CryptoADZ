package com.cryptoadz.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Random;

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

  //  private static int LIMITE_MISSAO ;
    
    private static  BigDecimal RECOMPENSA_MISSAO = BigDecimal.valueOf(10.50);

    
    
    public BannerVisualizacaoStatusDTO registrarVisualizacao(Long bannerId, Long usuarioId) {
        log.info("Iniciando registro de visualização: banner={} usuário={}", bannerId, usuarioId);

        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        Banner banner = bannerRepository.findById(bannerId)
            .orElseThrow(() -> new RuntimeException("Banner não encontrado"));

        LocalDate hoje = LocalDate.now();
        LocalDate dataUltimaVisualizacao = usuario.getDataUltimaVisualizacao();
        LocalDate dataUltimaColeta = usuario.getDataUltimaColeta();

        // Reset diário APENAS se for novo dia E já tiver coletado ou nunca tiver coletado
        if ((dataUltimaVisualizacao == null || !dataUltimaVisualizacao.isEqual(hoje)) &&
            (dataUltimaColeta == null || !dataUltimaColeta.isEqual(hoje))) {
            
            log.info("Reset diário válido: novo dia e recompensa anterior coletada.");
            usuario.setBannersVistos(0);
            usuario.setDataUltimaVisualizacao(hoje);
        }
        int limiteMissao = getLimiteMissaoParaHoje();

        // verifica limite diário
        if (usuario.getBannersVistos() >= limiteMissao) {
            log.warn("Usuário {} atingiu limite diário de {}", usuarioId, limiteMissao);
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
        if (vistos == limiteMissao) {
            BigDecimal antigo = usuario.getSaldoTokens();
            usuario.setSaldoTokens(antigo.add(getRECOMPENSA_MISSAO()));

            log.info("Missão diária completada por {}: +{} tokens ({} → {})",
                     usuarioId, getRECOMPENSA_MISSAO(), antigo, usuario.getSaldoTokens());
        }

        usuarioRepository.save(usuario);

        // monta DTO de retorno
        BannerVisualizacaoStatusDTO dto = new BannerVisualizacaoStatusDTO();
        dto.setBannersVistos(vistos);
        dto.setLimitePorDia(limiteMissao);
        dto.setTokensAtualizados(usuario.getSaldoTokens());
        dto.setLimitePorBanner(calcularLimitePorBanner());
        dto.setRecompensaMissao(getRECOMPENSA_MISSAO());
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
        int limiteMissao = getLimiteMissaoParaHoje();
        BannerVisualizacaoStatusDTO dto = new BannerVisualizacaoStatusDTO();
        dto.setBannersVistos(usuario.getBannersVistos());
        dto.setLimitePorDia(limiteMissao);
        dto.setTokensAtualizados(usuario.getSaldoTokens());
        dto.setRecompensaMissao(getRECOMPENSA_MISSAO());
        
        return dto;
    }

    public BannerVisualizacaoStatusDTO coletarRecompensa(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        LocalDate hoje = LocalDate.now();

        log.info("Usuário {} solicitou recompensa da missão", usuarioId);

        int bannersVistos = usuario.getBannersVistos();
        int limiteMissao = getLimiteMissaoParaHoje();
        if (bannersVistos < limiteMissao) {
            log.warn("Usuário {} tentou coletar recompensa sem completar missão (banners vistos: {})", usuarioId, bannersVistos);
            throw new RuntimeException("Missão não completada ainda. Continue assistindo banners.");
        }

        // Verifica se já coletou recompensa hoje
        if (usuario.getDataUltimaColeta() != null && usuario.getDataUltimaColeta().isEqual(hoje)) {
            log.warn("Usuário {} já coletou recompensa hoje ({})", usuarioId, hoje);
            throw new RuntimeException("Recompensa já coletada hoje. Volte amanhã!");
        }

        // Paga recompensa
        usuario.setSaldoTokens(usuario.getSaldoTokens().add(getRECOMPENSA_MISSAO()));

        usuario.setDataUltimaColeta(hoje);

        // ✅ Zera o contador
        usuario.setBannersVistos(0);
        usuario.setDataUltimaVisualizacao(hoje); // Reinicia também a data de visualização se quiser resetar

        usuarioRepository.save(usuario);

        log.info("Recompensa entregue: usuário {} recebeu {} tokens. Novo saldo: {}",
                usuarioId, getRECOMPENSA_MISSAO(), usuario.getSaldoTokens());
     
        BannerVisualizacaoStatusDTO dto = new BannerVisualizacaoStatusDTO();
        dto.setLimitePorDia(limiteMissao);
        dto.setBannersVistos(0); // Já que zerou
        dto.setTokensAtualizados(usuario.getSaldoTokens());
        dto.setRecompensaMissao(getRECOMPENSA_MISSAO());

        return dto;
    }
    
    
    public int getLimiteMissaoParaHoje() {
        LocalDate hoje = LocalDate.now();
        
        // Usa a data para criar uma seed (ex: ano * 10000 + mês * 100 + dia)
        int seed = hoje.getYear() * 10000 + hoje.getMonthValue() * 100 + hoje.getDayOfMonth();

        Random random = new Random(seed);

        // Define o intervalo de limite possível, por ex, entre 10 e 20
        int limiteMin = 15;
        int limiteMax = 15;

        int limiteHoje = limiteMin + random.nextInt(limiteMax - limiteMin + 1);
        log.info("Limite missão para {}: {}", hoje, limiteHoje); // ADICIONE ISSO
        return limiteHoje;
    }

    public int calcularLimitePorBanner() {
        int bannersAtivos = bannerRepository.countBannersAtivos();

        int limiteHoje = getLimiteMissaoParaHoje();

        if (bannersAtivos == 0) return limiteHoje;

        int resultado = limiteHoje / bannersAtivos + 2;

        if (resultado < 1) {
            return limiteHoje;
        }

        return resultado;
    }

	public BigDecimal getRECOMPENSA_MISSAO() {
		return RECOMPENSA_MISSAO;
	}

	public static void setRECOMPENSA_MISSAO(BigDecimal rECOMPENSA_MISSAO) {
		RECOMPENSA_MISSAO = rECOMPENSA_MISSAO;
	}

    
  /**  public int calcularLimitePorBanner() {
        int bannersAtivos = bannerRepository.countBannersAtivos();

        if (bannersAtivos <= 0) return LIMITE_MISSAO;

        int resultado = LIMITE_MISSAO / bannersAtivos;

        return (resultado < 1) ? LIMITE_MISSAO : resultado + 1;
    }*/




    
    
}

