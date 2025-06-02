package org.serratec.projetofinal_api_g4.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.serratec.projetofinal_api_g4.domain.Pedido;
import org.serratec.projetofinal_api_g4.dto.PedidoDTO;
import org.serratec.projetofinal_api_g4.enums.PedidoStatus;
import org.serratec.projetofinal_api_g4.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/pedidos")
@Tag(name = "Pedidos", description = "API de gerenciamento de pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Operation(summary = "Listar todos os pedidos")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    @GetMapping
    public ResponseEntity<List<PedidoDTO>> listarTodos() {
        List<Pedido> pedidos = pedidoService.listarTodos();
        List<PedidoDTO> pedidosDTO = pedidos.stream()
                .map(PedidoDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(pedidosDTO);
    }

    @Operation(summary = "Buscar pedido por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido encontrado"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    @GetMapping("/{id}")
    public ResponseEntity<PedidoDTO> buscarPorId(@PathVariable Long id) {
        Optional<Pedido> pedido = pedidoService.buscarPorId(id);
        if (pedido.isPresent()) {
            return ResponseEntity.ok(new PedidoDTO(pedido.get()));
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Listar pedidos de um cliente")
    @GetMapping("/cliente/{id}")
    @PreAuthorize("@securityService.isOwner(#id) or hasAnyRole('ADMIN', 'VENDEDOR')")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedidos do cliente encontrados"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    public ResponseEntity<List<PedidoDTO>> listarPedidosDoCliente(@PathVariable Long id) {
        List<Pedido> pedidos = pedidoService.buscarPorClienteId(id);
        List<PedidoDTO> pedidosDTO = pedidos.stream()
                .map(PedidoDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(pedidosDTO);
    }

    @Operation(summary = "Criar novo pedido")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pedido criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Cliente ou produto não encontrado")
    })
    @PreAuthorize("hasRole('CLIENTE') or hasAnyRole('ADMIN', 'VENDEDOR')")
    @PostMapping
    public ResponseEntity<PedidoDTO> criar(@Valid @RequestBody PedidoDTO pedidoDTO) {
        PedidoDTO novoPedido = pedidoService.inserir(pedidoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoPedido);
    }

    @Operation(summary = "Atualizar status do pedido")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado"),
            @ApiResponse(responseCode = "400", description = "Status inválido ou pedido não pode ser atualizado")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<PedidoDTO> atualizarStatus(@PathVariable Long id, @RequestParam String status) {
        Optional<Pedido> pedidoOpt = pedidoService.buscarPorId(id);
        if (!pedidoOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        PedidoStatus novoStatus;
        try {
            novoStatus = PedidoStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }

        Pedido pedido = pedidoOpt.get();
        PedidoStatus statusAtual = pedido.getStatus();
        
        if (!pedidoService.isValidStatusTransition(statusAtual, novoStatus)) {
            return ResponseEntity.badRequest().build();
        }

        pedido.setStatus(novoStatus);
        Pedido pedidoAtualizado = pedidoService.salvar(pedido);
        return ResponseEntity.ok(new PedidoDTO(pedidoAtualizado));
    }

    @Operation(summary = "Deletar pedido")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Pedido deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado"),
            @ApiResponse(responseCode = "400", description = "Pedido não pode ser excluído")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        pedidoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}