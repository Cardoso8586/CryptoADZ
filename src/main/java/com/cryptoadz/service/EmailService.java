package com.cryptoadz.service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void enviarAvisoDeAlteracaoDeSenha(String nomeUsuario, String destinatarioEmail, String novaSenha) {
        SimpleMailMessage mensagem = new SimpleMailMessage();
        mensagem.setTo(destinatarioEmail);
        mensagem.setSubject("🔐 Alteração de Senha Confirmada");

        mensagem.setText(
            "Olá, " + nomeUsuario + ",\n\n" +
            "Sua senha foi alterada com sucesso!\n\n" +
            "🔑 Nova senha: " + novaSenha + "\n\n" +
            "⚠️ Se você não realizou essa alteração, entre em contato com nosso suporte imediatamente.\n\n" +
            "Atenciosamente,\n" +
            "Equipe CryptoADZ 💼"
        );

        mailSender.send(mensagem);
    }

//=================================================  enviarAvisoDeAlteracaoDeSenha  ========================================================
    
    public void enviarAvisoDeAlteracaoDeNome(String novoNome, String destinatarioEmail, String nomeAntigo) {
        SimpleMailMessage mensagem = new SimpleMailMessage();
        mensagem.setTo(destinatarioEmail);
        mensagem.setSubject("Seu nome de usuário foi alterado com sucesso!");

        mensagem.setText(
            "Olá, " + novoNome + ",\n\n" +
            "Informamos que o seu nome de usuário foi alterado recentemente.\n\n" +
            "✉️ Nome anterior: " + nomeAntigo + "\n" +
            "👤 Novo nome: " + novoNome + "\n\n" +
            "Se você não realizou essa alteração, entre em contato com nosso suporte imediatamente.\n\n" +
            "Atenciosamente,\n" +
            "Equipe CryptoADZ ✨"
        );

        mailSender.send(mensagem);
    }

//============================================== enviarConfirmacaoDeposito  ==============================================
    public void enviarConfirmacaoDeposito(String nomeUsuario, String destinatarioEmail, BigDecimal valor) {
        SimpleMailMessage mensagem = new SimpleMailMessage();
        mensagem.setTo(destinatarioEmail);
        mensagem.setSubject("💰 Confirmação de Depósito Recebido");

        mensagem.setText(
            "Olá, " + nomeUsuario + ",\n\n" +
            "💵 Seu depósito de " + valor.setScale(2, RoundingMode.HALF_UP) + " USDT foi registrado com sucesso!\n\n" +
            "✨ O valor está agora disponível em sua carteira.\n" +
            "Agradecemos por utilizar a plataforma CryptoADZ.\n\n" +
            "Se tiver alguma dúvida ou precisar de suporte, não hesite em nos contatar.\n\n" +
            "Atenciosamente,\n🚀 Equipe CryptoADZ"
        );

        mailSender.send(mensagem);
    }

   /**blic void enviarConfirmacaoDeposito(String nomeUsuario, String destinatarioEmail, BigDecimal valor) {
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
    }*/

    
    
    
    //============================================ enviarEmailBoasVindas ===============================================
    
  //============================================ enviarEmailBoasVindas ===============================================
    public void enviarEmailBoasVindas(String nomeUsuario, String destinatarioEmail) {
        SimpleMailMessage mensagem = new SimpleMailMessage();
        mensagem.setTo(destinatarioEmail);
        mensagem.setSubject("🎉 Bem-vindo à CryptoADZ!");

        mensagem.setText(
            "Olá, " + nomeUsuario + ",\n\n" +
            "🌟 Seja muito bem-vindo(a) à plataforma CryptoADZ! \uD83C�\n\n" +
            "✅ Sua conta foi criada com sucesso e você já pode aproveitar todos os recursos incríveis que preparamos para você.\n\n" +
            "❓ Se tiver qualquer dúvida, nossa equipe de suporte está pronta para ajudar.\n\n" +
            "👋 Estamos felizes em tê-lo(a) conosco!\n\n" +
            "Atenciosamente,\n" +
            "Equipe CryptoADZ"
        );

        mailSender.send(mensagem);
    }

    
   /**lic void enviarEmailBoasVindas(String nomeUsuario, String destinatarioEmail) {
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
    }*/
    
