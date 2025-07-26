package com.cryptoadz.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.cryptoadz.model.Usuario;
import com.cryptoadz.repository.UsuarioRepository;

@Service
public class UsuarioService {
	
	   private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final UsuarioRepository usuarioRepository;

    @Autowired
    private EmailService emailService;
    
    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Usuario salvar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }
    
    public long contarUsuarios() {
        return usuarioRepository.count();
    }
    
 ///================================= atualizarNome =====================================================
    public void atualizarNome(Long id, String novoNome) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        String nomeAntigo = usuario.getUsername();
        usuario.setUsername(novoNome);
        usuarioRepository.save(usuario);

        try {
            // Envia o e-mail informando a alteração do nome
            emailService.enviarAvisoDeAlteracaoDeNome(novoNome, usuario.getEmail(), nomeAntigo);
        } catch (Exception e) {
            System.err.println("Erro ao enviar e-mail de alteração de nome: " + e.getMessage());
        }
    }


    //==============================  atualizarSenha ==========================================
    
    public void atualizarSenha(Long id, String novaSenha) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(id);

        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();

            // Criptografa a nova senha
            String senhaCriptografada = passwordEncoder.encode(novaSenha);
            usuario.setSenha(senhaCriptografada);

            // Salva no banco
            usuarioRepository.save(usuario);

            try {
                // Pega nome e e-mail do usuário
                String nome = usuario.getUsername() ; // ou getUsername(), se for esse o nome visível
                String email = usuario.getEmail();

                // Envia e-mail com aviso e nova senha
                emailService.enviarAvisoDeAlteracaoDeSenha(nome, email, novaSenha);
            } catch (Exception e) {
                System.err.println("Erro ao enviar e-mail de alteração de senha: " + e.getMessage());
            }
        } else {
            throw new RuntimeException("Usuário não encontrado com o ID: " + id);
        }
    }

   
}

