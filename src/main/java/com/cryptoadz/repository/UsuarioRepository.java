package com.cryptoadz.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable; // ✅ import correto
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

    void save(Optional<Usuario> user); // 👈 Este método é estranho, explicação abaixo

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT u FROM Usuario u WHERE u.id = :id")
    Usuario findByIdForUpdate(@Param("id") Long id);

    // Método para pegar top N usuários por visualizações semanais
    @Query("SELECT u FROM Usuario u ORDER BY u.quantidadeVisualizacaoSemanal DESC")
    List<Usuario> findTopNByOrderByQuantidadeVisualizacaoSemanalDesc(Pageable pageable);

    default List<Usuario> findTopNByOrderByQuantidadeVisualizacaoSemanalDesc(int n) {
        return findTopNByOrderByQuantidadeVisualizacaoSemanalDesc(PageRequest.of(0, n));
    }


	List<Usuario> findAllByOrderByQuantidadeVisualizacaoSemanalDesc();


	List<Usuario> findTop3ByOrderByQuantidadeVisualizacaoSemanalDesc();

	


}
