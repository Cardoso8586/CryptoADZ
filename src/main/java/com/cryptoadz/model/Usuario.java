package com.cryptoadz.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
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
    
    private int bannersVistos;  // contador de banners vistos

    @Column(name = "meus-anuncios")
    private BigDecimal meusAnuncios = BigDecimal.ZERO;
    
    private LocalDate dataUltimaColeta;

    private LocalDate dataUltimaVisualizacao;  // nova data para controle de banners diários
    @Column(name = "ip_cadastro", unique = true)
    private String ipCadastro;

    @Column(name = "usdt_saldo", precision = 19, scale = 8)
    private BigDecimal usdtSaldo = BigDecimal.ZERO;

    @Column(name = "usdt_wallet", precision = 19, scale = 8)
    private BigDecimal usdtWallet = BigDecimal.ZERO;

    
    @Column(name = "quantidade_visualizacao_semanal")
    private int quantidadeVisualizacaoSemanal;

    // ... construtores, outros getters e setters ...

    public BigDecimal getUsdtSaldo() {
        return usdtSaldo;
    }

    public void setUsdtSaldo(BigDecimal usdtSaldo) {
        this.usdtSaldo = usdtSaldo;
    }

    public BigDecimal getUsdtWallet() {
        return usdtWallet;
    }

    public void setUsdtWallet(BigDecimal usdtWallet) {
        this.usdtWallet = usdtWallet;
    }

   
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.saldoTokens == null) {
            this.saldoTokens = BigDecimal.ZERO; // inicializa saldo com zero se null
        }
       
        if (this.usdtSaldo == null) {
            this.usdtSaldo = BigDecimal.ZERO;
        }
        if (this.usdtWallet == null) {
            this.usdtWallet = BigDecimal.ZERO;
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
    @Column(name = "premio_pendente")
    private BigDecimal premioPendente = BigDecimal.ZERO;

    public BigDecimal getPremioPendente() {
        return premioPendente;
    }

    public void setPremioPendente(BigDecimal premioPendente) {
        this.premioPendente = premioPendente;
    }

    
 // Getter
    public String getIpCadastro() {
        return ipCadastro;
    }

    // Setter
    public void setIpCadastro(String ipCadastro) {
        this.ipCadastro = ipCadastro;
    }
    // Getters e Setters
    public LocalDate getDataUltimaVisualizacao() {
        return dataUltimaVisualizacao;
    }

    public void setDataUltimaVisualizacao(LocalDate dataUltimaVisualizacao) {
        this.dataUltimaVisualizacao = dataUltimaVisualizacao;
    }

 // getters e setters para dataUltimaColeta
    public LocalDate getDataUltimaColeta() {
        return dataUltimaColeta;
    }

    public void setDataUltimaColeta(LocalDate dataUltimaColeta) {
        this.dataUltimaColeta = dataUltimaColeta;
    }
    public int getBannersVistos() {
        return bannersVistos;
    }

    // setter para banners vistos
    public void setBannersVistos(int bannersVistos) {
        this.bannersVistos = bannersVistos;
    }
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

	public int getQuantidadeVisualizacaoSemanal() {
		return quantidadeVisualizacaoSemanal;
	}

	public void setQuantidadeVisualizacaoSemanal(int quantidadeVisualizacaoSemanal) {
		this.quantidadeVisualizacaoSemanal = quantidadeVisualizacaoSemanal;
	}





	
}

