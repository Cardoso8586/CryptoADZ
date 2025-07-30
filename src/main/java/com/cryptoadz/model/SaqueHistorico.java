package com.cryptoadz.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "saque_historico")
public class SaqueHistorico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

   
    @Column(name = " user_Id", nullable = false)
    private Long userId;
    
    private String status;
    
    @Column(name = "carteira_destino", nullable = false)
    private String carteiraDestino;
    
    
    @Column(name = "valor_usdt", nullable = false)
    private BigDecimal valorUSDT;
    
    
    @Column(name = "tx_hash", nullable = false)
    private String txHash;
    
   
    @Column(name = " data_hora", nullable = false)
    private LocalDateTime dataHora;

    public SaqueHistorico() {
    }

    public SaqueHistorico(Long userId, String carteiraDestino, BigDecimal valorUSDT, String txHash, LocalDateTime dataHora) {
        this.userId = userId;
        this.carteiraDestino = carteiraDestino;
        this.valorUSDT = valorUSDT;
        this.txHash = txHash;
        this.dataHora = dataHora;
    }

    // Getters e setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getCarteiraDestino() { return carteiraDestino; }
    public void setCarteiraDestino(String carteiraDestino) { this.carteiraDestino = carteiraDestino; }

    public BigDecimal getValorUSDT() { return valorUSDT; }
    public void setValorUSDT(BigDecimal valorUSDT) { this.valorUSDT = valorUSDT; }

    public String getTxHash() { return txHash; }
    public void setTxHash(String txHash) { this.txHash = txHash; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}



