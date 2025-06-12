package com.cryptoadz.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.cryptoadz.model.Usuario;
import com.cryptoadz.model.UsuarioDetails;
import com.cryptoadz.repository.UsuarioRepository;
import com.cryptoadz.service.CaptchaService;
import com.cryptoadz.service.UsuarioService;

@Controller
public class UsuarioController {

	 @Autowired
	    private CaptchaService captchaService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/cadastro")
    public String mostrarFormularioCadastro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "cadastro";
    }

   
    @PostMapping("/cadastro")
    public String cadastrarUsuario(
            @ModelAttribute Usuario usuario,
            @RequestParam(name = "cf-turnstile-response") String captchaToken,
            Model model
    ) {
        if (!captchaService.isValid(captchaToken)) {
            model.addAttribute("erro", "Falha na verificação do CAPTCHA.");
            return "cadastro";
        }

        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        usuarioRepository.save(usuario);
        return "redirect:/login";
    }
    
    // ✅ Mova este método para o controller, não dentro do service
    @PostMapping("/atualizar-username")
    public ResponseEntity<?> atualizarUsername(@RequestBody Map<String, String> body) {
        String novoUsername = body.get("username");

        if (novoUsername == null || novoUsername.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("erro", "Nome de usuário inválido."));
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof UsuarioDetails)) {
            return ResponseEntity.status(401).body(Map.of("erro", "Usuário não autenticado."));
        }

        UsuarioDetails usuarioDetails = (UsuarioDetails) authentication.getPrincipal();

        Usuario usuario = usuarioRepository.findByEmail(usuarioDetails.getUsername());
        if (usuario == null) {
            return ResponseEntity.status(404).body(Map.of("erro", "Usuário não encontrado."));
        }

        usuario.setUsername(novoUsername);
        usuarioRepository.save(usuario);

        UsuarioDetails novoUsuarioDetails = new UsuarioDetails(usuario);

        Authentication novaAuth = new UsernamePasswordAuthenticationToken(
                novoUsuarioDetails,
                authentication.getCredentials(),
                novoUsuarioDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(novaAuth);

        return ResponseEntity.ok(Map.of("mensagem", "Nome de usuário atualizado com sucesso!"));
    }
//==============================================================================================================
    @GetMapping("/usuarios/quantidade")
    public ResponseEntity<Map<String, Long>> getQuantidadeUsuarios() {
        long total = usuarioService.contarUsuarios();
      //  Map<String, Long> resposta = Map.of("total Users.", total);
        Map<String, Long> resposta = Map.of("total", total);

        return ResponseEntity.ok(resposta);
    }



}



