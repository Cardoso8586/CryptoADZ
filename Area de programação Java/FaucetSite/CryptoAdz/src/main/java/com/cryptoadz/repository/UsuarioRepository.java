
package com.cryptoadz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cryptoadz.model.Usuario;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsername(String username);
  
	Usuario findByEmail(String email);
	 Usuario findByCodigoConfirmacao(String codigo);
    
	

}
