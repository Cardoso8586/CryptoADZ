package com.cryptoadz.model;
import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "anuncios") // Muda aqui para evitar conflito com "vagas"
public class Anuncios {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String descricao;
    private String url;
    
    @ManyToOne
    private Usuario usuario;
    
   @Column(name = "data_publicacao")
    private LocalDate dataPublicacao;
   
   
    @Column(name = "max_visualizacoes")
    private int maxVisualizacoes;
    
    
    @Column(name = "bloqueio_horas")
    private int bloqueio_horas;

    
    @Column(name = "tempo_visualizacao")
    private Integer tempoVisualizacao;

    
    
    @Column(name = "tokens_por_visualizacao")
    private BigDecimal tokensPorVisualizacao;

    @Column(name = "meus-anuncios")
    private BigDecimal meusAnuncios = BigDecimal.ZERO;
  
    // Getter e Setter
    public BigDecimal getMeusAnuncios() {
		return meusAnuncios;
	}

	public void setMeusAnuncios(BigDecimal meusAnuncios) {
		this.meusAnuncios = meusAnuncios;
	}
    public BigDecimal getTokensPorVisualizacao() {
        return tokensPorVisualizacao;
    }

    public void setTokensPorVisualizacao(BigDecimal tokensPorVisualizacao) {
        this.tokensPorVisualizacao = tokensPorVisualizacao;
    }

    
    
    public Integer getTempoVisualizacao() {
        return tempoVisualizacao;
    }

    public void setTempoVisualizacao(Integer tempoVisualizacao) {
        this.tempoVisualizacao = tempoVisualizacao;
    }
   

    
    public int getMaxVisualizacoes() {
        return maxVisualizacoes;
    }

    public void setMaxVisualizacoes(int maxVisualizacoes) {
        this.maxVisualizacoes = maxVisualizacoes;
    }

    public int getBloqueio_horas() {
        return bloqueio_horas;
    }

    public void setBloqueio_horas(int bloqueio_horas) {
        this.bloqueio_horas = bloqueio_horas;
    }

    

    // Getters e setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public LocalDate getDataPublicacao() {
        return dataPublicacao;
    }

    public void setDataPublicacao(LocalDate dataPublicacao) {
        this.dataPublicacao = dataPublicacao;
    }

	
}

