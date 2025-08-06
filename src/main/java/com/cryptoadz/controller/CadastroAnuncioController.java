package com.cryptoadz.controller;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.cryptoadz.dto.AnuncioDTO;
import com.cryptoadz.dto.AnuncioEdicaoDTO;
import com.cryptoadz.dto.AnuncioEdicaoResponseDTO;
import com.cryptoadz.dto.AnuncioListagemDTO;
import com.cryptoadz.dto.AnuncioResponseDTO;
import com.cryptoadz.model.Anuncios;
import com.cryptoadz.model.Usuario;
import com.cryptoadz.repository.AnuncioVisualizacaoRepository;
import com.cryptoadz.repository.AnunciosRepository;
import com.cryptoadz.repository.UsuarioRepository;
import com.cryptoadz.service.CadastroAnuncioService;

import jakarta.validation.Valid;


@Validated
@RestController
@RequestMapping("/api/anuncio")
public class CadastroAnuncioController {
	
	
    // para quantidade de anuncios feitos.
	@Autowired
	private AnunciosRepository anuncioRepository;
	@Autowired
	private UsuarioRepository  usuarioRepository;
	
	
	//quantidades de visualizações, cliks
	@Autowired
	private AnuncioVisualizacaoRepository anuncioVisualizacaoRepository;

	  @Autowired
    private CadastroAnuncioService cadastroAnuncioService;

  
   
    @PostMapping("/cadastrar")
    public ResponseEntity<AnuncioResponseDTO> cadastrarAnuncio(
            @RequestBody AnuncioDTO dto,
            Principal principal) {

        String username = principal.getName();

        // Aqui você passa o username para o serviço
        AnuncioResponseDTO response = cadastroAnuncioService.cadastrarAnuncio(dto, username);

        return ResponseEntity.ok(response);
    }

   
  ///=====================================================================
    @GetMapping("/quantidade")
    public ResponseEntity<Map<String, Long>> getQuantidadeAnuncios() {
    	  long total = anuncioRepository.count();
        Map<String, Long> resposta = Map.of("total", total);
        return ResponseEntity.ok(resposta);
    }
    

    
    @GetMapping("/quantidade-cliks")
    public ResponseEntity<Map<String, Long>> getQuantidadeCliks() {
       Long total = anuncioVisualizacaoRepository.somarTodosTotalClicks();
       if (total == null) total = 0L;
       Map<String, Long> resposta = Map.of("total", total);
       return ResponseEntity.ok(resposta);
   }

   
    @GetMapping("/meus-anuncios")
    public ResponseEntity<Map<String, BigDecimal>> getQuantidadeDeAnuncios(Principal principal) {
        String username = principal.getName();
        Usuario usuario = usuarioRepository.findByUsername(username)
            .orElseThrow(() -> new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Usuário não encontrado"));
        
        BigDecimal quantidade = Optional.ofNullable(usuario.getMeusAnuncios()).orElse(BigDecimal.ZERO);
        
        return ResponseEntity.ok(Map.of("quantidade", quantidade));
    }

//==========================================================================================


  
    @GetMapping("/listar-anuncios")
    public ResponseEntity<List<AnuncioListagemDTO>> listarMeusAnuncios(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        List<AnuncioListagemDTO> anuncios = cadastroAnuncioService.listarMeusAnuncios(username);
        return ResponseEntity.ok(anuncios);
    }

    @PutMapping("/editar-anuncios/{id}")
    public ResponseEntity<AnuncioEdicaoResponseDTO> editarAnuncio(
            @PathVariable Long id,
            @RequestBody AnuncioEdicaoDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = userDetails.getUsername();
        AnuncioEdicaoResponseDTO response = cadastroAnuncioService.editarAnuncio(id, dto, username);
        return ResponseEntity.ok(response);
    }


}//<CadastroAnuncioController>




