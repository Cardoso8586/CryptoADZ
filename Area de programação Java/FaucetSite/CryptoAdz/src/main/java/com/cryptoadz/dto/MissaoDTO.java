// MissaoDTO.java (para enviar status)
package com.cryptoadz.dto;

public class MissaoDTO {
    private boolean assistirCompleted;
    private boolean cadastrarCompleted;
    private int contadorAssistir;
    private int contadorCadastrar;

    // getters e setters
    public boolean isAssistirCompleted() { return assistirCompleted; }
    public void setAssistirCompleted(boolean assistirCompleted) { this.assistirCompleted = assistirCompleted; }

    public boolean isCadastrarCompleted() { return cadastrarCompleted; }
    public void setCadastrarCompleted(boolean cadastrarCompleted) { this.cadastrarCompleted = cadastrarCompleted; }

    public int getContadorAssistir() { return contadorAssistir; }
    public void setContadorAssistir(int contadorAssistir) { this.contadorAssistir = contadorAssistir; }

    public int getContadorCadastrar() { return contadorCadastrar; }
    public void setContadorCadastrar(int contadorCadastrar) { this.contadorCadastrar = contadorCadastrar; }
}
