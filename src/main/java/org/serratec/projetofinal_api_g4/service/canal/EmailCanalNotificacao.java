package org.serratec.projetofinal_api_g4.service.canal;

public class EmailCanalNotificacao implements CanalNotificacao {
    @Override
    public void enviar(String destinatario, String mensagem) {
        // Simulação de envio de e-mail
        System.out.println("E-mail enviado para " + destinatario + ": " + mensagem);
    }
}
