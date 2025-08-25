package com.cryptoadz.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cryptoadz.model.Anuncios;
import com.cryptoadz.model.Usuario;
import com.cryptoadz.repository.AnuncioRepository;
import com.cryptoadz.repository.AnuncioVisualizacaoRepository;
import com.cryptoadz.repository.UsuarioRepository;

@Controller
public class DashBoardController {

    private final UsuarioRepository usuarioRepository;
    private final AnuncioRepository anuncioRepository;
    public DashBoardController(UsuarioRepository usuarioRepository, AnuncioRepository anuncioRepository,
                              AnuncioVisualizacaoRepository anuncioVisualizacaoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.anuncioRepository = anuncioRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "15") int size,
                            Authentication authentication, Model model) {

        String username = authentication.getName();
        Usuario usuario = usuarioRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("Usuário '" + username + "' não encontrado"));

        model.addAttribute("usuario", usuario);
        model.addAttribute("saldoTokens", usuario.getSaldoTokens());
        model.addAttribute("saldoUsdt", usuario.getUsdtSaldo());
        model.addAttribute("dataCadastroFormatada", usuario.getCreatedAt()
        	    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        model.addAttribute("senha-usuario", usuario.getSenha());
        model.addAttribute("email-usuario", usuario.getEmail());
       
       
      
        BigDecimal ganhos = usuario.getGanhosPendentesReferral() != null ? usuario.getGanhosPendentesReferral() : BigDecimal.ZERO;
        ganhos = ganhos.setScale(4, RoundingMode.HALF_UP);
        model.addAttribute("ganhosPendentesReferral", ganhos);

        
        // Constrói link de convite
        String linkReferencia = "https://cryptoadz.up.railway.app/referidos?ref=" + Base64.getUrlEncoder().encodeToString(usuario.getId().toString().getBytes());
   //--------------------------------------------------------------------------------------------------------------------------------------------------------------
       // String linkReferencia = "http://localhost:8080/referidos?ref=" + Base64.getUrlEncoder().encodeToString(usuario.getId().toString().getBytes());
        model.addAttribute("linkReferencia", linkReferencia);

        
        List<Usuario> indicados = usuarioRepository.findByReferredBy(usuario.getId());
        model.addAttribute("indicados", indicados);
        model.addAttribute("qtdIndicados", indicados.size());
        // Buscar indicados
        int qtdIndicados = indicados.size();

     // Calcular nível
        String nivel;
        if (qtdIndicados <= 5) {
            nivel = "Iniciante";
        } else if (qtdIndicados <= 15) {
            nivel = "Júnior";
        } else if (qtdIndicados <= 30) {
            nivel = "Pleno";
        } else if (qtdIndicados <= 50) {
            nivel = "Sênior";
        } else if (qtdIndicados <= 80) {
            nivel = "Gestor";
        } else if (qtdIndicados <= 120) {
            nivel = "Executivo";
        } else if (qtdIndicados <= 200) {
            nivel = "Diretor";
        } else if (qtdIndicados <= 500) {
            nivel = "Presidente";
        } else {
            nivel = "Lendário";
        }

        // Atualizar nível do usuário
        usuario.setNivel(nivel);
        usuarioRepository.save(usuario);

       

        // Passar dados para a view
        model.addAttribute("usuario", usuario);
        model.addAttribute("indicados", indicados);
        model.addAttribute("qtdIndicados", qtdIndicados);
        model.addAttribute("nivel", nivel);


        if (page < 0) {
            page = 0;
        }

        Page<Anuncios> anunciosPage = anuncioRepository.findByMaxVisualizacoesGreaterThan(0, PageRequest.of(page, size));

        if (page >= anunciosPage.getTotalPages() && anunciosPage.getTotalPages() > 0) {
            page = anunciosPage.getTotalPages() - 1;
            anunciosPage = anuncioRepository.findByMaxVisualizacoesGreaterThan(0, PageRequest.of(page, size));
        }

        model.addAttribute("anuncios", anunciosPage.getContent());
        model.addAttribute("paginaAtual", page);
        model.addAttribute("totalPaginas", anunciosPage.getTotalPages());
        model.addAttribute("tamanhoPagina", size);

        int paginaAnterior = page > 0 ? page - 1 : 0;
        int paginaProxima = page < anunciosPage.getTotalPages() - 1 ? page + 1 : anunciosPage.getTotalPages() - 1;

        model.addAttribute("paginaAnterior", paginaAnterior);
        model.addAttribute("paginaProxima", paginaProxima);

        int hora = LocalTime.now().getHour();
        String saudacao = hora < 12 ? "Bom dia" : (hora < 18 ? "Boa tarde" : "Boa noite");
        model.addAttribute("saudacao", saudacao);
       

        
        
       
        for (Anuncios anuncio : anunciosPage.getContent()) {
            System.out.println("Anuncio ID: " + anuncio.getId() + " | tokensPorVisualizacao: " + anuncio.getTokensPorVisualizacao());
        }
        
        
//==================================================================================================================
        

        return "dashboard";
    }
   
    
    
    
   
}


//========================================funcionando pelas metades======================================


