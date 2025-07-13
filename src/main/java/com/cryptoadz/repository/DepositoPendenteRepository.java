package com.cryptoadz.repository;

import com.cryptoadz.model.DepositoPendente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepositoPendenteRepository extends JpaRepository<DepositoPendente, Long> {
    List<DepositoPendente> findByConfirmadoFalse();
	List<DepositoPendente> findByStatus(String status);
	 Optional<DepositoPendente> findTopByUserIdAndStatusInOrderByDataSolicitacaoDesc(Long userId, List<String> statusList);
}
