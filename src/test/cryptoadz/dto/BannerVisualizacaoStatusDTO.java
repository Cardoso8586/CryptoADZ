package com.cryptoadz.dto;

import java.math.BigDecimal;

public class BannerVisualizacaoStatusDTO {

    private int bannersVistos;
    private int bannersParaRecompensa;
    private int tokensGanhos;
    private int limitePorDia;
    private BigDecimal tokensAtualizados;

    public BannerVisualizacaoStatusDTO(int bannersVistos, int bannersParaRecompensa, int tokensGanhos) {
        this.bannersVistos = bannersVistos;
        this.bannersParaRecompensa = bannersParaRecompensa;
        this.tokensGanhos = tokensGanhos;
    }

    public BannerVisualizacaoStatusDTO() {
		
	}

	public String getProgresso() {
        return bannersVistos + "/" + bannersParaRecompensa;
    }

    // Getters e setters
    public int getBannersVistos() {
        return bannersVistos;
    }

    public int getBannersParaRecompensa() {
        return bannersParaRecompensa;
    }

    public int getTokensGanhos() {
        return tokensGanhos;
    }
    public void setBannersVistos(int bannersVistos) {
        this.bannersVistos = bannersVistos;
    }

	public int getLimitePorDia() {
		return limitePorDia;
	}

	public void setLimitePorDia(int limitePorDia) {
		this.limitePorDia = limitePorDia;
	}

	public BigDecimal getTokensAtualizados() {
		return tokensAtualizados;
	}

	public void setTokensAtualizados(BigDecimal tokensAtualizados) {
		this.tokensAtualizados = tokensAtualizados;
	}
  

}
