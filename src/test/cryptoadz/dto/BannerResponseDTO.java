package com.cryptoadz.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.cryptoadz.model.Banner;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;

public class BannerResponseDTO {
    private Long id;
    private String titulo;
    private String urlDestino;
    private String imagemUrl;
    private boolean ativo;
    private Integer tempoExibicao;
    
    @Column(columnDefinition = "DATETIME")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataCriacao;
    
    
    @Column(columnDefinition = "DATETIME")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataExpiracao;

    private BigDecimal saldoAtual;
    private BigDecimal tokensGastos;
    private String mensagem;  // campo para resposta ao usuário

    public BannerResponseDTO(Banner banner, BigDecimal tokensGastos, BigDecimal saldoAtual, String mensagem) {
        if (banner == null) {
            // Se banner for null, inicializa os campos com valores padrão
            this.id = null;
            this.titulo = "Banner não encontrado";
            this.urlDestino = "";
            this.imagemUrl = "";
            this.ativo = false;
            this.dataCriacao = null;
            this.dataExpiracao = null;
        } else {
            this.id = banner.getId();
            this.titulo = banner.getTitulo();
            this.urlDestino = banner.getUrlDestino();
            this.imagemUrl = banner.getImagemUrl();
            this.ativo = banner.isAtivo();
            this.dataCriacao = banner.getDataCriacao();
            this.dataExpiracao = banner.getDataExpiracao();
        }
        this.tokensGastos = tokensGastos;
        this.saldoAtual = saldoAtual;
        this.mensagem = mensagem;
    }


    // Getters e setters para todos os campos

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

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDateTime getDataExpiracao() {
        return dataExpiracao;
    }

    public void setDataExpiracao(LocalDateTime dataExpiracao) {
        this.dataExpiracao = dataExpiracao;
    }

    public BigDecimal getSaldoAtual() {
        return saldoAtual;
    }

    public void setSaldoAtual(BigDecimal saldoAtual) {
        this.saldoAtual = saldoAtual;
    }

    public BigDecimal getTokensGastos() {
        return tokensGastos;
    }

    public void setTokensGastos(BigDecimal tokensGastos) {
        this.tokensGastos = tokensGastos;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }


	public Integer getTempoExibicao() {
		return tempoExibicao;
	}


	public void setTempoExibicao(Integer tempoExibicao) {
		this.tempoExibicao = tempoExibicao;
	}
}

