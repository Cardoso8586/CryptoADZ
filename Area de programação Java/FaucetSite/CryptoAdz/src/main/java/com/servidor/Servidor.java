package com.servidor;


import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

public class Servidor {

    public static void main(String[] args) {
        Properties properties = new Properties();

        try {
            // Carrega o arquivo config.properties
            FileInputStream fis = new FileInputStream("config.properties");
            properties.load(fis);

            // Lê a porta e a configuração de reutilização
            int porta = Integer.parseInt(properties.getProperty("port", "8086"));
            boolean reusePorta = Boolean.parseBoolean(properties.getProperty("auto.release.port", "false"));

            // Cria o ServerSocket
            ServerSocket serverSocket = new ServerSocket();
            if (reusePorta) {
                serverSocket.setReuseAddress(true); // Ativa reutilização
            }

            // Faz o bind da porta
            serverSocket.bind(new InetSocketAddress(porta));
            System.out.println("Servidor rodando na porta: " + porta);

            // Laço principal do servidor
            while (true) {
                Socket cliente = serverSocket.accept();
                System.out.println("Novo cliente conectado: " + cliente.getInetAddress());

                // Aqui você pode passar o cliente para uma thread, se quiser
                cliente.close(); // Exemplo básico: fecha imediatamente
            }

        } catch (IOException e) {
            System.err.println("Erro ao iniciar o servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
