package com.cryptoadz.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "banners")
public class Banner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;

    private String urlDestino; // Para onde o banner leva quando clicado

    private String imagemUrl;  // Caminho ou URL da imagem salva no servidor

    
    private int totalCliquesPermitidos; // Meta de visualização ou cliques

    private int cliquesAtuais = 0; // Quantidade de cliques até agora

    private Integer tempoExibicao; // 
    
    @Column(columnDefinition = "DATETIME")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataCriacao;

    @Column(columnDefinition = "DATETIME")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataExpiracao;  // Alterado de LocalDateTime para LocalDate

    private boolean ativo = true;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario; // Anunciante dono do banner

	private String mensagemExpiracao;

    @PrePersist
    protected void onCreate() {
        this.dataCriacao = LocalDateTime.now();
    }

 
    public Banner(String titulo, String urlDestino, String imagemUrl, int totalCliquesPermitidos, LocalDateTime dataExpiracao, Usuario usuario, Integer tempoExibicao) {
        this.titulo = titulo;
        this.urlDestino = urlDestino;
        this.imagemUrl = imagemUrl;
        this.totalCliquesPermitidos = totalCliquesPermitidos;
        this.dataExpiracao = dataExpiracao;
        this.usuario = usuario;
        this.setMensagemExpiracao("Patrocinado"); // mensagem fixa
        this.tempoExibicao = tempoExibicao;
    }

    // Getters e Setters

    public Banner() {
		
	}


	public Long getId() { return id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getUrlDestino() { return urlDestino; }
    public void setUrlDestino(String urlDestino) { this.urlDestino = urlDestino; }

    public String getImagemUrl() { return imagemUrl; }
    public void setImagemUrl(String imagemUrl) { this.imagemUrl = imagemUrl; }

    public int getTotalCliquesPermitidos() { return totalCliquesPermitidos; }
    public void setTotalCliquesPermitidos(int totalCliquesPermitidos) { this.totalCliquesPermitidos = totalCliquesPermitidos; }

    public int getCliquesAtuais() { return cliquesAtuais; }
    public void setCliquesAtuais(int cliquesAtuais) { this.cliquesAtuais = cliquesAtuais; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }

 

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

	public Integer getTempoExibicao() {
		return tempoExibicao;
	}

	public void setTempoExibicao(Integer tempoExibicao) {
		this.tempoExibicao = tempoExibicao;
	}
	  public LocalDateTime getDataExpiracao() {
	        return dataExpiracao;
	    }

	   
		public void setDataExpiracao(LocalDateTime dataExpiracao) {
			this.dataExpiracao = dataExpiracao;
			
		}


		public String getMensagemExpiracao() {
			return mensagemExpiracao;
		}


		public void setMensagemExpiracao(String mensagemExpiracao) {
			this.mensagemExpiracao = mensagemExpiracao;
		}
	



	

	
}
