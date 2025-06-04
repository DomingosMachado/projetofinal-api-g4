package org.serratec.projetofinal_api_g4.service.canal;

public class EmailCanalNotificacao implements CanalNotificacao {
    @Override
    public void enviar(String destinatario, String mensagem) {
        System.out.println("Enviando e-mail para " + destinatario + ": " + mensagem);
    }
}

