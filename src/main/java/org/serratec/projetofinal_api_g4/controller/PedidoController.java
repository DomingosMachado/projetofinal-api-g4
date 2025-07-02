package org.serratec.projetofinal_api_g4.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.serratec.projetofinal_api_g4.domain.Pedido;
import org.serratec.projetofinal_api_g4.dto.PedidoDTO;
import org.serratec.projetofinal_api_g4.enums.PedidoStatus;
import org.serratec.projetofinal_api_g4.service.PedidoService;
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

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @Operation(summary = "Listar todos os pedidos")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de pedidos retornada com sucesso")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    @GetMapping
    public ResponseEntity<List<PedidoDTO>> listarTodos() {
        List<PedidoDTO> pedidosDTO = pedidoService.listarTodos().stream()
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
        return pedidoService.buscarPorId(id)
                .map(pedido -> ResponseEntity.ok(new PedidoDTO(pedido)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Listar pedidos de um cliente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pedidos do cliente encontrados"),
        @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
        @ApiResponse(responseCode = "403", description = "Cliente só pode ver seus próprios pedidos")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR') or (hasRole('CLIENTE') and @securityConfig.isOwner(#id))")
    @GetMapping("/cliente/{id}")
    public ResponseEntity<List<PedidoDTO>> listarPedidosDoCliente(@PathVariable Long id) {
        List<PedidoDTO> pedidosDTO = pedidoService.buscarPorClienteId(id).stream()
                .map(PedidoDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(pedidosDTO);
    }

    @Operation(summary = "Criar novo pedido")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pedido criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Cliente ou produto não encontrado"),
            @ApiResponse(responseCode = "403", description = "Cliente não pode criar pedido para outro cliente")
    })
    @PreAuthorize("(@securityConfig.isOwner(#pedidoDTO.cliente.id) and hasRole('CLIENTE')) or hasAnyRole('ADMIN', 'VENDEDOR')")
    @PostMapping
    public ResponseEntity<PedidoDTO> criar(@Valid @RequestBody PedidoDTO pedidoDTO) {
        PedidoDTO novoPedido = pedidoService.inserir(pedidoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoPedido);
    }

    @Operation(summary = "Cancelar pedido")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido cancelado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado"),
            @ApiResponse(responseCode = "400", description = "Pedido não pode ser cancelado")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR') or hasRole('CLIENTE')")
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<PedidoDTO> cancelarPedido(@PathVariable Long id) {
        PedidoDTO pedidoCancelado = pedidoService.cancelarPedido(id);
        return ResponseEntity.ok(pedidoCancelado);
    }

    @Operation(summary = "Confirmar pedido (apenas Admin/Vendedor)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido confirmado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado"),
            @ApiResponse(responseCode = "400", description = "Pedido não pode ser confirmado")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    @PatchMapping("/{id}/confirmar")
    public ResponseEntity<PedidoDTO> confirmarPedido(@PathVariable Long id) {
        PedidoDTO pedidoConfirmado = pedidoService.atualizarStatus(id, PedidoStatus.CONFIRMADO);
        return ResponseEntity.ok(pedidoConfirmado);
    }

    @Operation(summary = "Enviar pedido (apenas Admin/Vendedor)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido enviado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado"),
            @ApiResponse(responseCode = "400", description = "Pedido não pode ser enviado")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    @PatchMapping("/{id}/enviar")
    public ResponseEntity<PedidoDTO> enviarPedido(@PathVariable Long id) {
        PedidoDTO pedidoEnviado = pedidoService.atualizarStatus(id, PedidoStatus.ENVIADO);
        return ResponseEntity.ok(pedidoEnviado);
    }

    @Operation(summary = "Confirmar entrega do pedido (apenas Cliente)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Entrega confirmada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado"),
            @ApiResponse(responseCode = "400", description = "Pedido não pode ser marcado como entregue"),
            @ApiResponse(responseCode = "403", description = "Apenas o cliente pode confirmar a entrega")
    })
    @PreAuthorize("@securityConfig.isPedidoOwner(#id) and hasRole('CLIENTE')")
    @PatchMapping("/{id}/entregar")
    public ResponseEntity<PedidoDTO> confirmarEntrega(@PathVariable Long id) {
        PedidoDTO pedidoEntregue = pedidoService.atualizarStatus(id, PedidoStatus.ENTREGUE);
        return ResponseEntity.ok(pedidoEntregue);
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