//================================================ enviarConfirmacaoSaque ================================================================
    
    
  

    public void enviarConfirmacaoSaque(String username, String email, BigDecimal valorUSDT) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("✅ Confirmação de Saque - CryptoADZ");

            String valorFormatado = valorUSDT.setScale(2, RoundingMode.HALF_UP).toPlainString();

            String html = """
                <div style="font-family: Arial, sans-serif; color: #333; padding: 20px; border: 1px solid #eee;">
                    <h2 style="color: #2dce89;">💸 Saque Confirmado!</h2>
                    <p>Olá, <strong>%s</strong>,</p>
                    <p>Recebemos sua solicitação de saque no valor de:</p>
                    <p style="font-size: 24px; color: #1a73e8; font-weight: bold;">%s USDT</p>
                    <p>⏳ Em breve o valor será transferido para sua carteira cadastrada.</p>

                    <hr style="margin: 20px 0;">
                    <p>🔐 Este e-mail é apenas uma confirmação automática.</p>
                    <p>Obrigado por utilizar nossos serviços! 🚀</p>

                    <p style="margin-top: 30px;">Atenciosamente,<br><strong>Equipe CryptoADZ</strong></p>
                    <img src="https://i.imgur.com/S5g3NnN.png" alt="CryptoADZ Logo" width="120" style="margin-top: 10px;">
                </div>
            """.formatted(username, valorFormatado);

            helper.setText(html, true);
            mailSender.send(message);

        } catch (MessagingException e) {
            System.err.println("Erro ao enviar e-mail de confirmação de saque: " + e.getMessage());
        }
    }

//=================================================  enviarConfirmacaoAnuncio  ===========================================
	

	public void enviarConfirmacaoAnuncioHtml(String username, String email, String titulo, String descricao, String url) {
	    try {
	        MimeMessage message = mailSender.createMimeMessage();
	        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

	        helper.setTo(email);
	        helper.setSubject("📢 Confirmação de Criação de Anúncio - CryptoADZ");

	        String html = """
	            <div style=\"font-family: Arial, sans-serif; color: #333; padding: 20px; border: 1px solid #eee;\">
	                <h2 style=\"color: #2dce89;\">📢 Seu Anúncio Foi Publicado!</h2>
	                <p>Olá, <strong>%s</strong>,</p>
	                <p>O seu anúncio foi criado com sucesso! Aqui estão os detalhes:</p>

	                <p><strong>📝 Título:</strong> %s</p>
	                <p><strong>📄 Descrição:</strong> %s</p>
	                <p><strong>🔗 Link:</strong> <a href=\"%s\" target=\"_blank\">%s</a></p>

	                <hr style=\"margin: 20px 0;\">
	                <p>🚀 Obrigado por utilizar a plataforma <strong>CryptoADZ</strong>.</p>
	                <p style=\"margin-top: 30px;\">Atenciosamente,<br><strong>Equipe CryptoADZ</strong></p>
	                <img src=\"https://i.imgur.com/S5g3NnN.png\" alt=\"CryptoADZ Logo\" width=\"120\" style=\"margin-top: 10px;\">
	            </div>
	        """.formatted(username, titulo, descricao, url, url);

	        helper.setText(html, true);
	        mailSender.send(message);

	    } catch (MessagingException e) {
	        System.err.println("Erro ao enviar e-mail de confirmação de anúncio: " + e.getMessage());
	    }
	}

