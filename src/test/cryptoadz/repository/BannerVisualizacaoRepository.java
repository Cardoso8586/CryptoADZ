package com.cryptoadz.repository;


import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cryptoadz.model.BannerVisualizacao;

public interface BannerVisualizacaoRepository extends JpaRepository<BannerVisualizacao, Long> {

    @Query("SELECT COUNT(v) FROM BannerVisualizacao v " +
           "WHERE v.usuario.id = :usuarioId " +
           "AND v.banner.id = :bannerId " +
           "AND DATE(v.dataVisualizacao) = :data")
    
   
    long countVisualizacoesPorDia(@Param("usuarioId") Long usuarioId, @Param("bannerId") Long bannerId, @Param("data") LocalDate data);
 

}
