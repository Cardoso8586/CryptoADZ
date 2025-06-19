package com.cryptoadz.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "avisos")
public class Aviso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String titulo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String mensagem;

    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;

    @Column(name = "data_validade", nullable = false)
    private LocalDate dataValidade;

    @Column(name = "criado_em", updatable = false)
    private LocalDate criadoEm = LocalDate.now();

    // 🔗 Construtores
    public Aviso() {
    }

    public Aviso(String titulo, String mensagem, LocalDate dataInicio, LocalDate dataValidade) {
        this.titulo = titulo;
        this.mensagem = mensagem;
        this.dataInicio = dataInicio;
        this.dataValidade = dataValidade;
        this.criadoEm = LocalDate.now();
    }

    // 🔧 Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataValidade() {
        return dataValidade;
    }

    public void setDataValidade(LocalDate dataValidade) {
        this.dataValidade = dataValidade;
    }

    public LocalDate getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDate criadoEm) {
        this.criadoEm = criadoEm;
    }
}

