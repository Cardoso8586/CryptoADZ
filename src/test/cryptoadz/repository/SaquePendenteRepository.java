package com.cryptoadz.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cryptoadz.dto.SaquePendenteDTO;
import com.cryptoadz.model.SaquePendente;
import com.cryptoadz.model.Usuario;

@Repository
public interface SaquePendenteRepository extends JpaRepository<SaquePendente, Long> {

    // Para buscar todos os saques de um usuário (ex. histórico)
    List<SaquePendente> findByUserId(Long userId);

    // Para buscar saques pendentes (você pode filtrar mais se quiser)
    List<SaquePendente> findByStatus(String status);

    // ✅ Correto para buscar o último saque por data usando o objeto Usuario
    Optional<SaquePendente> findTopByUserOrderByDataSolicitacaoDesc(Usuario user);

    // ✅ Alternativa caso queira buscar diretamente por ID do usuário (JPQL com LIMIT 1)
    @Query("SELECT s FROM SaquePendente s WHERE s.user.id = :userId ORDER BY s.dataSolicitacao DESC")
    List<SaquePendente> findUltimoSaquePorUserId(Long userId);

	
	
}
