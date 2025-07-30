package com.cryptoadz.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cryptoadz.dto.SaqueHistoricoDTO;
import com.cryptoadz.model.SaqueHistorico;

public interface SaqueHistoricoRepository extends JpaRepository<SaqueHistorico, Long> {

    List<SaqueHistorico> findByUserId(Long userId);
    // List<SaqueHistorico> findByUserIdOrderByDataHoraDesc(Long userId);

  
    @Query("SELECT new com.cryptoadz.dto.SaqueHistoricoDTO(s.userId, s.carteiraDestino, s.valorUSDT, s.status, s.dataHora) FROM SaqueHistorico s WHERE s.userId = :userId")
    List<SaqueHistoricoDTO> buscarSemHash(@Param("userId") Long userId);


    

}
