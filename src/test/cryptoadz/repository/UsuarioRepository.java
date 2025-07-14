
package com.cryptoadz.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cryptoadz.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsername(String username);
  
	Usuario findByEmail(String email);
	 Usuario findByCodigoConfirmacao(String codigo);

	
    
	 Optional<Usuario> findByIpCadastro(String ipCadastro);

	void save(Optional<Usuario> user);
	

}
