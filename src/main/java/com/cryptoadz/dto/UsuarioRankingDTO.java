package com.cryptoadz.dto;

public class UsuarioRankingDTO {
    private Long id;
    private String nome;
    private int visualizacoes;
    private int posicao;

    // construtores
    public UsuarioRankingDTO(Long id, String nome, int visualizacoes, int posicao) {
        this.setId(id);
        this.setNome(nome);
        this.setVisualizacoes(visualizacoes);
        this.setPosicao(posicao);
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public int getVisualizacoes() {
		return visualizacoes;
	}

	public void setVisualizacoes(int visualizacoes) {
		this.visualizacoes = visualizacoes;
	}

	public int getPosicao() {
		return posicao;
	}

	public void setPosicao(int posicao) {
		this.posicao = posicao;
	}

    // getters e setters
}
