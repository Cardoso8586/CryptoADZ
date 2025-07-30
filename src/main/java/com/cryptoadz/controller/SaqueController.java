package com.cryptoadz.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cryptoadz.model.SaqueHistorico;
import com.cryptoadz.repository.SaqueHistoricoRepository;
import com.cryptoadz.service.SaqueService;
import com.fasterxml.jackson.annotation.JsonProperty;

@RestController
@RequestMapping("/api/saque")
public class SaqueController {

	
	 @Autowired
	 private SaqueHistoricoRepository saqueHistoricoRepository;
	 
	 
    private final SaqueService saqueService;

   
    public SaqueController(SaqueService saqueService) {
        this.saqueService = saqueService;
    }

    @PostMapping
    public ResponseEntity<String> saque(@RequestBody SaqueRequest request) {
        try {
            String txHash = saqueService.realizarSaque(
                request.getUserId(),
                request.getCarteiraDestino(),
                request.getValorUSDT()
                
            );
            
           // System.out.println("Recebido no backend:");
           // System.out.println("userId = " + request.getUserId());
          //  System.out.println("carteiraDestino = " + request.getCarteiraDestino());
           // System.out.println("valorUSDT = " + request.getValorUSDT());
            return ResponseEntity.ok(txHash);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro: " + e.getMessage());
        }
    }

    
   

    @GetMapping("/historico/{userId}")
    public List<SaqueHistorico> listarHistoricoPorUsuario(@PathVariable Long userId) {
        return saqueHistoricoRepository.findByUserIdOrderByDataHoraDesc(userId);
    }

}

class SaqueRequest {
	  @JsonProperty("userId")
	    private Long userId;

	 @JsonProperty("carteiraDestino")
	 private String carteiraDestino;

	 @JsonProperty("valorUSDT")
	 private BigDecimal valorUSDT;
    // getters e setters
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public String getCarteiraDestino() {
        return carteiraDestino;
    }
    public void setCarteiraDestino(String carteiraDestino) {
        this.carteiraDestino = carteiraDestino;
    }
    public BigDecimal getValorUSDT() {
        return valorUSDT;
    }
    public void setValorUSDT(BigDecimal valorUSDT) {
        this.valorUSDT = valorUSDT;
    }
}


