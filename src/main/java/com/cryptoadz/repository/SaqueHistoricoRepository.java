package com.cryptoadz.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cryptoadz.model.SaqueHistorico;

public interface SaqueHistoricoRepository extends JpaRepository<SaqueHistorico, Long> {
   // List<SaqueHistorico> findByUserId(Long userId);
    List<SaqueHistorico> findByUserIdOrderByDataHoraDesc(Long userId);

    
}
