package com.cryptoadz.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;

public class BannerDTO {
    private String titulo;
    private String urlDestino;
    private String imagemUrl;
    private Integer totalCliquesPermitidos;

    
    @Column(columnDefinition = "DATETIME")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataExpiracao;  

    private Boolean ativo;
    private Integer tempoExibicao;
    private BigDecimal tokensGastos;

    public BannerDTO() {}

    // Getters e Setters

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getUrlDestino() {
        return urlDestino;
    }

    public void setUrlDestino(String urlDestino) {
        this.urlDestino = urlDestino;
    }

    public String getImagemUrl() {
        return imagemUrl;
    }

    public void setImagemUrl(String imagemUrl) {
        this.imagemUrl = imagemUrl;
    }

    public Integer getTotalCliquesPermitidos() {
        return totalCliquesPermitidos;
    }

    public void setTotalCliquesPermitidos(Integer totalCliquesPermitidos) {
        this.totalCliquesPermitidos = totalCliquesPermitidos;
    }

    public LocalDateTime getDataExpiracao() {
        return dataExpiracao;
    }

    public void setDataExpiracao(LocalDateTime dataExpiracao) {
        this.dataExpiracao = dataExpiracao;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public Integer getTempoExibicao() {
        return tempoExibicao;
    }

    public void setTempoExibicao(Integer tempoExibicao) {
        this.tempoExibicao = tempoExibicao;
    }

    public BigDecimal getTokensGastos() {
        return tokensGastos;
    }

    public void setTokensGastos(BigDecimal tokensGastos) {
        this.tokensGastos = tokensGastos;
    }
}
