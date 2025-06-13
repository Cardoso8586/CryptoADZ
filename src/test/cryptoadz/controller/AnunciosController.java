package com.cryptoadz.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.cryptoadz.model.Anuncios;
import com.cryptoadz.service.AnuncioVisualizacaoService;
import com.cryptoadz.service.AnunciosService;

import java.security.Principal;

@Controller
public class AnunciosController {

    @Autowired
    private AnunciosService anunciosService;
    
    @Autowired
    private AnuncioVisualizacaoService anuncioVisualizacaoService;

    @GetMapping("/anuncios")
    public String listarAnuncios(Model model, Principal principal) {
        System.out.println("Requisição GET /anuncios recebida");
        if (principal == null) {
            System.out.println("Principal é null");
        } else {
            System.out.println("Usuário logado: " + principal.getName());
        }
        List<Anuncios> anunciosDisponiveis = anunciosService.buscarAnunciosDisponiveisParaUsuario(
            principal != null ? principal.getName() : "usuarioTeste");
        System.out.println("Anúncios encontrados: " + anunciosDisponiveis.size());

        model.addAttribute("anuncios", anunciosDisponiveis.stream().limit(10).toList());
        return "dashboard";
    }
//=======================================================================================================================================

    // Novo endpoint para tentar liberar o anúncio para visualização
    @GetMapping("/anuncios/{anuncioId}")
    public String mostrarAnuncio(@PathVariable Long anuncioId, Principal principal, Model model) {
        String username = principal.getName();

        // Verifica se está bloqueado
        if (anuncioVisualizacaoService.estaBloqueado(username, anuncioId)) {
            // Pode retornar uma view de aviso, ou redirecionar para outra página
            model.addAttribute("mensagem", "Você está temporariamente bloqueado para visualizar este anúncio. Por favor, aguarde.");
            return "anuncio-bloqueado";  // nome do template que criaremos para mostrar o bloqueio
        }

        // Se não estiver bloqueado, carrega o anúncio normalmente
        Anuncios anuncio = anunciosService.buscarPorId(anuncioId);
        model.addAttribute("anuncio", anuncio);
        return "pagina-anuncio";  // nome do template que exibe o anúncio normalmente
    }
    
  //========================================================================================================================================
    
    
    
    

}//<AnunciosController>

