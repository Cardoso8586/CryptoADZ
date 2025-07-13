package com.cryptoadz.dto;

public class EnderecoDepositoResponse {
    private String endereco;

    public EnderecoDepositoResponse(String endereco) {
        this.endereco = endereco;
    }

    // getter
    public String getEndereco() {
        return endereco;
    }
    
}