package org.serratec.projetofinal_api_g4.service;

import java.util.ArrayList;
import java.util.List;
import org.serratec.projetofinal_api_g4.service.canal.CanalNotificacao;

public class NotificacaoService {
    // Lista de canais de notificação disponíveis
    private List<CanalNotificacao> canais = new ArrayList<>();

    public void adicionarCanal(CanalNotificacao canal) {
        canais.add(canal);
    }

    public void enviarNotificacao(String destinatario, String mensagem) {
        for (CanalNotificacao canal : canais) {
            canal.enviar(destinatario, mensagem);
        }
    }
}
