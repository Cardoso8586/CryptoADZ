package com.cryptoadz.model;


import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.*;

@Entity
@Table(name = "banner_visualizacoes")
public class BannerVisualizacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "banner_id", nullable = false)
    private Banner banner;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "data_visualizacao", columnDefinition = "DATETIME")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataVisualizacao;

    
   
    @Column(name = "visualizado_em")
    private LocalDate visualizadoEm; // ou LocalDateTime, dependendo do que quer
    
    @PrePersist
    public void onCreate() {
        this.dataVisualizacao = LocalDateTime.now();
    }

    public BannerVisualizacao() {}

    public BannerVisualizacao(Banner banner, Usuario usuario) {
        this.banner = banner;
        this.usuario = usuario;
    }

    // Getters e Setters

    public Long getId() {
        return id;
    }

    public Banner getBanner() {
        return banner;
    }

    public void setBanner(Banner banner) {
        this.banner = banner;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public LocalDateTime getDataVisualizacao() {
        return dataVisualizacao;
    }

    public void setDataVisualizacao(LocalDateTime dataVisualizacao) {
        this.dataVisualizacao = dataVisualizacao;
    }

    public LocalDate getVisualizadoEm() {
        return visualizadoEm;
    }

    public void setVisualizadoEm(LocalDate visualizadoEm) {
        this.visualizadoEm = visualizadoEm;
    }

	
}
