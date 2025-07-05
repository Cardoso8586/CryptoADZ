// MissaoDTO.java (para enviar status)
package com.cryptoadz.dto;

import java.math.BigDecimal;

public class MissaoDTO {
    private boolean assistirCompleted;
    private boolean cadastrarCompleted;
    private int contadorAssistir;
    private int contadorCadastrar;
    private BigDecimal recompensa_Assistir;
    private BigDecimal recompensa_Registrar;
    // getters e setters
    public boolean isAssistirCompleted() { return assistirCompleted; }
    public void setAssistirCompleted(boolean assistirCompleted) { this.assistirCompleted = assistirCompleted; }

    public boolean isCadastrarCompleted() { return cadastrarCompleted; }
    public void setCadastrarCompleted(boolean cadastrarCompleted) { this.cadastrarCompleted = cadastrarCompleted; }

    public int getContadorAssistir() { return contadorAssistir; }
    public void setContadorAssistir(int contadorAssistir) { this.contadorAssistir = contadorAssistir; }

    public int getContadorCadastrar() { return contadorCadastrar; }
    public void setContadorCadastrar(int contadorCadastrar) { this.contadorCadastrar = contadorCadastrar; }
	public BigDecimal getRecompensa_Assistir() {
		return recompensa_Assistir;
	}
	public void setRecompensa_Assistir(BigDecimal recompensa_Assistir) {
		this.recompensa_Assistir = recompensa_Assistir;
	}
	public BigDecimal getRecompensa_Registrar() {
		return recompensa_Registrar;
	}
	public void setRecompensa_Registrar(BigDecimal recompensa_Registrar) {
		this.recompensa_Registrar = recompensa_Registrar;
	}
    
   
}
