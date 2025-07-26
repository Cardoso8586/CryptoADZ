package com.cryptoadz.service;

import com.cryptoadz.model.Usuario;
import com.cryptoadz.repository.UsuarioRepository;

import jakarta.transaction.Transactional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {
	
	   private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final UsuarioRepository usuarioRepository;

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

        usuario.setUsername(novoNome);
        usuarioRepository.save(usuario);
    }

    //==============================  atualizarSenha ==========================================
    
    public void atualizarSenha(Long id, String novaSenha) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(id);

        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();
            
            // Criptografa a nova senha antes de salvar
            String senhaCriptografada = passwordEncoder.encode(novaSenha);
            usuario.setSenha(senhaCriptografada);

            // Salva o usuário com a nova senha
            usuarioRepository.save(usuario);
        } else {
            throw new RuntimeException("Usuário não encontrado com o ID: " + id);
        }
    }
   
}

