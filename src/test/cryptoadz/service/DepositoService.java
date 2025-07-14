package com.cryptoadz.service;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

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
        Usuario user = buscarUsuario(userId);

        DepositoPendente deposito = new DepositoPendente();
        deposito.setUser(user); // usa o objeto Usuario diretamente
        deposito.setValorEsperado(amount);
        deposito.setConfirmado(false);
        deposito.setDataSolicitacao(LocalDateTime.now());
        deposito.setEnderecoDeposito(enderecoFixo);
        deposito.setStatus("PENDENTE");

        depositoPendenteRepository.save(deposito);
    }

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

    @Scheduled(fixedRate = 1000)
    @Transactional
    public void verificarDepositosPendentes() {
        List<DepositoPendente> pendentes = depositoPendenteRepository.findByStatus("PENDENTE"); // buscar por status PENDENTE

        LocalDateTime agora = LocalDateTime.now();

        for (DepositoPendente deposito : pendentes) {
            if (deposito.getDataSolicitacao().isBefore(agora.minusHours(1))) {
                deposito.setStatus("REJEITADO");
                depositoPendenteRepository.save(deposito);

                criarHistorico(deposito.getUser(), deposito.getValorEsperado(), "REJEITADO");
                continue;
            }

            boolean confirmado = verificarDepositoNaBlockchain(deposito);

            if (confirmado) {
                deposito.setStatus("CONFIRMADO");
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

        depositoHistoricoRepository.save(historico);
    }


 // Substituir o método simulado por uma integração real com a API da FaucetPay ou TRON
    private boolean verificarDepositoNaBlockchain(DepositoPendente deposito) {
        try {
            // Dados necessários
            String walletAddress = deposito.getEnderecoDeposito();
            BigDecimal valorEsperado = deposito.getValorEsperado();

            // Endpoint da FaucetPay (exemplo) - você deve substituir pela documentação oficial da FaucetPay
            String apiUrl = "https://faucetpay.io/api/check_transaction"; // Substitua pela URL correta

            // Montar request
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer SUA_CHAVE_API") // substitua por sua chave real
                .POST(HttpRequest.BodyPublishers.ofString("""
                {
                    \"address\": \"" + walletAddress + "\",
                    \"amount\": " + valorEsperado + "
                }
                """))
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                // Parse o JSON (usando biblioteca como Jackson ou org.json)
                String responseBody = response.body();
                JSONObject json = new JSONObject(responseBody);

                boolean encontrado = json.optBoolean("found", false);
                return encontrado;
            }
        } catch (Exception e) {
            System.err.println("Erro ao verificar na blockchain: " + e.getMessage());
        }

        return true;
    }

}
