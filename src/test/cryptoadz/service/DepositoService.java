package com.cryptoadz.service;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.cryptoadz.model.DepositoHistorico;
import com.cryptoadz.model.DepositoPendente;
import com.cryptoadz.model.Usuario;
import com.cryptoadz.repository.DepositoHistoricoRepository;
import com.cryptoadz.repository.DepositoPendenteRepository;
import com.cryptoadz.repository.UsuarioRepository;

import jakarta.transaction.Transactional;

@Service
public class DepositoService {
	
   
    
    
	@Value("${tron.api.key}")
	private String tronApiKey ;
	
    @Value("${deposito.endereco.fixo}")
    private String enderecoFixo; // endereço fixo da carteira

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private DepositoHistoricoRepository depositoHistoricoRepository;

    
    @Autowired
    private DepositoPendenteRepository depositoPendenteRepository;

    @Transactional
    public void solicitarDeposito(Long userId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.valueOf(3)) < 0) {
            throw new IllegalArgumentException("O valor mínimo para depósito é 3 USDT.");
        }

        Usuario user = buscarUsuario(userId);

        DepositoPendente deposito = new DepositoPendente();
        deposito.setUser(user);
        deposito.setValorEsperado(amount);
        deposito.setConfirmado(false);
        deposito.setDataSolicitacao(LocalDateTime.now());
        deposito.setEnderecoDeposito(enderecoFixo);
        deposito.setStatus("PENDENTE");
        deposito.setTransactionId(UUID.randomUUID().toString());

        depositoPendenteRepository.save(deposito);
   
    }//================================ fim solicitarDeposito  =============================================

    
    
    @Transactional
    public void creditarDeposito(Long userId, BigDecimal amount) {
        Usuario user = buscarUsuario(userId);
        BigDecimal saldoAtual = user.getUsdtSaldo() != null ? user.getUsdtSaldo() : BigDecimal.ZERO;
        user.setUsdtSaldo(saldoAtual.add(amount));
        usuarioRepository.save(user);
    }

    private Usuario buscarUsuario(Long userId) {
        return usuarioRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    @Scheduled(fixedRate = 10_000) // Executa a cada 10 segundos (evita sobrecarga)
    @Transactional
    public void verificarDepositosPendentes() {
        List<DepositoPendente> pendentes = depositoPendenteRepository.findByStatus("PENDENTE");

        LocalDateTime agora = LocalDateTime.now();

        for (DepositoPendente deposito : pendentes) {
            // Se passou mais de 1 hora, rejeita
            if (deposito.getDataSolicitacao().isBefore(agora.minusHours(1))) {
                deposito.setStatus("REJEITADO");
                depositoPendenteRepository.save(deposito);

                criarHistorico(deposito.getUser(), deposito.getValorEsperado(), "REJEITADO");
                continue;
            }

            // Verifica se há depósito na TRON
            Optional<String> txIdConfirmada = verificarDepositoNaTron(deposito);

            if (txIdConfirmada.isPresent()) {
                deposito.setStatus("CONFIRMADO");
                deposito.setTransactionId(txIdConfirmada.get());
                depositoPendenteRepository.save(deposito);

                creditarDeposito(deposito.getUser().getId(), deposito.getValorEsperado());

                criarHistorico(deposito.getUser(), deposito.getValorEsperado(), "CONFIRMADO");
            }
        }
    }

    @Transactional
    public void criarHistorico(Usuario user, BigDecimal valor, String status) {
        DepositoHistorico historico = new DepositoHistorico();
        historico.setUser(user);
        historico.setValor(valor);
        historico.setStatus(status);
        historico.setDataDeposito(LocalDateTime.now());
        historico.setIdTransacao(UUID.randomUUID().toString()); // id único interno
       

        depositoHistoricoRepository.save(historico);
    }


    
    public Optional<String> verificarDepositoNaTron(DepositoPendente deposito) {
        try {
            String endereco = deposito.getEnderecoDeposito();
            BigDecimal valorEsperado = deposito.getValorEsperado();
            String url = "https://api.trongrid.io/v1/accounts/" + enderecoFixo + "/transactions/trc20?limit=20";
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("TRON-PRO-API-KEY", tronApiKey)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JSONObject json = new JSONObject(response.body());
                JSONArray transacoes = json.getJSONArray("data");

                for (int i = 0; i < transacoes.length(); i++) {
                    JSONObject tx = transacoes.getJSONObject(i);

                    String to = tx.getString("to");
                    if (!to.equalsIgnoreCase(endereco)) continue;

                    JSONObject tokenInfo = tx.optJSONObject("token_info");
                    if (tokenInfo == null || !"USDT".equalsIgnoreCase(tokenInfo.optString("symbol"))) continue;

                    BigDecimal valorUSDT = new BigDecimal(tx.getString("value")).divide(BigDecimal.valueOf(1_000_000));
                    if (valorUSDT.compareTo(valorEsperado) >= 0) {
                        return Optional.of(tx.getString("transaction_id")); // ou tx.getString("transaction_id")
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Erro ao verificar depósito na TRON: " + e.getMessage());
        }

        return Optional.empty();
    }


  
    

}
