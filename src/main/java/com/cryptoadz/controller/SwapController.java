package com.cryptoadz.controller;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cryptoadz.dto.SwapRequest;
import com.cryptoadz.service.SwapService;

@RestController
@RequestMapping("/api/swap")
public class SwapController {

    private final SwapService swapService;

    public SwapController(SwapService swapService) {
        this.swapService = swapService;
    }

    @PostMapping
    public ResponseEntity<?> realizarTroca(@RequestBody SwapRequest request) {
        try {
            BigDecimal valorRecebido = swapService.realizarSwap(request);
            return ResponseEntity.ok(Map.of("sucesso", true, "valorRecebido", valorRecebido));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }
}
