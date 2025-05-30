package org.serratec.projetofinal_api_g4.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;


public class EmailConfig {
    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${mail.signature.ecommerce}") // Nova propriedade para a assinatura do e-commerce
    private String mailSignature;

    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text + "\n\n" + mailSignature); // Adiciona a assinatura
        try {
            mailSender.send(message);
        } catch (Exception e) {
            // Adicionar log aqui para registrar falhas no envio de e-mail
            System.err.println("Erro ao enviar e-mail para " + to + ": " + e.getMessage());
        }
    }
}

