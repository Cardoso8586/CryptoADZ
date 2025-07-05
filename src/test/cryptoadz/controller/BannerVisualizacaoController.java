package com.cryptoadz.controller;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cryptoadz.dto.BannerVisualizacaoStatusDTO;
import com.cryptoadz.model.Banner;
import com.cryptoadz.model.Usuario;
import com.cryptoadz.repository.BannerRepository;
import com.cryptoadz.repository.UsuarioRepository;
import com.cryptoadz.service.BannerVisualizacaoService;

@RestController
@RequestMapping("/api/visualizacoes/banner")
public class BannerVisualizacaoController {

    @Autowired
    private BannerVisualizacaoService service;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping
    public ResponseEntity<?> registrarVisualizacao(@RequestParam Long bannerId, @RequestParam Long usuarioId) {
        try {
            BannerVisualizacaoStatusDTO dto = service.registrarVisualizacao(bannerId, usuarioId);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.SC_BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).body("Erro inesperado");
        }
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> registrarVisualizacaoComLimite(
            @RequestParam Long bannerId,
            @RequestParam Long usuarioId) {

        if  (!service.podeRegistrarVisualizacao(bannerId, usuarioId, 0)) {  // só precisa do usuário pra verificar limite
            return ResponseEntity.status(HttpStatus.SC_TOO_MANY_REQUESTS)
                    .body("Limite diário de visualizações atingido.");
        }

        try {
            BannerVisualizacaoStatusDTO visualizacao = service.registrarVisualizacao(bannerId, usuarioId);
            return ResponseEntity.ok(visualizacao);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.SC_BAD_REQUEST).body(e.getMessage());
        }
    }



    
    @PostMapping("/coletar")
    public ResponseEntity<?> coletarRecompensa(@RequestParam Long usuarioId) {
        try {
            BannerVisualizacaoStatusDTO status = service.coletarRecompensa(usuarioId);
            return ResponseEntity.ok(status);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        
    

}
  
    @GetMapping("/status")
    public ResponseEntity<?> getStatusMissao(@RequestParam Long usuarioId)
    {
            Usuario usuario = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            BannerVisualizacaoStatusDTO dto = new BannerVisualizacaoStatusDTO();
            dto.setBannersVistos(usuario.getBannersVistos());
            dto.setLimitePorDia(15); // ou use uma constante: LIMITE_MISSAO
            dto.setTokensAtualizados(usuario.getSaldoTokens());

            return ResponseEntity.ok(dto);
        }     
          
    
}


//=======================================================================  FUNCIONANDO==========================================================================