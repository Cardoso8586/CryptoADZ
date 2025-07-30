package com.cryptoadz;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CryptoAdzApplication {

	
    public static void main(String[] args) {
    

        SpringApplication.run(CryptoAdzApplication.class, args);
    }
}


/**   // Carrega o arquivo .env na raiz do projeto (padrão)
Dotenv dotenv = Dotenv.load();

// Setando as variáveis do .env como propriedades do sistema para o Spring ler
dotenv.entries().forEach(entry -> {
    System.setProperty(entry.getKey(), entry.getValue());
});
 */