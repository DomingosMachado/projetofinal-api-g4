package org.serratec.projetofinal_api_g4.controller;

import org.serratec.projetofinal_api_g4.service.NotificacaoService;
import org.serratec.projetofinal_api_g4.service.NotificacaoService.Notificacao;
import org.serratec.projetofinal_api_g4.service.canal.EmailCanalNotificacao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/notificacoes")
public class NotificacaoController {

    private final NotificacaoService notificacaoService;

    @Autowired
    public NotificacaoController(NotificacaoService notificacaoService) {
        this.notificacaoService = notificacaoService;
        // adiciona canal de e-mail por padrão
        this.notificacaoService.adicionarCanal(new EmailCanalNotificacao());
    }

    // DTO para receber dados do envio
    public static class NotificacaoRequest {
        public String destinatario;
        public String mensagem;
    }

    @PostMapping("/enviar")
    public String enviarNotificacao(@RequestBody NotificacaoRequest request) {
        notificacaoService.enviarNotificacao(request.destinatario, request.mensagem);
        return "Notificação enviada!";
    }

    @GetMapping("/historico")
    public List<Notificacao> getHistorico() {
        return notificacaoService.getHistorico();
    }
}
