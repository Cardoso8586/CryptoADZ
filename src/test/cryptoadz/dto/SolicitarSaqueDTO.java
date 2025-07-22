package com.cryptoadz.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public class SolicitarSaqueDTO {

    @NotNull(message = "UserId é obrigatório")
    private Long userId;

    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    private BigDecimal valor_solicitado;

    @NotNull(message = "Endereço destino é obrigatório")
    private String enderecoDestino;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

   
    public String getEnderecoDestino() {
        return enderecoDestino;
    }

    public void setEnderecoDestino(String enderecoDestino) {
        this.enderecoDestino = enderecoDestino;
    }

    public BigDecimal getValor_solicitado() {
		return valor_solicitado;
	}

	public void setValor_solicitado(BigDecimal valor_solicitado) {
		this.valor_solicitado = valor_solicitado;
	}
}
