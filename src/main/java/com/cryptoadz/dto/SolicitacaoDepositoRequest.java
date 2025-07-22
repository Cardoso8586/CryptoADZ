package com.cryptoadz.dto;

import java.math.BigDecimal;

public class SolicitacaoDepositoRequest {
    private Long userId;
    private BigDecimal valor;
    private Boolean confirmado;
    
    
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public BigDecimal getValor() {
		return valor;
	}
	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public Boolean getConfirmado() {
		return confirmado;
	}
	public void setConfirmado(Boolean confirmado) {
		this.confirmado = confirmado;
	}


    // getters e setters
}


