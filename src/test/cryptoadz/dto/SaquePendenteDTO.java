package com.cryptoadz.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.cryptoadz.model.Usuario;

public class SaquePendenteDTO {

    private Long id;

 
    private Usuario user;

    private BigDecimal valor_solicitado = BigDecimal.ZERO;
    private String enderecoDestino;
    private String status;
    private LocalDateTime dataSolicitacao;
    private String transactionId;
    private boolean confirmado;

    // Construtor padrão
    public SaquePendenteDTO() {}

    // Construtor com campos úteis
    public SaquePendenteDTO(Long id, BigDecimal valor_solicitado, String enderecoDestino,
                            String status, LocalDateTime dataSolicitacao, String transactionId, boolean confirmado) {
        this.id = id;
        this.enderecoDestino = enderecoDestino;
        this.status = status;
        this.valor_solicitado = valor_solicitado;
        this.dataSolicitacao = dataSolicitacao;
        this.transactionId = transactionId;
        this.confirmado = confirmado;
    }

    // Getters e Setters

    public Long getId() {
        return id;
    }

   

    public String getEnderecoDestino() {
        return enderecoDestino;
    }

    public void setEnderecoDestino(String enderecoDestino) {
        this.enderecoDestino = enderecoDestino;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getDataSolicitacao() {
        return dataSolicitacao;
    }

    public void setDataSolicitacao(LocalDateTime dataSolicitacao) {
        this.dataSolicitacao = dataSolicitacao;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public boolean isConfirmado() {
        return confirmado;
    }

    public void setConfirmado(boolean confirmado) {
        this.confirmado = confirmado;
    }



	public BigDecimal getValor_solicitado() {
		return valor_solicitado;
	}

	public void setValor_solicitado(BigDecimal valor_solicitado) {
		this.valor_solicitado = valor_solicitado;
	}

	 public Usuario getUser() {
	        return user;
	    }

	    public void setUser(Usuario user) {
	        this.user = user;
	    }
	
}
