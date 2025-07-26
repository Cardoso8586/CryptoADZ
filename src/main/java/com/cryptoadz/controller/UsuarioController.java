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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.cryptoadz.dto.AtualizacaoNomeDTO;
import com.cryptoadz.dto.AtualizacaoSenhaDTO;
import com.cryptoadz.model.TentativaCadastro;
import com.cryptoadz.model.Usuario;
import com.cryptoadz.model.UsuarioDetails;
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

   //============================================================================
    
   
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
        
          // Envia e-mail de boas-vindas
        try {
            emailService.enviarEmailBoasVindas(usuario.getUsername(), usuario.getEmail());
        } catch (Exception e) {
            System.err.println("Erro ao enviar e-mail de boas-vindas: " + e.getMessage());
        }

        
        return "redirect:/cadastro?cadastroSucesso=true";

        //return "redirect:/login";
    }
   
   
    
   //========================================= Cadastro controlado por IP   ===========================================================
    
  
  /**  @PostMapping("/cadastro")
   public String cadastrarUsuario(
            @ModelAttribute Usuario usuario,
            @RequestParam(name = "cf-turnstile-response") String captchaToken,
            HttpServletRequest request,
            Model model
    ) {
        String ipUsuario = getClientIp(request);

        // Recupera ou cria registro de tentativas
        TentativaCadastro tentativa = tentativaCadastroService.getOuCriarTentativa(ipUsuario);

        // Verifica se o IP está temporariamente bloqueado
        if (tentativaCadastroService.isBloqueado(tentativa)) {
            model.addAttribute("erroIp", "Este IP está temporariamente bloqueado por excesso de tentativas de cdastrar mais de um Usuário.");
            return "cadastro";
        }

        // Verifica se o CAPTCHA foi resolvido corretamente
        if (!captchaService.isValid(captchaToken)) {
            tentativaCadastroService.registrarTentativa(tentativa);
            model.addAttribute("erroCaptcha", "Falha na verificação do CAPTCHA.");
            return "cadastro";
        }

        // Validação de e-mail/usuário
        if (usuario.getUsername() == null || usuario.getUsername().isEmpty()) {
            tentativaCadastroService.registrarTentativa(tentativa);
            model.addAttribute("erroEmail", "O campo e-mail é obrigatório.");
            return "cadastro";
        }

        // Validação de senha
        if (usuario.getSenha() == null || usuario.getSenha().isEmpty()) {
            tentativaCadastroService.registrarTentativa(tentativa);
            model.addAttribute("erroSenha", "O campo senha é obrigatório.");
            return "cadastro";
        }

        // Verifica se já existe uma conta com esse IP
        if (usuarioRepository.findByIpCadastro(ipUsuario).isPresent()) {
            tentativaCadastroService.registrarTentativa(tentativa);
            model.addAttribute("erroIp", "Já existe uma conta cadastrada com este IP.");
            return "cadastro";
        }

        // Cadastro bem-sucedido
        tentativaCadastroService.limparTentativas(tentativa);
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        usuario.setIpCadastro(ipUsuario);
        usuarioRepository.save(usuario);
        
     
      
         // Envia e-mail de boas-vindas
        try {
            emailService.enviarEmailBoasVindas(usuario.getUsername(), usuario.getEmail());
        } catch (Exception e) {
            System.err.println("Erro ao enviar e-mail de boas-vindas: " + e.getMessage());
        }


        return "redirect:/cadastro?cadastroSucesso=true";
        //return "redirect:/login";
   
    }
    */
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
    
    @PostMapping("/atualizar-senha/{id}")
    public ResponseEntity<?> atualizarSenha(@PathVariable Long id, @RequestBody AtualizacaoSenhaDTO dto) {
        usuarioService.atualizarSenha(id, dto.getNovaSenha());
        return ResponseEntity.ok("Senha atualizada com sucesso!");
    }

    
    
}



