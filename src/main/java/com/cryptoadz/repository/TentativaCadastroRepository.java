package com.cryptoadz.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cryptoadz.model.TentativaCadastro;

@Repository
public interface TentativaCadastroRepository extends JpaRepository<TentativaCadastro, Long> {
    Optional<TentativaCadastro> findByIp(String ip);
    
}
