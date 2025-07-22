package com.cryptoadz.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cryptoadz.model.Anuncios;

@Repository
public interface AnuncioRepository extends JpaRepository<Anuncios, Long> {
    // Métodos customizados podem ser adicionados aqui
	 Page<Anuncios> findByMaxVisualizacoesGreaterThan(int value, Pageable pageable);
	 Page<Anuncios> findByMaxVisualizacoesGreaterThanAndIdNotIn(int maxVisualizacoes, List<Long> ids, Pageable pageable);
	 
	 
	 @Query("SELECT a FROM Anuncios a WHERE a.id NOT IN (SELECT av.anuncio.id FROM AnuncioVisualizacao av WHERE av.username = :username AND av.bloqueioExpiraEm > CURRENT_TIMESTAMP)")
	 List<Anuncios> findAnunciosDisponiveis(@Param("username") String username);

	 long count();
	
	 List<Anuncios> findByUsuario_Username(String username);
	

	

}
