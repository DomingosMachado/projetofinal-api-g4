package org.serratec.projetofinal_api_g4.service;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import org.serratec.projetofinal_api_g4.service.canal.CanalNotificacao;

public class NotificacaoService {

    // exemplo, e-mail, SMS
    private List<CanalNotificacao> canais = new ArrayList<>();

    // histórico de notificações enviadas
    private List<Notificacao> historico = new ArrayList<>();

    // classe interna que representa uma notificação enviada
    public static class Notificacao {
        private String destinatario;
        private String mensagem;
        private LocalDateTime dataEnvio;

        public Notificacao(String destinatario, String mensagem, LocalDateTime dataEnvio) {
            this.destinatario = destinatario;
            this.mensagem = mensagem;
            this.dataEnvio = dataEnvio;
        }

        // getters p acessar os dados da notificação
        public String getDestinatario() { return destinatario; }
        public String getMensagem() { return mensagem; }
        public LocalDateTime getDataEnvio() { return dataEnvio; }
    }

    // método p adicionar um canal de notificação à lista pipipipopopo
    public void adicionarCanal(CanalNotificacao canal) {
        canais.add(canal);
    }

    // método p enviar uma notificação p todos os canais cadastrados
    public void enviarNotificacao(String destinatario, String mensagem) {
        for (CanalNotificacao canal : canais) {
            canal.enviar(destinatario, mensagem);
        }
        // após o enviar, registra a notificação no histórico
        historico.add(new Notificacao(destinatario, mensagem, LocalDateTime.now()));
    }

    // método p acessar o histórico de notificações enviadas
    public List<Notificacao> getHistorico() {
        return historico;
    }
}
