package com.cryptoadz.dto;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;




public class AnuncioDTO {
@Autowired
	 
    @NotNull(message = "O ID do usuário é obrigatório")
    private Long usuarioId;

    @NotBlank(message = "O título é obrigatório")
    private String titulo;

    @NotBlank(message = "A descrição é obrigatória")
    private String descricao;

    @NotBlank(message = "A URL é obrigatória")
    private String url;

    @Min(value = 1, message = "A quantidade de visualizações deve ser no mínimo 1")
    private int maxVisualizacoes;

    @Min(value = 0, message = "O bloqueio em horas não pode ser negativo")
    private int bloqueioHoras;

    @NotNull(message = "O tempo de visualização é obrigatório")
    @Min(value = 5, message = "O tempo mínimo de visualização é 5 segundos")
    private Integer tempoVisualizacao;

    @NotNull(message = "Os tokens por visualização são obrigatórios")
    @DecimalMin(value = "0.01", message = "Deve ser no mínimo 0.01 token por visualização")
    private BigDecimal tokensPorVisualizacao;

 
    private BigDecimal tokensGastos;
   
    private BigDecimal meusAnuncios;
	
    // Getters e Setters

    public BigDecimal getTokensGastos() {
        return tokensGastos;
    }

   public void setTokensGastos(BigDecimal tokensGastos) {
       this.tokensGastos = tokensGastos;
  }
    
    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
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

    public int getMaxVisualizacoes() {
        return maxVisualizacoes;
    }

    public void setMaxVisualizacoes(int maxVisualizacoes) {
        this.maxVisualizacoes = maxVisualizacoes;
    }

    public int getBloqueioHoras() {
        return bloqueioHoras;
    }

    public void setBloqueioHoras(int bloqueioHoras) {
        this.bloqueioHoras = bloqueioHoras;
    }

    public Integer getTempoVisualizacao() {
        return tempoVisualizacao;
    }

    public void setTempoVisualizacao(Integer tempoVisualizacao) {
        this.tempoVisualizacao = tempoVisualizacao;
    }

    public BigDecimal getTokensPorVisualizacao() {
        return tokensPorVisualizacao;
    }

    public void setTokensPorVisualizacao(BigDecimal tokensPorVisualizacao) {
        this.tokensPorVisualizacao = tokensPorVisualizacao;
    }

	public BigDecimal getMeusAnuncios() {
		return meusAnuncios;
	}

	public void setMeusAnuncios(BigDecimal meusAnuncios) {
		this.meusAnuncios = meusAnuncios;
	}

}