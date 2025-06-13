package com.cryptoadz.config;


import com.cryptoadz.model.Usuario;
import com.cryptoadz.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) 
            throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
        return User.withUsername(usuario.getUsername())
                   .password(usuario.getSenha())     // senha já tem hash
                   .roles("USER")                    // ajuste se precisar
                   .build();
    }
    
    
}

