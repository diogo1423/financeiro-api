package br.com.nossofinanceiro.service; // Pacote sugerido

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    /**
     * Envia o e-mail de verificação de conta.
     * @param paraEmail O e-mail do destinatário.
     * @param nomeUsuario O nome do usuário.
     * @param token O token de verificação.
     */
    public void enviarEmailVerificacao(String paraEmail, String nomeUsuario, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("seu-email@gmail.com"); // Use o mesmo e-mail do application.properties
        message.setTo(paraEmail);
        message.setSubject("Bem-vindo ao Nosso Financeiro - Verifique sua conta");

        // Idealmente, a URL base da API viria de uma configuração
        String urlVerificacao = "http://localhost:8080/api/auth/verificar-email?token=" + token;

        String texto = "Olá, " + nomeUsuario + ",\n\n"
                + "Obrigado por se registrar no Nosso Financeiro!\n\n"
                + "Por favor, clique no link abaixo para verificar sua conta:\n"
                + urlVerificacao + "\n\n"
                + "Se você não se registrou, por favor, ignore este e-mail.\n\n"
                + "Atenciosamente,\n"
                + "Equipe Nosso Financeiro";

        message.setText(texto);
        mailSender.send(message);
    }

    /**
     * Envia o e-mail de boas-vindas após a verificação.
     * @param paraEmail O e-mail do destinatário.
     * @param nomeUsuario O nome do usuário.
     */
    public void enviarEmailBoasVindas(String paraEmail, String nomeUsuario) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("seu-email@gmail.com"); // Use o mesmo e-mail do application.properties
        message.setTo(paraEmail);
        message.setSubject("Sua conta no Nosso Financeiro foi ativada!");

        String texto = "Olá, " + nomeUsuario + ",\n\n"
                + "Sua conta foi verificada com sucesso!\n\n"
                + "Agora você já pode fazer login e começar a organizar suas finanças.\n\n"
                + "Atenciosamente,\n"
                + "Equipe Nosso Financeiro";

        message.setText(texto);
        mailSender.send(message);
    }
}