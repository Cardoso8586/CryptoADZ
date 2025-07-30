package com.cryptoadz.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SaqueHistoricoDTO {
    private Long userId;
    private String carteiraDestino;
    private BigDecimal valorUSDT;
    private String status;
    private LocalDateTime dataHora;

    public SaqueHistoricoDTO(Long userId, String carteiraDestino, BigDecimal valorUSDT, String status, LocalDateTime dataHora) {
        this.userId = userId;
        this.carteiraDestino = carteiraDestino;
        this.valorUSDT = valorUSDT;
        this.status = status;
        this.dataHora = dataHora;
    }

    public Long getUserId() { return userId; }
    public String getCarteiraDestino() { return carteiraDestino; }
    public BigDecimal getValorUSDT() { return valorUSDT; }
    public String getStatus() { return status; }
    public LocalDateTime getDataHora() { return dataHora; }
}
