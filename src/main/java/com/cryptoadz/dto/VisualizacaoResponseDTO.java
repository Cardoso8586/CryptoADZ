package com.cryptoadz.dto;

import java.math.BigDecimal;

public class VisualizacaoResponseDTO {
  
    private BigDecimal tokensCreditados;

    public VisualizacaoResponseDTO(BigDecimal tokensCreditados) {
      
        this.tokensCreditados = tokensCreditados;
    }

    public BigDecimal getTokensCreditados() {
        return tokensCreditados;
    }

  
    public void setTokensCreditados(BigDecimal tokensCreditados) {
        this.tokensCreditados = tokensCreditados;
    }
}

