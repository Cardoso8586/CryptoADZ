package com.cryptoadz.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class DepositoPendente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Usuario user;

    private BigDecimal valorEsperado;
    private boolean confirmado;

    @Column(name = "data_solicitacao")
    private LocalDateTime dataSolicitacao;

    private String enderecoDeposito;

    @Column(nullable = false)
    private String status = "PENDENTE"; 
    // Getters e Setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getUser() { return user; }
    public void setUser(Usuario user) { this.user = user; }

    public BigDecimal getValorEsperado() { return valorEsperado; }
    public void setValorEsperado(BigDecimal valorEsperado) { this.valorEsperado = valorEsperado; }

    public boolean isConfirmado() { return confirmado; }
    public void setConfirmado(boolean confirmado) { this.confirmado = confirmado; }

    public LocalDateTime getDataSolicitacao() { return dataSolicitacao; }
    public void setDataSolicitacao(LocalDateTime dataSolicitacao) { this.dataSolicitacao = dataSolicitacao; }

    public String getEnderecoDeposito() { return enderecoDeposito; }
    public void setEnderecoDeposito(String enderecoDeposito) { this.enderecoDeposito = enderecoDeposito; }
    
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

