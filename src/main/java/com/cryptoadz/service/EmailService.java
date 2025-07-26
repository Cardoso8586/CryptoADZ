package com.cryptoadz.service;
import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void enviarAvisoDeAlteracaoDeSenha(String nomeUsuario, String destinatarioEmail, String novaSenha) {
        SimpleMailMessage mensagem = new SimpleMailMessage();
        mensagem.setTo(destinatarioEmail);
        mensagem.setSubject("Sua senha foi alterada");

        mensagem.setText(
            "Olá, " + nomeUsuario + ",\n\n" +
            "Sua senha foi alterada com sucesso.\n\n" +
            "Nova senha: " + novaSenha + "\n\n" +
            "Se você não realizou essa alteração, entre em contato com o suporte imediatamente.\n\n" +
            "Atenciosamente,\nEquipe CryptoADZ"
        );

        mailSender.send(mensagem);
    }
    
//=================================================  enviarAvisoDeAlteracaoDeSenha  ========================================================
    
    public void enviarAvisoDeAlteracaoDeNome(String novoNome, String destinatarioEmail, String nomeAntigo) {
        SimpleMailMessage mensagem = new SimpleMailMessage();
        mensagem.setTo(destinatarioEmail);
        mensagem.setSubject("Seu nome de usuário foi alterado");

        mensagem.setText(
            "Olá, " + novoNome + ",\n\n" +
            "Seu nome de usuário foi alterado com sucesso.\n\n" +
            "Nome anterior: " + nomeAntigo + "\n" +
            "Novo nome: " + novoNome + "\n\n" +
            "Se você não realizou essa alteração, entre em contato com o suporte imediatamente.\n\n" +
            "Atenciosamente,\nEquipe CryptoADZ"
        );

        mailSender.send(mensagem);
    }
//============================================== enviarConfirmacaoDeposito  ==============================================
    
    public void enviarConfirmacaoDeposito(String nomeUsuario, String destinatarioEmail, BigDecimal valor) {
        SimpleMailMessage mensagem = new SimpleMailMessage();
        mensagem.setTo(destinatarioEmail);
        mensagem.setSubject("Confirmação de Depósito Realizado");

        mensagem.setText(
            "Olá, " + nomeUsuario + ",\n\n" +
            "Seu depósito de " + valor + " USDT foi registrado com sucesso.\n\n" +
            "Obrigado por usar nossos serviços.\n\n" +
            "Atenciosamente,\nEquipe CryptoADZ"
        );

        mailSender.send(mensagem);
    }

    //=============================================================================================
    public void enviarEmailBoasVindas(String nomeUsuario, String destinatarioEmail) {
        SimpleMailMessage mensagem = new SimpleMailMessage();
        mensagem.setTo(destinatarioEmail);
        mensagem.setSubject("Bem-vindo à CryptoADZ!");

        mensagem.setText(
            "Olá, " + nomeUsuario + ",\n\n" +
            "Seja muito bem-vindo(a) à plataforma CryptoADZ!\n\n" +
            "Sua conta foi criada com sucesso. Aproveite todos os recursos que preparamos para você.\n\n" +
            "Se tiver qualquer dúvida, entre em contato com nosso suporte.\n\n" +
            "Atenciosamente,\nEquipe CryptoADZ"
        );

        mailSender.send(mensagem);
    }


}
