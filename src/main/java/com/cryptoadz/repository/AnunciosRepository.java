package com.cryptoadz.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;   // CORRETO
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.cryptoadz.model.Anuncios;
import com.cryptoadz.model.Usuario;

public interface AnunciosRepository extends JpaRepository<Anuncios, Long> {
    List<Anuncios> findAllByOrderByDataPublicacaoAsc();
    @Query("SELECT a FROM Anuncios a WHERE a.maxVisualizacoes > 0 AND " +
    	       "NOT EXISTS (SELECT 1 FROM AnuncioVisualizacao av WHERE av.anuncio.id = a.id AND av.username = :username)")
    	Page<Anuncios> findNaoVisualizadosPorUsuario(String username, Pageable pageable);
    
    
    List<Anuncios> findByUsuario(Usuario usuario);

    List<Anuncios> findByUsuarioId(Long usuarioId);
    
    List<Anuncios> findByUsuarioUsername(String username);

}