// MissaoDiariaRepository.java
package com.cryptoadz.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cryptoadz.model.MissaoDiaria;
import com.cryptoadz.model.Usuario;

public interface MissaoDiariaRepository extends JpaRepository<MissaoDiaria, Long> {
    Optional<MissaoDiaria> findByUsuarioAndDataMissao(Usuario usuario, LocalDate dataMissao);
}