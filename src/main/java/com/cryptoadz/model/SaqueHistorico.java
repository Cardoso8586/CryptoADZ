package com.cryptoadz.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SaqueHistorico {
    private Long userId;
    private String carteiraDestino;
    private BigDecimal valorUSDT;
    private String txHash;
    
    private LocalDateTime dataHora;

    public SaqueHistorico(Long userId, String carteiraDestino, BigDecimal valorUSDT, String txHash, LocalDateTime dataHora) {
        this.userId = userId;
        this.carteiraDestino = carteiraDestino;
        this.valorUSDT = valorUSDT;
        this.txHash = txHash;
        this.dataHora = dataHora;
    }

    // getters e setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    // outros getters/setters...

	public String getCarteiraDestino() {
		return carteiraDestino;
	}

	public void setCarteiraDestino(String carteiraDestino) {
		this.carteiraDestino = carteiraDestino;
	}

	
	public String getTxHash() {
		return txHash;
	}

	public void setTxHash(String txHash) {
		this.txHash = txHash;
	}

	public BigDecimal getValorUSDT() {
		return valorUSDT;
	}

	public void setValorUSDT(BigDecimal valorUSDT) {
		this.valorUSDT = valorUSDT;
	}

	public LocalDateTime getDataHora() {
		return dataHora;
	}

	public void setDataHora(LocalDateTime dataHora) {
		this.dataHora = dataHora;
	}
}


