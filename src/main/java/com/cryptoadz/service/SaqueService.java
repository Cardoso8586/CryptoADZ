package com.cryptoadz.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Numeric;

import com.cryptoadz.config.AppConfig;
import com.cryptoadz.model.SaqueHistorico;
import com.cryptoadz.model.Usuario;
import com.cryptoadz.repository.SaqueHistoricoRepository;
import com.cryptoadz.repository.UsuarioRepository;

import jakarta.transaction.Transactional;

@Service
public class SaqueService {

	@Value("${bsc.rpc.url}")
	private String rpcUrl;

	@Value("${wallet.private-key}")
	private String privateKey;

	@Value("${usdt.contract-address}")
	private String usdtContractAddress;

	//private final String rpcUrl = AppConfig.getBscRpcUrl();
	//private final String privateKey = AppConfig.getWalletPrivateKey();
	//private final String usdtContractAddress = AppConfig.getUsdtContractAddress();

	
    private final List<SaqueHistorico> historico = new ArrayList<>(); // Histórico local em memória
    @Autowired
    private SaqueHistoricoRepository saqueHistoricoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private EmailService emailService;
    
    @Transactional
    public String realizarSaque(Long userId, String carteiraDestino, BigDecimal valorUSDT) throws Exception {
        Web3j web3j = Web3j.build(new HttpService(rpcUrl));
        Credentials credentials = Credentials.create(privateKey);
        String carteiraOrigem = credentials.getAddress();

        BigInteger saldoUSDT = consultarSaldoUSDT(carteiraOrigem);

        // Valor a enviar na blockchain = valor solicitado menos taxa fixa 1 USDT
        BigDecimal taxaFixa = BigDecimal.ONE; // 1 USDT fixo
        BigDecimal valorEnviar = valorUSDT.subtract(taxaFixa);
        if (valorEnviar.compareTo(BigDecimal.ZERO) <= 0) {
            throw new Exception("Valor a enviar após taxa fixa deve ser maior que zero.");
        }

        // Converte para BigInteger com 18 casas decimais para enviar na transação
        BigInteger valorSaque = valorEnviar
                .multiply(BigDecimal.TEN.pow(18))
                .setScale(0, RoundingMode.HALF_UP)
                .toBigInteger();

        if (saldoUSDT.compareTo(valorSaque) < 0) {
            throw new Exception("Não foi possível processar sua solicitação. Por favor, tente novamente mais tarde.");
        }


        BigInteger saldoBNB = consultarSaldoBNB(carteiraOrigem);
        BigInteger gasLimit = BigInteger.valueOf(100_000);
        BigInteger gasPrice = web3j.ethGasPrice().send().getGasPrice();
        BigInteger custoGas = gasPrice.multiply(gasLimit);

        if (saldoBNB.compareTo(custoGas) < 0) {
            throw new Exception("Solicitação não concluída. Tente novamente mais tarde.");
        }

        BigInteger nonce = web3j.ethGetTransactionCount(carteiraOrigem, DefaultBlockParameterName.LATEST)
                .send().getTransactionCount();

        Function function = new Function(
                "transfer",
                Arrays.asList(new Address(carteiraDestino), new Uint256(valorSaque)),
                Collections.emptyList()
        );

        String encodedFunction = FunctionEncoder.encode(function);

        RawTransaction rawTransaction = RawTransaction.createTransaction(
                nonce,
                gasPrice,
                gasLimit,
                usdtContractAddress,
                BigInteger.ZERO,
                encodedFunction
        );

        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, 56L, credentials);
        String hexValue = Numeric.toHexString(signedMessage);

        String txHash = web3j.ethSendRawTransaction(hexValue).send().getTransactionHash();

        // Atualiza saldo do usuário no banco normalmente, sem descontar taxa fixa
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new Exception("Usuário não encontrado"));

        BigDecimal novoSaldo = usuario.getUsdtSaldo().subtract(valorUSDT);
        if (novoSaldo.compareTo(BigDecimal.ZERO) < 0) {
            throw new Exception("Saldo insuficiente no banco para completar o saque.");
        }

        usuario.setUsdtSaldo(novoSaldo);
        usuarioRepository.save(usuario);

        
    
        String status = "COMFIRMADO";
        SaqueHistorico registro = new SaqueHistorico(userId, txHash, valorEnviar, txHash,LocalDateTime.now());
        registro.setUserId(userId);
        registro.setCarteiraDestino(carteiraDestino);
        registro.setValorUSDT(valorEnviar);
        registro.setTxHash(txHash);
        registro.setStatus(status);
        registro.setDataHora(LocalDateTime.now());

        saqueHistoricoRepository.save(registro);
      
        
        historico.add(new SaqueHistorico(userId, carteiraDestino, valorEnviar, status, LocalDateTime.now()));

        // Envia e-mail de confirmação
        
        try {
            emailService.enviarConfirmacaoSaque(usuario.getUsername(), usuario.getEmail(), valorUSDT);
        } catch (Exception e) {
            System.err.println("Erro ao enviar e-mail de confirmação de Saque: " + e.getMessage());
        }
        
        return txHash;
    }

  
    // Método para consultar o saldo USDT da carteira, chamando o contrato balanceOf(address)
    public BigInteger consultarSaldoUSDT(String carteira) throws Exception {
        Web3j web3j = Web3j.build(new HttpService(rpcUrl));

        Function function = new Function(
            "balanceOf",
            Arrays.asList(new Address(carteira)),
            Arrays.asList(new TypeReference<Uint256>() {})
        );

        String encodedFunction = FunctionEncoder.encode(function);

        org.web3j.protocol.core.methods.response.EthCall response = web3j.ethCall(
            org.web3j.protocol.core.methods.request.Transaction.createEthCallTransaction(
                carteira, usdtContractAddress, encodedFunction),
            DefaultBlockParameterName.LATEST
        ).send();

        @SuppressWarnings("unchecked")
        List<Type<?>> someTypes = (List<Type<?>>) (List<?>) FunctionReturnDecoder.decode(
            response.getValue(), function.getOutputParameters());

        if (someTypes.isEmpty()) {
            return BigInteger.ZERO;
        }

        Uint256 balance = (Uint256) someTypes.get(0);
        return balance.getValue();
    }
    
    

    // Consulta saldo BNB da carteira (saldo em wei)
    public BigInteger consultarSaldoBNB(String carteira) throws Exception {
        Web3j web3j = Web3j.build(new HttpService(rpcUrl));
        return web3j.ethGetBalance(carteira, DefaultBlockParameterName.LATEST).send().getBalance();
    }

    
    
    // Consulta histórico local (em memória)
    public List<SaqueHistorico> obterHistoricoPorUsuario(Long userId) {
        return historico.stream()
                .filter(s -> s.getUserId().equals(userId))
                .collect(Collectors.toList());
    }
    
  
}
