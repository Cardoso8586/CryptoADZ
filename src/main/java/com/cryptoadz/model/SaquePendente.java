package com.cryptoadz.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "saque_pendente")
public class SaquePendente {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	
	private BigDecimal valor_solicitado = BigDecimal.ZERO;
  
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Usuario user;

    private boolean confirmado;

    private String enderecoDestino;
    
    @Column(nullable = false)
    private String status; // PENDENTE, CONFIRMADO, REJEITADO

    @Column(columnDefinition = "datetime")
    private LocalDateTime dataSolicitacao;


    @Column(name = "transaction_id")
    private String transactionId; // hash da transação de saque

    // Getters e setters

    public Long getId() {
        return id;
    }

   

    public String getEnderecoDestino() {
        return enderecoDestino;
    }

    public void setEnderecoDestino(String enderecoDestino) {
        this.enderecoDestino = enderecoDestino;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public LocalDateTime getDataSolicitacao() {
        return dataSolicitacao;
    }

    public void setDataSolicitacao(LocalDateTime dataSolicitacao) {
        this.dataSolicitacao = dataSolicitacao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Usuario getUser() {
        return user;
    }

    public void setUser(Usuario user) {
        this.user = user;
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

	
}
