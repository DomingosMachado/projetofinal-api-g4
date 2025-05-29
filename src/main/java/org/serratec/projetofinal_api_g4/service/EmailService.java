package org.serratec.projetofinal_api_g4.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String from;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarEmailConfirmacao(String email, String nome) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(email);
        message.setSubject("Confirmação de Cadastro");
        message.setText("Olá " + nome + ", \n\nSeu cadastro foi realizado com sucesso!");
        
        mailSender.send(message);
    }

    public void enviarEmailAtualizacao(String email, String nome) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(email);
        message.setSubject("Atualização de Cadastro");
        message.setText("Olá " + nome + ", \n\nSeus dados foram atualizados com sucesso!");
        
        mailSender.send(message);
    }
}
