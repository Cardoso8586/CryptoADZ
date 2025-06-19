package com.cryptoadz.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cryptoadz.model.BonusDiario;
import com.cryptoadz.model.Usuario;

@Repository
public interface BonusDiarioRepository extends JpaRepository<BonusDiario, Long> {
    Optional<BonusDiario> findByUsuarioAndDataColeta(Usuario usuario, LocalDate dataColeta);
    Optional<BonusDiario> findTopByUsuarioOrderByDataColetaDesc(Usuario usuario);
    Optional<BonusDiario> findTopByUsuarioAndDataColetaBeforeOrderByDataColetaDesc(Usuario usuario, LocalDate data);

}