//=============================================== enviarConfirmacaoBannerHtml ======================================================

	public void enviarConfirmacaoBannerHtml(String username, String email, String titulo, String urlDestino,
            String imagemUrl, LocalDateTime dataExpiracao) {

String dataExpFormatada = dataExpiracao.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

String htmlContent = """
<html>
<body style='font-family: Arial, sans-serif; background-color: #f9f9f9; padding: 20px;'>
<div style='background-color: #ffffff; border-radius: 8px; padding: 20px; max-width: 600px; margin: auto; box-shadow: 0 2px 4px rgba(0,0,0,0.1);'>
<h2 style='color: #2c3e50;'>👋 Olá, %s!</h2>
<p style='font-size: 16px; color: #333;'>Seu banner foi <strong>criado com sucesso</strong> na plataforma <strong>CryptoADZ</strong>!</p>
<hr style='border: none; border-top: 1px solid #eee;'>
<p><strong>📅 Título:</strong> %s</p>
<p><strong>🔗 Link de Destino:</strong> <a href='%s' style='color: #3498db;'>%s</a></p>
<p><strong>📆 Expira em:</strong> %s</p>
<div style='margin-top: 15px;'>
<img src='%s' alt='Banner' style='max-width: 100%%; height: auto; border-radius: 4px;'>
</div>
<p style='margin-top: 20px; font-size: 14px; color: #555;'>
Obrigado por usar a <strong>CryptoADZ</strong>!<br>
Atenciosamente,<br>
Equipe CryptoADZ
</p>
</div>
</body>
</html>
""".formatted(username, titulo, urlDestino, urlDestino, dataExpFormatada, imagemUrl);

MimeMessage mimeMessage = mailSender.createMimeMessage();
try {
MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
helper.setTo(email);
helper.setSubject("✉️ Confirmação de Criação de Banner - CryptoADZ");
helper.setText(htmlContent, true);

mailSender.send(mimeMessage);

} catch (MessagingException e) {
System.err.println("Erro ao enviar e-mail de confirmação de banner: " + e.getMessage());
}



}

	public void enviarConfirmacaoPremio(String username, String email, BigDecimal premio, String receberPremio) {
	    String valorFormatado = premio.setScale(2, RoundingMode.HALF_UP).toPlainString();
	    String assunto = "🎉 Você recebeu sua premiação semanal!";

	    String htmlContent = """
	    <html>
	    <body style='font-family: Arial, sans-serif; background-color: #f9f9f9; padding: 20px;'>
	    <div style='background-color: #ffffff; border-radius: 8px; padding: 20px; max-width: 600px; margin: auto; box-shadow: 0 2px 4px rgba(0,0,0,0.1);'>
	        <h2 style='color: #00cc99;'>Olá, %s!</h2>
	        <p style='font-size: 16px; color: #333;'>Sua dedicação foi reconhecida! 🎯</p>
	        <div style='font-size: 18px; background-color: #e6fff9; padding: 15px; border-radius: 8px; color: #006655; margin-top: 15px;'>
	            %s<br><br>
	            💰 Valor creditado: <strong>%s ADZ Tokens</strong>
	        </div>
	        <p style='font-size: 16px; margin-top: 20px; color: #333;'>⚠️ <strong>ATENÇÃO:</strong> Se você ainda não recebeu sua recompensa, <a href='https://seusite.com/login' style='color: #00cc99; text-decoration: none; font-weight: bold;'>acesse agora mesmo sua conta</a> e resgate seu prêmio antes que expire!</p>
	        <p style='font-size: 15px; margin-top: 20px;'>Continue ativo, assista mais anúncios e aumente suas chances de ganhar ainda mais nas próximas rodadas!</p>
	        <hr style='border: none; border-top: 1px solid #eee; margin: 20px 0;'>
	        <p style='font-size: 13px; color: #999;'>Este e-mail foi enviado automaticamente. Em caso de dúvidas, entre em contato com o suporte.</p>
	        <p style='font-size: 14px; color: #00cc99;'>Equipe CryptoADZ</p>
	    </div>
	    </body>
	    </html>
	    """.formatted(username, receberPremio, valorFormatado);

	    try {
	        MimeMessage mimeMessage = mailSender.createMimeMessage();
	        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
	        helper.setTo(email);
	        helper.setSubject(assunto);
	        helper.setText(htmlContent, true);

	        mailSender.send(mimeMessage);
	    } catch (MessagingException e) {
	        System.err.println("Erro ao enviar e-mail de premiação para " + email + ": " + e.getMessage());
	    }
	}






}
