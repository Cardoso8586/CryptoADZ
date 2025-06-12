package com.cryptoadz.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String email;

    @Column(name = "password_hash")
    private String senha;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "saldo_tokens")
    private BigDecimal saldoTokens;

    @Column(name = "meus-anuncios")
    private BigDecimal meusAnuncios = BigDecimal.ZERO;
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.saldoTokens == null) {
            this.saldoTokens = BigDecimal.ZERO; // inicializa saldo com zero se null
        }
    }

    public Usuario() {}

    public Usuario(String username, String email, String senha) {
        this.username = username;
        this.email = email;
        this.senha = senha;
        this.saldoTokens = BigDecimal.ZERO; // saldo inicial zero
    }
    
    private String codigoConfirmacao;
    private boolean ativo = false;

    // Getters e Setters
    public BigDecimal getMeusAnuncios() {
		return meusAnuncios;
	}

	public void setMeusAnuncios(BigDecimal meusAnuncios) {
		this.meusAnuncios = meusAnuncios;
	}
   
    public Long getId() { return id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public BigDecimal getSaldoTokens() { return saldoTokens; }
    public void setSaldoTokens(BigDecimal saldoTokens) { this.saldoTokens = saldoTokens; }

	//public Long setId(Long usuarioId) {
		//return id;
		
	//}
	public void setId(Long id) {
	    this.id = id;
	}


	public String getCodigoConfirmacao() {
		return codigoConfirmacao;
	}

	public void setCodigoConfirmacao(String codigoConfirmacao) {
		this.codigoConfirmacao = codigoConfirmacao;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}
	
	
}

