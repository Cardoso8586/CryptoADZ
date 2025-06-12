package com.cryptoadz.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cryptoadz.model.AnuncioVisualizacao;

import jakarta.transaction.Transactional;
public interface AnuncioVisualizacaoRepository extends JpaRepository<AnuncioVisualizacao, Long> {
	

    // Declarar esse método para apagar todas as visualizações relacionadas a um anúncio
    @Transactional
    void deleteByAnuncio_Id(Long anuncioId);
    
    AnuncioVisualizacao findByUsernameAndAnuncio_Id(String username, Long anuncioId);
 // Exemplo usando JPA
    @Modifying
    @Query("DELETE FROM AnuncioVisualizacao v WHERE v.anuncio.id = :anuncioId")
    void deletarVisualizacoesPorAnuncioId(@Param("anuncioId") Long anuncioId);

	AnuncioVisualizacao findByUsuarioUsernameAndAnuncioId(String username, Long anuncioId);

	 @Query("SELECT SUM(a.totalClicks) FROM AnuncioVisualizacao a")
	    Long somarTodosTotalClicks();

}
