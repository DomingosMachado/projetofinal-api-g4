// EXTRA DOMINGOS MACHADO

package org.serratec.projetofinal_api_g4.controller;

import org.serratec.projetofinal_api_g4.domain.Pedido;
import org.serratec.projetofinal_api_g4.dto.DevolucaoPedidoDTO;
import org.serratec.projetofinal_api_g4.dto.DevolucaoRespostaDTO;
import org.serratec.projetofinal_api_g4.service.DevolucaoPedidoService;
import org.serratec.projetofinal_api_g4.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/devolucoes")
public class DevolucaoController {

    @Autowired
    private DevolucaoPedidoService devolucaoService;

    @Autowired
    private EmailService emailService;

    @PostMapping
    public ResponseEntity<DevolucaoRespostaDTO> solicitarDevolucao(@RequestBody DevolucaoPedidoDTO dto) {
        DevolucaoRespostaDTO devolucao = devolucaoService.solicitarDevolucao(dto.getPedidoId());
        
        try {

            Pedido pedido = devolucaoService.buscarPedidoPorId(dto.getPedidoId());
            String emailCliente = pedido.getCliente().getEmail();
            String nomeCliente = pedido.getCliente().getNome();
            
            emailService.enviarEmailDevolucao(
                emailCliente,
                nomeCliente,
                pedido.getId(),
                devolucao.getStatus().toString()
            );
        } catch (Exception e) {
            System.err.println("Erro ao enviar e-mail de confirmação: " + e.getMessage());
        }
        
        return ResponseEntity.ok(devolucao);
    }
}