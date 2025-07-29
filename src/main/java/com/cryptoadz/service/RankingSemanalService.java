package com.cryptoadz.service;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cryptoadz.dto.UsuarioRankingDTO;
import com.cryptoadz.model.Usuario;
import com.cryptoadz.repository.UsuarioRepository;

@Service
public class RankingSemanalService {

    private final UsuarioRepository usuarioRepo;

    @Autowired
    private EmailService emailService;
    
    public RankingSemanalService(UsuarioRepository usuarioRepo) {
        this.usuarioRepo = usuarioRepo;
    }

    @Transactional
    public List<PremioUsuario> distribuirPremios() {
        BigDecimal premioPrimeiroLugar = BigDecimal.valueOf(100);
        BigDecimal premioSegundoLugar = BigDecimal.valueOf(50);
        BigDecimal premioTerceiroLugar = BigDecimal.valueOf(25);

        List<Usuario> top3 = usuarioRepo.findTop3ByOrderByQuantidadeVisualizacaoSemanalDesc();
        List<PremioUsuario> premiados = new ArrayList<>();

        for (int i = 0; i < top3.size(); i++) {
            Usuario u = top3.get(i);
            BigDecimal premio = BigDecimal.ZERO;
            switch (i) {
                case 0 -> premio = premioPrimeiroLugar;
                case 1 -> premio = premioSegundoLugar;
                case 2 -> premio = premioTerceiroLugar;
            }
           
            String receberPremio = "";
            if (premio.compareTo(new BigDecimal("100")) >= 0) {
            	receberPremio = "🎉 Parabéns! Você ficou em 1º lugar!";
            } else if (premio.compareTo(new BigDecimal("50")) >= 0) {
            	receberPremio = "🥈 Excelente! Você ficou em 2º lugar!";
            } else if (premio.compareTo(new BigDecimal("25")) >= 0) {
            	receberPremio = "🥉 Muito bom! Você ficou em 3º lugar!";
            } else {
            	receberPremio = "🎁 Você recebeu um prêmio especial!";
            }

            // Envia e-mail de confirmação
            try {
                emailService.enviarConfirmacaoPremio(u.getUsername(), u.getEmail(), premio, receberPremio);
            } catch (Exception e) {
                System.err.println("Erro ao enviar e-mail de confirmação de depósito: " + e.getMessage());
            }// Zera pendente
            // Em vez de creditar direto no saldo, guardar como pendente
            u.setPremioPendente(premio); 
            usuarioRepo.save(u);

            premiados.add(new PremioUsuario(u.getId(), u.getUsername(), premio));
        }

        return premiados;
    }

    
    //==================================================== confirmarPremio =============================================================
    @Transactional
    public boolean confirmarPremio(Long usuarioId) {
        return usuarioRepo.findById(usuarioId).map(u -> {
            BigDecimal pendente = u.getPremioPendente();
            if (pendente.compareTo(BigDecimal.ZERO) > 0) {
                u.setSaldoTokens(u.getSaldoTokens().add(pendente)); // Credita
                u.setPremioPendente(BigDecimal.ZERO); 
                
              
                usuarioRepo.save(u);
                return true;
            }
            return false;
        }).orElse(false);
    }

    // Para testes: roda a cada 1 minuto
   //@Scheduled(fixedRate = 360000)  // 60000 ms = 1 minuto
    @Scheduled(cron = "0 0 0 * * SUN", zone = "America/Sao_Paulo")
    public void distribuirPremiosAgendado() {
        distribuirPremios();
        resetarVisualizacoesSemanais();
    }

    public void distribuirPremiosAgendadoTeste() {
        distribuirPremios();
        resetarVisualizacoesSemanais();
    }

    //==================================== resetarVisualizacoesSemanais ============================================
    @Transactional
    public void resetarVisualizacoesSemanais() {
        distribuirPremios();  // Distribui os prêmios antes de zerar as visualizações semanais

        List<Usuario> usuarios = usuarioRepo.findAll();
        for (Usuario u : usuarios) {
            u.setQuantidadeVisualizacaoSemanal(0);
        }
        usuarioRepo.saveAll(usuarios);
    }

    public List<Usuario> getRankingSemanalTop(int top) {
        return usuarioRepo.findTopNByOrderByQuantidadeVisualizacaoSemanalDesc(top);
    }
    
   //========================================= getMinhaPosicao ==============================================
    public UsuarioRankingDTO getMinhaPosicao(Long usuarioId) {
        List<Usuario> usuarios = usuarioRepo.findAllByOrderByQuantidadeVisualizacaoSemanalDesc();

        for (int i = 0; i < usuarios.size(); i++) {
            Usuario u = usuarios.get(i);
            if (u.getId().equals(usuarioId)) {
                return new UsuarioRankingDTO(
                    u.getId(),
                    u.getUsername(),
                    u.getQuantidadeVisualizacaoSemanal(),
                    i + 1
                );
            }
        }

        return null; // ou lançar exceção se preferir
    }
    
    
//=========================================================================================
   

    // Classe auxiliar para retorno do prêmio distribuído
    public static class PremioUsuario {
        private Long usuarioId;
        private String nomeUsuario;
        private BigDecimal premio;

        public PremioUsuario(Long usuarioId, String nomeUsuario, BigDecimal premio) {
            this.usuarioId = usuarioId;
            this.nomeUsuario = nomeUsuario;
            this.premio = premio;
        }
        private BigDecimal premioPendente;

     // Getter
     public BigDecimal getPremioPendente() {
         return premioPendente;
     }

     // Setter
     public void setPremioPendente(BigDecimal premioPendente) {
         this.premioPendente = premioPendente;
     }

        // getters e setters
        public Long getUsuarioId() {
            return usuarioId;
        }

        public void setUsuarioId(Long usuarioId) {
            this.usuarioId = usuarioId;
        }

        public String getNomeUsuario() {
            return nomeUsuario;
        }

        public void setNomeUsuario(String nomeUsuario) {
            this.nomeUsuario = nomeUsuario;
        }

        public BigDecimal getPremio() {
            return premio;
        }

        public void setPremio(BigDecimal premio) {
            this.premio = premio;
        }
    }
    public BigDecimal getPremioPendente(Long id) {
        return usuarioRepo.findById(id)
            .map(Usuario::getPremioPendente) // Busca o campo premioPendente da entidade
            .orElse(BigDecimal.ZERO);        // Se não encontrar o usuário, retorna 0
    }





  
}

