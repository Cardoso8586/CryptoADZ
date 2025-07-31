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


/** 
 */