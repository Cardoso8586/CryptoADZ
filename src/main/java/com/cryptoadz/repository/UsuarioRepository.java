
package com.cryptoadz.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cryptoadz.model.Usuario;

import jakarta.persistence.LockModeType;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsername(String username);
  
	Usuario findByEmail(String email);
	 Usuario findByCodigoConfirmacao(String codigo);

	
    
	 Optional<Usuario> findByIpCadastro(String ipCadastro);

	void save(Optional<Usuario> user);

	//Object findByIdForUpdate(Long userId);
	
	   @Lock(LockModeType.PESSIMISTIC_WRITE)
	    @Query("SELECT u FROM Usuario u WHERE u.id = :id")
	    Usuario findByIdForUpdate(@Param("id") Long id);

}
