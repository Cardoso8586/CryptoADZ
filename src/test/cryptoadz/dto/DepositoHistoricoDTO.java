package com.cryptoadz.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.cryptoadz.model.DepositoHistorico;

public class DepositoHistoricoDTO {
    private BigDecimal valor;
    private LocalDateTime dataDeposito;
    private String status;

    public DepositoHistoricoDTO(DepositoHistorico h) {
        this.setValor(h.getValor());
        this.setDataDeposito(h.getDataDeposito());
        this.setStatus(h.getStatus());
    }

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public LocalDateTime getDataDeposito() {
		return dataDeposito;
	}

	public void setDataDeposito(LocalDateTime dataDeposito) {
		this.dataDeposito = dataDeposito;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

    // getters e setters
}
