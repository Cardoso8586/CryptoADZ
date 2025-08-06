package com.cryptoadz.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.cryptoadz.model.Anuncios;

public class AnuncioResponseDTO {
    private String titulo;
    private String descricao;
    private String url;
    private LocalDate dataPublicacao;
    private BigDecimal tokensGastos;
    private BigDecimal saldoAtualUsuario;
    private int bloqueioHoras; 

    public AnuncioResponseDTO(Anuncios anuncio, BigDecimal tokensGastos, BigDecimal saldoAtualUsuario) {
        this.setTitulo(anuncio.getTitulo());
        this.setDescricao(anuncio.getDescricao());
        this.setUrl(anuncio.getUrl());
        this.setDataPublicacao(anuncio.getDataPublicacao());
        this.setTokensGastos(tokensGastos);
        this.setSaldoAtualUsuario(saldoAtualUsuario);
        this.setBloqueioHoras(anuncio.getBloqueio_horas()); // <- Adiciona valor do model
    }

    private String mensagem;

    public AnuncioResponseDTO(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
    // Getters e Setters

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

    public BigDecimal getTokensGastos() {
        return tokensGastos;
    }

    public void setTokensGastos(BigDecimal tokensGastos) {
        this.tokensGastos = tokensGastos;
    }

    public BigDecimal getSaldoAtualUsuario() {
        return saldoAtualUsuario;
    }

    public void setSaldoAtualUsuario(BigDecimal saldoAtualUsuario) {
        this.saldoAtualUsuario = saldoAtualUsuario;
    }

    public int getBloqueioHoras() {
        return bloqueioHoras;
    }

    public void setBloqueioHoras(int bloqueioHoras) {
        this.bloqueioHoras = bloqueioHoras;
    }
}
