package com.cryptoadz.config;

import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;

public class WalletGenerator {

    public static void main(String[] args) {
        try {
            // Gera o par de chaves
            ECKeyPair keyPair = Keys.createEcKeyPair();

            // Chave privada (formato hexadecimal)
            String privateKey = keyPair.getPrivateKey().toString(16);
            String publicKey = keyPair.getPublicKey().toString(16);

            // Endereço da carteira (derivado da chave pública)
            String address = Keys.getAddress(keyPair);

            System.out.println("🔑 Chave Privada: 0x" + privateKey);
            System.out.println("🔓 Chave Pública: 0x" + publicKey);
            System.out.println("📬 Endereço: 0x" + address);
        } catch (Exception e) {
            System.err.println("Erro ao gerar carteira: " + e.getMessage());
        }
    }
}

//================================================ carteira gerada ================================================================
/**🔑 Chave Privada: 0x2c65c5f9d71786cd78e1faa2c6f2665b22defe25b14c8666c25ef02ecddd3472
🔓 Chave Pública: 0x8f3f9a8cf7c71ff1a553e613c9b7da4acc7b2181cbb070d9f699f15671561806088d17a3cfb8d8dde2bf47cc0442445ada637ab82fb72eb7c825e322da52796f
📬 Endereço: 0xb9779be4984f91532bd7614b4314524dd5296f76
*/