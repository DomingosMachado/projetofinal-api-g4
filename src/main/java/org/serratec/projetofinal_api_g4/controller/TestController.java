package org.serratec.projetofinal_api_g4.controller;

import java.util.List;

import org.serratec.projetofinal_api_g4.domain.Pedido;
import org.serratec.projetofinal_api_g4.dto.PedidoDTO;
import org.serratec.projetofinal_api_g4.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private PedidoService pedidoService;

    @GetMapping("/pedidos-raw")
    public ResponseEntity<?> getPedidosRaw() {
        try {
            List<Pedido> pedidos = pedidoService.listarTodos();
            return ResponseEntity.ok("Total de pedidos: " + pedidos.size());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Erro: " + e.getMessage());
        }
    }

    @GetMapping("/pedidos-dto")
    public ResponseEntity<?> getPedidosDTO() {
        try {
            List<Pedido> pedidos = pedidoService.listarTodos();
            System.out.println("Quantidade de pedidos encontrados: " + pedidos.size());
            
            for (int i = 0; i < pedidos.size(); i++) {
                Pedido pedido = pedidos.get(i);
                System.out.println("Processando pedido " + (i+1) + " - ID: " + pedido.getId());
                
                try {
                    PedidoDTO dto = new PedidoDTO(pedido);
                    System.out.println("DTO criado com sucesso para pedido " + pedido.getId());
                } catch (Exception e) {
                    System.err.println("Erro ao criar DTO para pedido " + pedido.getId() + ": " + e.getMessage());
                    e.printStackTrace();
                    return ResponseEntity.status(500).body("Erro no pedido ID " + pedido.getId() + ": " + e.getMessage());
                }
            }
            
            return ResponseEntity.ok("Todos os DTOs criados com sucesso!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Erro: " + e.getMessage());
        }
    }
}
