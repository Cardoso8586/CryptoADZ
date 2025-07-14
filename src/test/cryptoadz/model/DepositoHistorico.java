package com.cryptoadz.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "deposito_historico")
public class DepositoHistorico {


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "deposito_historico_seq")
    @SequenceGenerator(name = "deposito_historico_seq", sequenceName = "deposito_historico_seq", allocationSize = 1)
    private Long id;


    @Column(name = "data_deposito", nullable = false)
    private LocalDateTime dataDeposito;

    @Column(nullable = false)
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario user;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal valor;

    // Getters e Setters aqui
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Usuario getUser() {
        return user;
    }

    public void setUser(Usuario user) {
        this.user = user;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
}

