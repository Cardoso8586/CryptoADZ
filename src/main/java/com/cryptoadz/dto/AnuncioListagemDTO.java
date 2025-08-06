package com.cryptoadz.dto;

import com.cryptoadz.model.Anuncios;

public class AnuncioListagemDTO {
    private Long id;
    private String titulo;
    private String descricao;
    private String url;

    public AnuncioListagemDTO(Anuncios anuncio) {
        this.setId(anuncio.getId());
        this.setTitulo(anuncio.getTitulo());
        this.setDescricao(anuncio.getDescricao());
        this.setUrl(anuncio.getUrl());
    }

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

    // Getters e Setters
}
