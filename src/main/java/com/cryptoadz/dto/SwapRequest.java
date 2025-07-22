package com.cryptoadz.dto;

public class SwapRequest {
    private String from;       // "usdt" ou "token"
    private String to;         // "token" ou "usdt"
    private double fromAmount; // Valor enviado na troca
    private Long userId; // ← mudou aqui
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public double getFromAmount() {
		return fromAmount;
	}
	public void setFromAmount(double fromAmount) {
		this.fromAmount = fromAmount;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}

    // Getters e Setters
}
