package com.cryptoadz.model;

import java.math.BigDecimal;

public class DepositoRequest {
    private Long userId;
    private BigDecimal valor;
    // Getters e setters
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
}

