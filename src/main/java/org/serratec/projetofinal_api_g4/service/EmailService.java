package org.serratec.projetofinal_api_g4.service;

<<<<<<< HEAD
import org.serratec.projetofinal_api_g4.dto.EmailDTO;
=======
import org.springframework.beans.factory.annotation.Value;
>>>>>>> origin/Teste
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
<<<<<<< HEAD
    
    private JavaMailSender mailSender;

    public void enviarEmailConfirmacao(EmailDTO emailDTO) {
       SimpleMailMessage mensagem = new SimpleMailMessage();
        
       mensagem.setTo(emailDTO.getDestinatario());
       mensagem.setSubject(emailDTO.getAssunto());
       mensagem.setText(emailDTO.getMensagem());

       try {
        mailSender.send(mensagem);
        System.out.println("E-mail enviado com sucesso para: " + emailDTO.getDestinatario());
       } catch (Exception e) {
        System.err.println("Erro ao enviar e-mail: " + e.getMessage());
        throw new RuntimeException("Erro ao enviar e-mail", e);      
         }
        
    }  
}

=======

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
>>>>>>> origin/Teste
