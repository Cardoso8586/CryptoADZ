package com.cryptoadz.dto;

import java.math.BigDecimal;

public class SolicitacaoDepositoRequest {
    private Long userId;
    private BigDecimal valor;
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

    // getters e setters
}


