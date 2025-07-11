package org.serratec.projetofinal_api_g4.service;

import org.serratec.projetofinal_api_g4.domain.Pedido;
import org.serratec.projetofinal_api_g4.enums.PedidoStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
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
            System.out.println("Email de confirmação enviado para: " + email);
        } catch (MailException e) {
            System.out.println("Erro ao enviar email de confirmação para " + email + ": " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao enviar email de confirmação");
        }
    }

    public void enviarEmailAtualizacao(String email, String nome, String string) {
        try {
            SimpleMailMessage message = criarMensagem(email, nome, "Atualização de Cadastro",
                    "Seus dados foram atualizados com sucesso!");
            mailSender.send(message);
            System.out.println("Email de atualização enviado para: " + email);
        } catch (MailException e) {
            System.out.println("Erro ao enviar email de atualização para " + email + ": " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao enviar email de atualização");
        }
    }

    public void enviarEmailPedido(String email, String nome, String numeroPedido) {
        try {
            SimpleMailMessage message = criarMensagem(email, nome, "Confirmação de Pedido",
                    "Seu pedido número " + numeroPedido + " foi realizado com sucesso!");
            mailSender.send(message);
            System.out.println("Email de pedido enviado para: " + email);
        } catch (MailException e) {
            System.out.println("Erro ao enviar email de pedido para " + email + ": " + e.getMessage());
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

    public void enviarEmailAtualizacaoStatus(String email, String nome, String string, PedidoStatus novoStatus) {
        try {
            SimpleMailMessage message = criarMensagem(email, nome, "Atualização de Status do Pedido",
                    "O status do seu pedido foi atualizado para: " + novoStatus);
            mailSender.send(message);
            System.out.println("Email de atualização de status enviado para: " + email);
        } catch (MailException e) {
            System.out.println("Erro ao enviar email de atualização de status para " + email + ": " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erro ao enviar email de atualização de status");
        }
    }

    public void enviarEmailCancelamento(String email, Pedido pedido) {
        try {
            SimpleMailMessage message = criarMensagem(
                    email,
                    pedido.getCliente().getNome(),
                    "Cancelamento de Pedido",
                    "Olá " + pedido.getCliente().getNome() +
                            ",\n\nSeu pedido realizado em " + pedido.getDataPedido().toLocalDate() +
                            " foi cancelado com sucesso.\n\nSe você não solicitou esse cancelamento, entre em contato conosco.");
            mailSender.send(message);
            System.out.println("Email de cancelamento enviado para: " + email);
        } catch (MailException e) {
            System.out.println("Erro ao enviar email de cancelamento para " + email + ": " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao enviar email de cancelamento");
        }

    }


    public void enviarEmailDevolucao(String email, String nome, Long pedidoId, String status) {
        try {
            SimpleMailMessage message = criarMensagem(email, nome, "Solicitação de Devolução",
                    "Olá " + nome + ",\n\nSua solicitação de devolução do pedido " + pedidoId +
                            " foi recebida com o status: " + status + ".\n\nAguarde nosso contato para mais informações.");
            mailSender.send(message);
            System.out.println("Email de devolução enviado para: " + email);
        } catch (MailException e) {
            System.out.println("Erro ao enviar email de devolução para " + email + ": " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao enviar email de devolução");
        }
    }
}