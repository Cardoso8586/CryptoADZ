package com.cryptoadz.repository;


import com.cryptoadz.model.Aviso;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AvisoRepository extends JpaRepository<Aviso, Long> {
	void deleteByDataValidadeBefore(LocalDate date);
	
}
