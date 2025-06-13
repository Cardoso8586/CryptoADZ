// src/main/java/com/cryptoadz/model/MissaoDiaria.java
package com.cryptoadz.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "missao_do_dia")
public class MissaoDiaria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "data_missao", nullable = false)
    private LocalDate dataMissao;

    // Contadores, com DEFAULT no DDL
    @Column(name = "contador_assistir", nullable = false, columnDefinition = "INT NOT NULL DEFAULT 0")
    private int contadorAssistir = 0;

    @Column(name = "contador_cadastrar", nullable = false, columnDefinition = "INT NOT NULL DEFAULT 0")
    private int contadorCadastrar = 0;

    // Flags de recompensa, com DEFAULT no DDL
    @Column(name = "recompensa_assistiu", nullable = false, columnDefinition = "BOOLEAN NOT NULL DEFAULT FALSE")
    private boolean recompensaAssistiu = false;

    @Column(name = "recompensa_cadastrou", nullable = false, columnDefinition = "BOOLEAN NOT NULL DEFAULT FALSE")
    private boolean recompensaCadastrou = false;

    // Construtor padrão
    public MissaoDiaria() {}

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public LocalDate getDataMissao() { return dataMissao; }
    public void setDataMissao(LocalDate dataMissao) { this.dataMissao = dataMissao; }

    public int getContadorAssistir() { return contadorAssistir; }
    public void setContadorAssistir(int contadorAssistir) { this.contadorAssistir = contadorAssistir; }

    public int getContadorCadastrar() { return contadorCadastrar; }
    public void setContadorCadastrar(int contadorCadastrar) { this.contadorCadastrar = contadorCadastrar; }

    public boolean isRecompensaAssistiu() { return recompensaAssistiu; }
    public void setRecompensaAssistiu(boolean recompensaAssistiu) { this.recompensaAssistiu = recompensaAssistiu; }

    public boolean isRecompensaCadastrou() { return recompensaCadastrou; }
    public void setRecompensaCadastrou(boolean recompensaCadastrou) { this.recompensaCadastrou = recompensaCadastrou; }
}

