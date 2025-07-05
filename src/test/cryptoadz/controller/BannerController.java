package com.cryptoadz.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cryptoadz.dto.BannerDTO;
import com.cryptoadz.dto.BannerResponseDTO;
import com.cryptoadz.model.Banner;
import com.cryptoadz.service.BannerService;

@RestController
@RequestMapping("/api/banners")
@CrossOrigin(origins = "*")
public class BannerController {

    @Autowired
    private BannerService bannerService;

    @Value("${upload.banner.directory}")
    private String uploadDir;

    @PostMapping("/banner")
    public ResponseEntity<BannerResponseDTO> criarBanner(@RequestBody BannerDTO dto, Principal principal) {
        try {
            // principal.getName() retorna o nome do usuário logado
            BannerResponseDTO resposta = bannerService.criarBannerComUsuario(dto, principal.getName());
            return ResponseEntity.ok(resposta);
        } catch (RuntimeException e) {
            // Retorna mensagem de erro e saldo atual (se possível)
            BannerResponseDTO erroResponse = new BannerResponseDTO(
                null, // banner
                dto.getTokensGastos(),
                null, // saldo atual desconhecido aqui, poderia tentar buscar
                "Erro ao criar banner: " + e.getMessage()
            );
            return ResponseEntity.badRequest().body(erroResponse);
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadBanner(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Arquivo vazio");
        }

        try {
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String filename = UUID.randomUUID() + "_" + StringUtils.cleanPath(file.getOriginalFilename());
            Path filepath = Paths.get(uploadDir, filename);

            Files.write(filepath, file.getBytes());

            String url = "/uploads/banners/" + filename;

            return ResponseEntity.ok(url);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Erro ao enviar arquivo: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Banner>> listarTodos() {
        return ResponseEntity.ok(bannerService.listarTodos());
    }

    @GetMapping("/ativos")
    public ResponseEntity<List<Banner>> listarAtivos() {
        return ResponseEntity.ok(bannerService.listarAtivos());
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Banner>> listarPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(bannerService.listarPorUsuario(usuarioId));
    }

   

   

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        bannerService.deletarBanner(id);
        return ResponseEntity.noContent().build();
    }
  
   
    
}
