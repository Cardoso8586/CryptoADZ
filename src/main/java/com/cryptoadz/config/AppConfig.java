package com.cryptoadz.config;

import io.github.cdimascio.dotenv.Dotenv;


public class AppConfig {
    private static final Dotenv dotenv = Dotenv.load(); // carrega o .env

    // Banco de Dados
    public static String getDbUrl() {
        return dotenv.get("DB_URL");
    }

    public static String getDbUsername() {
        return dotenv.get("DB_USERNAME");
    }

    public static String getDbPassword() {
        return dotenv.get("DB_PASSWORD");
    }

    public static String getDbDriver() {
        return dotenv.get("DB_DRIVER");
    }

    // Hibernate
    public static String getHibernateDialect() {
        return dotenv.get("HIBERNATE_DIALECT");
    }

    public static String getDdlAuto() {
        return dotenv.get("DDL_AUTO");
    }

    public static String getValidationQuery() {
        return dotenv.get("VALIDATION_QUERY");
    }

    // Sessão
    public static String getSessionTimeout() {
        return dotenv.get("SESSION_TIMEOUT");
    }

    // Servidor
    public static String getServerPort() {
        return dotenv.get("SERVER_PORT");
    }

    // E-mail
    public static String getMailHost() {
        return dotenv.get("MAIL_HOST");
    }

    public static String getMailPort() {
        return dotenv.get("MAIL_PORT");
    }

    public static String getMailUsername() {
        return dotenv.get("MAIL_USERNAME");
    }

    public static String getMailPassword() {
        return dotenv.get("MAIL_PASSWORD");
    }

    public static String getMailSmtpAuth() {
        return dotenv.get("MAIL_SMTP_AUTH");
    }

    public static String getMailStartTls() {
        return dotenv.get("MAIL_STARTTLS");
    }

    // Turnstile (Cloudflare)
    public static String getTurnstileSecret() {
        return dotenv.get("TURNSTILE_SECRET");
    }

    // Uploads
    public static String getUploadBannerDir() {
        return dotenv.get("UPLOAD_BANNER_DIR");
    }

    // BSC
    public static String getBscRpcUrl() {
        return dotenv.get("BSC_RPC_URL");
    }

    public static String getWalletPrivateKey() {
        return dotenv.get("WALLET_PRIVATE_KEY");
    }

    public static String getUsdtContractAddress() {
        return dotenv.get("USDT_CONTRACT_ADDRESS");
    }
    
    
}

