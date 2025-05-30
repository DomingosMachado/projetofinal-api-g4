package org.serratec.projetofinal_api_g4.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String from;

    public void enviarEmailConfirmacao(String email, String nome) {
        try {
            SimpleMailMessage message = criarMensagem(email, nome, "Confirmação de Cadastro", 
                "Seu cadastro foi realizado com sucesso!");
            mailSender.send(message);
            log.info("Email de confirmação enviado para: {}", email);
        } catch (MailException e) {
            log.error("Erro ao enviar email de confirmação para {}: {}", email, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao enviar email de confirmação");
        }
    }

    public void enviarEmailAtualizacao(String email, String nome) {
        try {
            SimpleMailMessage message = criarMensagem(email, nome, "Atualização de Cadastro", 
                "Seus dados foram atualizados com sucesso!");
            mailSender.send(message);
            log.info("Email de atualização enviado para: {}", email);
        } catch (MailException e) {
            log.error("Erro ao enviar email de atualização para {}: {}", email, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao enviar email de atualização");
        }
    }

    public void enviarEmailPedido(String email, String nome, String numeroPedido) {
        try {
            SimpleMailMessage message = criarMensagem(email, nome, "Confirmação de Pedido", 
                "Seu pedido número " + numeroPedido + " foi realizado com sucesso!");
            mailSender.send(message);
            log.info("Email de pedido enviado para: {}", email);
        } catch (MailException e) {
            log.error("Erro ao enviar email de pedido para {}: {}", email, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao enviar email de pedido");
        }
    }

    private SimpleMailMessage criarMensagem(String email, String nome, String assunto, String mensagem) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(email);
        message.setSubject(assunto);
        message.setText("Olá " + nome + ",\n\n" + mensagem);
        return message;
    }
}
