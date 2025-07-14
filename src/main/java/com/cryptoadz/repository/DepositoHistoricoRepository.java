package com.cryptoadz.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cryptoadz.model.DepositoHistorico;
import com.cryptoadz.model.Usuario;

public interface DepositoHistoricoRepository extends JpaRepository<DepositoHistorico, Long> {
    List<DepositoHistorico> findByUser(Usuario user);
    List<DepositoHistorico> findByUserId(Long userId);
}
