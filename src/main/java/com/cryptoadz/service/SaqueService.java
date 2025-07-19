package com.cryptoadz.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.bouncycastle.util.encoders.Hex;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tron.trident.utils.Base58Check;

import com.cryptoadz.dto.SaqueResponseDTO;
import com.cryptoadz.dto.SolicitarSaqueDTO;
import com.cryptoadz.model.SaquePendente;
import com.cryptoadz.model.Usuario;
import com.cryptoadz.repository.SaquePendenteRepository;
import com.cryptoadz.repository.UsuarioRepository;


@Service
public class SaqueService {

     @Value("${tron.private-key}")
	private String privateKeyHex;
	
	 @Value("${saque.taxa.fixa}")
	    private BigDecimal taxaFixa;
	 
    @Value("${tron.api.key}")
    private String tronApiKey;

 

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private SaquePendenteRepository saquePendenteRepository;
    
    @Autowired
    private TronSigner tronSigner;


    @Transactional
    public SaqueResponseDTO solicitarSaque(SolicitarSaqueDTO dto) {
        Long userId = dto.getUserId();
        BigDecimal amount = dto.getValor_solicitado();

        if (amount == null || amount.compareTo(BigDecimal.ONE) < 0) {
            return new SaqueResponseDTO(false, "Valor inválido para saque");
        }

        Usuario user = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        BigDecimal saldo = user.getUsdtSaldo() != null ? user.getUsdtSaldo() : BigDecimal.ZERO;

       
        BigDecimal valorLiquido = amount.subtract(taxaFixa);


        if (saldo.compareTo(amount) < 0) {
            return new SaqueResponseDTO(false, "Saldo insuficiente para saque");
        }

        // Desconta o valor total solicitado do saldo do usuário
        user.setUsdtSaldo(saldo.subtract(amount));
        usuarioRepository.save(user);
        System.out.println("Saldo atualizado: " + user.getUsdtSaldo());

        // Registra o saque com o valor que será transferido ao usuário (valor líquido)
        SaquePendente saque = new SaquePendente();
        saque.setUser(user);
        saque.setValor_solicitado(valorLiquido);
        saque.setEnderecoDestino(dto.getEnderecoDestino());
        saque.setStatus("PENDENTE");
        saque.setDataSolicitacao(LocalDateTime.now());

        saquePendenteRepository.save(saque);

        return new SaqueResponseDTO(true, "Saque solicitado com sucesso. Valor líquido: " + valorLiquido + " USDT (taxa de " + taxaFixa + " USDT aplicada)");
    }

    @Transactional
    public SaqueResponseDTO rejeitarSaque(Long saqueId) {
        try {
            Optional<SaquePendente> optionalSaque = saquePendenteRepository.findById(saqueId);
            if (!optionalSaque.isPresent()) {
                return new SaqueResponseDTO(false, "Saque não encontrado");
            }

            SaquePendente saque = optionalSaque.get();

            if (!"PENDENTE".equals(saque.getStatus())) {
                return new SaqueResponseDTO(false, "Saque não está pendente");
            }

            // Verifica se já se passaram 48 horas desde a solicitação
            LocalDateTime dataSolicitacao = saque.getDataSolicitacao();
            LocalDateTime agora = LocalDateTime.now();

            if (dataSolicitacao == null || dataSolicitacao.plusHours(1).isAfter(agora)) {
                return new SaqueResponseDTO(false, "Ainda não se passaram 1 hora desde a solicitação");
            }

            Usuario user = saque.getUser();
            BigDecimal saldoAtual = user.getUsdtSaldo() != null ? user.getUsdtSaldo() : BigDecimal.ZERO;

           user.setUsdtSaldo(saldoAtual.add(saque.getValor_solicitado()));
            
            
            usuarioRepository.save(user);

            saque.setStatus("REJEITADO");
      

            saquePendenteRepository.save(saque);

            return new SaqueResponseDTO(true, "Saque rejeitado após 48 horas e saldo devolvido");
        } catch (Exception e) {
            e.printStackTrace();
            return new SaqueResponseDTO(false, "Erro ao rejeitar saque: " + e.getMessage());
        }
    }

    
    @Transactional
    @Scheduled(fixedRate = 60000)
    public void rejeitarSaquesExpirados() {
        List<SaquePendente> saquesPendentes = saquePendenteRepository.findByStatus("PENDENTE");

        LocalDateTime agora = LocalDateTime.now();

        for (SaquePendente saque : saquesPendentes) {
            if (saque.getDataSolicitacao() != null &&
                saque.getDataSolicitacao().plusHours(1).isBefore(agora)) {

                Usuario user = saque.getUser();
                BigDecimal saldoAtual = user.getUsdtSaldo() != null ? user.getUsdtSaldo() : BigDecimal.ZERO;
                
                // Taxa de 10%
                BigDecimal valorSaque = saque.getValor_solicitado();
       
                // Soma o valor do saque + taxa
                BigDecimal valorComTaxa = valorSaque.add(taxaFixa);

                // Atualiza o saldo do usuário
               user.setUsdtSaldo(saldoAtual.add(valorComTaxa));
                usuarioRepository.save(user);

               // user.setUsdtSaldo(saldoAtual.add(saque.getValor_solicitado()));
              //  usuarioRepository.save(user);

                saque.setStatus("REJEITADO");
                saquePendenteRepository.save(saque);
            }
        }
    }

