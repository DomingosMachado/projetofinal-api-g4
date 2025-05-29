package org.serratec.projetofinal_api_g4.service;

import org.serratec.projetofinal_api_g4.dto.EmailDTO;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
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

