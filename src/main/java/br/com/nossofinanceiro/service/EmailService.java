package br.com.nossofinanceiro.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value; // Importe esta classe
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.backend-url}") // Injeta a URL do properties
    private String backendUrl;

    @Value("${spring.mail.username}") // Injeta o email do properties
    private String fromEmail;

    public void enviarEmailVerificacao(String paraEmail, String nomeUsuario, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail); // Usa o email injetado
        message.setTo(paraEmail);
        message.setSubject("Bem-vindo ao Nosso Financeiro - Verifique sua conta");

        // Usa a URL do backend configurada no properties
        String urlVerificacao = backendUrl + "/api/auth/verificar-email?token=" + token;

        String texto = "Olá, " + nomeUsuario + ",\n\n"
                // ... (o resto do texto do email continua igual)
                + "Por favor, clique no link abaixo para verificar sua conta:\n"
                + urlVerificacao + "\n\n"
                // ...
                ;

        message.setText(texto);
        mailSender.send(message);
    }

    public void enviarEmailBoasVindas(String paraEmail, String nomeUsuario) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail); // Usa o email injetado
        // ... (o resto do método continua igual)
    }
}