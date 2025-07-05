package com.cryptoadz.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cryptoadz.model.Banner;

import jakarta.transaction.Transactional;

@Repository
public interface BannerRepository extends JpaRepository<Banner, Long> {
    List<Banner> findByUsuarioId(Long usuarioId);
    List<Banner> findByAtivoTrue();
    @Transactional
    @Modifying
    @Query("DELETE FROM Banner b WHERE b.dataExpiracao < :agora")
    int deleteExpiredBanners(@Param("agora") LocalDateTime agora);

    @Query("SELECT COUNT(b) FROM Banner b WHERE b.ativo = true")
    int countBannersAtivos();
   



}
