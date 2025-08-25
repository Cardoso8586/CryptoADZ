package com.cryptoadz.controller;

import java.math.BigDecimal;
import java.util.Base64;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.cryptoadz.dto.AtualizacaoNomeDTO;
import com.cryptoadz.model.TentativaCadastro;
import com.cryptoadz.model.Usuario;
import com.cryptoadz.repository.UsuarioRepository;
import com.cryptoadz.service.CaptchaService;
import com.cryptoadz.service.EmailService;
import com.cryptoadz.service.TentativaCadastroService;
import com.cryptoadz.service.UsuarioService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class UsuarioController {
	


	
	@Autowired
    private EmailService emailService;
	   
    @Autowired
	private CaptchaService captchaService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private TentativaCadastroService tentativaCadastroService;
    
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/cadastro")
    public String mostrarFormularioCadastro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "cadastro";
    }
    
    @GetMapping("/referidos")
    public String mostrarCadastro(@RequestParam(required = false) String ref, Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("ref", ref != null ? ref : "");  // garante que sempre tenha valor
        return "cadastro";
    }

/**
    @PostMapping("/cadastro")
    public String cadastrarUsuario(
            @ModelAttribute Usuario usuario,
            @RequestParam(name = "cf-turnstile-response") String captchaToken,
            @RequestParam(required = false) String ref,
            Model model) {

        // 1️⃣ Verifica CAPTCHA
        if (!captchaService.isValid(captchaToken)) {
            model.addAttribute("erro", "Falha na verificação do CAPTCHA.");
            return "cadastro";
        }

        if (ref != null && !ref.isBlank()) {
            try {
                String decoded = new String(Base64.getUrlDecoder().decode(ref));
                Long referrerId = Long.parseLong(decoded);

                if (usuarioRepository.existsById(referrerId)) {
                    usuario.setReferredBy(referrerId); // agora salva como Long
                    System.out.println("[Cadastro] Usuário referido por: " + referrerId);
                }
            } catch (Exception e) {
                System.out.println("[Cadastro] Erro ao processar ref: " + e.getMessage());
            }
        }

        // 3️⃣ Salva usuário
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        usuarioRepository.save(usuario); // aqui o campo referredBy será salvo corretamente

        // 4️⃣ Envia e-mail
        try {
            emailService.enviarEmailBoasVindas(usuario.getUsername(), usuario.getEmail());
        } catch (Exception e) {
            System.err.println("Erro ao enviar e-mail de boas-vindas: " + e.getMessage());
        }

        return "redirect:/cadastro?cadastroSucesso=true";
    }


   */ 
   //========================================= Cadastro controlado por IP   ===========================================================

    
    @PostMapping("/cadastro")
    public String cadastrarUsuario(
            @ModelAttribute Usuario usuario,
            @RequestParam(name = "cf-turnstile-response") String captchaToken,
            HttpServletRequest request,
            @RequestParam(required = false) String ref,
            Model model) {

        String ipUsuario = getClientIp(request);

        TentativaCadastro tentativa = tentativaCadastroService.getOuCriarTentativa(ipUsuario);

        if (tentativaCadastroService.isBloqueado(tentativa)) {
            model.addAttribute("erroIp", "Este IP está temporariamente bloqueado por excesso de tentativas de cadastrar mais de um Usuário.");
            return "cadastro";
        }

        if (!captchaService.isValid(captchaToken)) {
            tentativaCadastroService.registrarTentativa(tentativa);
            model.addAttribute("erroCaptcha", "Falha na verificação do CAPTCHA.");
            return "cadastro";
        }

        if (usuario.getUsername() == null || usuario.getUsername().isEmpty()) {
            tentativaCadastroService.registrarTentativa(tentativa);
            model.addAttribute("erroEmail", "O campo e-mail é obrigatório.");
            return "cadastro";
        }

        if (usuario.getSenha() == null || usuario.getSenha().isEmpty()) {
            tentativaCadastroService.registrarTentativa(tentativa);
            model.addAttribute("erroSenha", "O campo senha é obrigatório.");
            return "cadastro";
        }

        if (usuarioRepository.findByIpCadastro(ipUsuario).isPresent()) {
            tentativaCadastroService.registrarTentativa(tentativa);
            model.addAttribute("erroIp", "Já existe uma conta cadastrada com este IP.");
            return "cadastro";
        }

        
        // Recompensa quem indicou (se existir)
        if (ref != null && !ref.isBlank()) {
            try {
                String decoded = new String(Base64.getUrlDecoder().decode(ref));
                Long referrerId = Long.parseLong(decoded);

                if (usuarioRepository.existsById(referrerId)) {
                    usuario.setReferredBy(referrerId); // agora salva como Long
                    System.out.println("[Cadastro] Usuário referido por: " + referrerId);
                }
            } catch (Exception e) {
                System.out.println("[Cadastro] Erro ao processar ref: " + e.getMessage());
            }
        }
        
        // Limpa tentativas
        tentativaCadastroService.limparTentativas(tentativa);

        // Criptografa senha
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));

        // Salva IP
        usuario.setIpCadastro(ipUsuario);

   
        usuarioRepository.save(usuario);

     

        // Envia e-mail de boas-vindas
        try {
            emailService.enviarEmailBoasVindas(usuario.getUsername(), usuario.getEmail());
        } catch (Exception e) {
            System.err.println("Erro ao enviar e-mail de boas-vindas: " + e.getMessage());
        }

        return "redirect:/cadastro?cadastroSucesso=true";
    }

//========================================================================================================
    
    //Recupera o IP real do cliente, mesmo por trás de proxies.
    
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        } else {
            // Em casos de múltiplos IPs, pega o primeiro (real)
            ip = ip.split(",")[0];
        }
        return ip;
    }

     
    
    
    //==========================================================================================================
    
   
//==============================================================================================================
    @GetMapping("/usuarios/quantidade")
    public ResponseEntity<Map<String, Long>> getQuantidadeUsuarios() {
        long total = usuarioService.contarUsuarios();
      //  Map<String, Long> resposta = Map.of("total Users.", total);
        Map<String, Long> resposta = Map.of("total", total);

        return ResponseEntity.ok(resposta);
    }

//================================================= atualizarNome =========================================================
    
    @PostMapping("/atualizar-nome/{id}")
    public ResponseEntity<?> atualizarNome(@PathVariable Long id, @RequestBody AtualizacaoNomeDTO dto) {
        usuarioService.atualizarNome(id, dto.getNovoNome());
        return ResponseEntity.ok("Nome atualizado com sucesso");
      
    }

 //=============================================== atuslizar senha ===================================================

    

    
}