    @Transactional
    public SaqueResponseDTO aprovarSaque(Long saqueId) {
        try {
            Optional<SaquePendente> optionalSaque = saquePendenteRepository.findById(saqueId);
            if (!optionalSaque.isPresent()) {
                return new SaqueResponseDTO(false, "Saque não encontrado");
            }
            SaquePendente saque = optionalSaque.get();

            if (!"PENDENTE".equals(saque.getStatus())) {
                return new SaqueResponseDTO(false, "Saque não está pendente");
            }

            saque.setStatus("APROVADO");
            saquePendenteRepository.save(saque);

            return new SaqueResponseDTO(true, "Saque aprovado com sucesso");
        } catch (Exception e) {
            e.printStackTrace();
            return new SaqueResponseDTO(false, "Erro ao aprovar saque: " + e.getMessage());
        }
    }

 

    @Transactional
    @Scheduled(fixedRate = 60000)
    public void processarSaquesPendentes() {
        List<SaquePendente> pendentes = saquePendenteRepository.findByStatus("APROVADO");

        for (SaquePendente saque : pendentes) {
            BigDecimal valorLiquido = saque.getValor_solicitado().subtract(taxaFixa);

            if (valorLiquido.compareTo(BigDecimal.ZERO) <= 0) {
                System.out.println("Valor líquido menor ou igual a zero para o saque ID: " + saque.getId());
                continue;
            }

            String hashTransacao = enviarSaqueParaBlockchain(saque.getEnderecoDestino(), valorLiquido);
            System.out.println("Processando saque ID: " + saque.getId() + ", destino: " + saque.getEnderecoDestino() + ", valor: " + valorLiquido);

            if (hashTransacao != null) {
                atualizarStatusConfirmado(saque.getId(), hashTransacao);
            } else {
                System.err.println("Falha ao enviar saque para blockchain no ID: " + saque.getId());
                // Aqui você pode escolher lançar uma exceção para rollback
                // throw new RuntimeException("Falha no saque para ID: " + saque.getId());
            }
        }
        
    }
    
    
    @Transactional
    public void atualizarStatusConfirmado(Long saqueId, String hash) {
        SaquePendente saque = saquePendenteRepository.findById(saqueId)
            .orElseThrow(() -> new RuntimeException("Saque não encontrado"));
        saque.setStatus("CONFIRMADO");
        saque.setTransactionId(hash);
        saquePendenteRepository.save(saque);
    }

    
    public String enviarSaqueParaBlockchain(String enderecoDestino, BigDecimal valor) {
        try {
            HttpClient client = HttpClient.newHttpClient();

            System.out.println("Iniciando criação da transação de saque...");
            // 1. Criar transação usando triggersmartcontract
            String createUrl = "https://api.trongrid.io/wallet/triggersmartcontract";
            String contractAddressHex = base58ToHex("TXLAQ63Xg1NAzckPwKHvzw7CSEmLMEqcdj");
            String functionSelector = "transfer(address,uint256)";
            String parameter = encodeTrc20Params(enderecoDestino, valor);

            String enderecoFixoBase58 = "TNUV9tWMVYZgmUhuPJtWt3spKSEJUdkgeZ";
            String enderecoFixoHex = base58ToHex(enderecoFixoBase58); // precisa converter para hex

            JSONObject triggerPayload = new JSONObject();
            triggerPayload.put("owner_address", enderecoFixoHex);
            triggerPayload.put("contract_address", contractAddressHex);
            triggerPayload.put("function_selector", functionSelector);
            triggerPayload.put("parameter", parameter);
            triggerPayload.put("fee_limit", 5_000_000L);
            triggerPayload.put("call_value", 0);

            System.out.println("Payload para criação da transação: " + triggerPayload.toString());

            HttpRequest requestCreate = HttpRequest.newBuilder()
                    .uri(URI.create(createUrl))
                    .header("Content-Type", "application/json")
                    .header("TRON-PRO-API-KEY", tronApiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(triggerPayload.toString()))
                    .build();

            HttpResponse<String> responseCreate = client.send(requestCreate, HttpResponse.BodyHandlers.ofString());
            System.out.println("Resposta da criação da transação: " + responseCreate.body());
            JSONObject responseJson = new JSONObject(responseCreate.body());

            if (!responseJson.has("transaction")) {
                System.err.println("Erro ao criar transação: " + responseCreate.body());
                return null;
            }

            JSONObject transaction = responseJson.getJSONObject("transaction");
            String rawDataHex = transaction.getString("raw_data_hex");
            System.out.println("Raw data hex da transação: " + rawDataHex);

            // 2. Assinar transação
            System.out.println("Assinando a transação...");
          
            String assinatura = tronSigner.sign(rawDataHex);
            System.out.println("Assinatura gerada: " + assinatura);
            JSONArray signatureArray = new JSONArray();
            signatureArray.put(assinatura);
            transaction.put("signature", signatureArray);

            // 3. Transmitir transação assinada
            String broadcastUrl = "https://api.trongrid.io/wallet/broadcasttransaction";
            System.out.println("Payload para transmissão da transação: " + transaction.toString());

            HttpRequest requestBroadcast = HttpRequest.newBuilder()
                    .uri(URI.create(broadcastUrl))
                    .header("Content-Type", "application/json")
                    .header("TRON-PRO-API-KEY", tronApiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(transaction.toString()))
                    .build();

            HttpResponse<String> responseBroadcast = client.send(requestBroadcast, HttpResponse.BodyHandlers.ofString());
            System.out.println("Resposta da transmissão da transação: " + responseBroadcast.body());

            JSONObject result = new JSONObject(responseBroadcast.body());

            if (result.optBoolean("result", false)) {
                System.out.println("Transação enviada com sucesso! TXID: " + transaction.getString("txID"));
                return transaction.getString("txID");
            } else {
                System.err.println("Erro ao enviar transação: " + result.toString());
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String base58ToHex(String base58Address) {
        byte[] addressBytes = Base58Check.base58ToBytes(base58Address);
        return Hex.toHexString(addressBytes);
    }

    
    public String encodeTrc20Params(String toAddressBase58, BigDecimal amount) {
        byte[] addressBytes = TronAddressUtils.base58CheckDecode(toAddressBase58);

        // Remove prefixo 0x41 do endereço
        byte[] addressNoPrefix = Arrays.copyOfRange(addressBytes, 1, addressBytes.length);

        String toAddressHex = new BigInteger(1, addressNoPrefix).toString(16);
        toAddressHex = String.format("%064x", new BigInteger(toAddressHex, 16));

        BigInteger amountInt = amount.multiply(BigDecimal.valueOf(1_000_000)).toBigInteger();
        String amountHex = String.format("%064x", amountInt);

        return toAddressHex + amountHex;
    }

 public void executarTransacao(String rawDataHex) {
        String assinatura = tronSigner.sign(rawDataHex);
        System.out.println("Assinatura: " + assinatura);
    }
 
 

    public List<SaquePendente> listarSaquesPendentes() {
        return saquePendenteRepository.findByStatus("PENDENTE");
    }
    
   

	

}
