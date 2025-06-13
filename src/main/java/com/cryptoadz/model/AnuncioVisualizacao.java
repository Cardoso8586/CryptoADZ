package com.cryptoadz.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "anuncio_visualizacoes")
public class AnuncioVisualizacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "anuncio_id")
    private Anuncios anuncio;

    private String username;

    @Column(name = "bloqueio_expira_em")
    private LocalDateTime bloqueioExpiraEm;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

//===============================

    @Column(name = "total_clicks")
    private Long totalClicks = 0L;


// outros campos e relacionamentos

// Getters e Setters
public Long getTotalClicks() {
    return totalClicks;
}

public void setTotalClicks(Long totalClicks) {
    this.totalClicks = totalClicks;
}

//==============================
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Anuncios getAnuncio() {
        return anuncio;
    }

    public void setAnuncio(Anuncios anuncio) {
        this.anuncio = anuncio;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDateTime getBloqueioExpiraEm() {
        return bloqueioExpiraEm;
    }

    public void setBloqueioExpiraEm(LocalDateTime bloqueioExpiraEm) {
        this.bloqueioExpiraEm = bloqueioExpiraEm;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public boolean isBloqueado() {
        return bloqueioExpiraEm != null && LocalDateTime.now().isBefore(bloqueioExpiraEm);
    }

	
  

}
