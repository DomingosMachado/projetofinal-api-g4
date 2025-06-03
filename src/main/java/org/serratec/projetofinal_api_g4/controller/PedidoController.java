package org.serratec.projetofinal_api_g4.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.serratec.projetofinal_api_g4.config.SecurityConfig;
import org.serratec.projetofinal_api_g4.domain.Pedido;
import org.serratec.projetofinal_api_g4.dto.PedidoDTO;
import org.serratec.projetofinal_api_g4.enums.PedidoStatus;
import org.serratec.projetofinal_api_g4.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;

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
    @PreAuthorize("@securityConfig.isOwner(#id) or hasAnyRole('ADMIN', 'VENDEDOR')")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pedidos do cliente encontrados"),
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    public ResponseEntity<List<PedidoDTO>> listarPedidosDoCliente(@PathVariable Long id) {
        // Se for CLIENTE, verifica se está acessando os próprios pedidos
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            boolean isCliente = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_CLIENTE"));
            
            if (isCliente) {
                Long userId = SecurityConfig.getAuthenticatedUserId();
                if (userId == null || !userId.equals(id)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
            }
        }
        
        List<Pedido> pedidos = pedidoService.buscarPorClienteId(id);
        if (pedidos.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
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

    @Operation(summary = "Cancelar pedido")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido cancelado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado"),
            @ApiResponse(responseCode = "400", description = "Pedido não pode ser cancelado"),
            @ApiResponse(responseCode = "403", description = "Não autorizado para cancelar este pedido")
    })
    @PreAuthorize("(@securityConfig.isOwner(authentication.name, #id) and hasRole('CLIENTE')) or hasAnyRole('ADMIN', 'VENDEDOR')")
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<PedidoDTO> cancelarPedido(@PathVariable Long id) {
        try {
            Optional<Pedido> pedidoOpt = pedidoService.buscarPorId(id);
            if (!pedidoOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            Pedido pedido = pedidoOpt.get();

            // Verificar se o pedido pode ser cancelado
            if (pedido.getStatus() != PedidoStatus.PENDENTE) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Pedido só pode ser cancelado quando está PENDENTE");
            }

            PedidoDTO pedidoCancelado = pedidoService.cancelarPedido(id);
            return ResponseEntity.ok(pedidoCancelado);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        }
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
        try {
            PedidoDTO pedidoConfirmado = pedidoService.atualizarStatus(id, PedidoStatus.CONFIRMADO);
            return ResponseEntity.ok(pedidoConfirmado);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        }
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
        try {
            PedidoDTO pedidoEnviado = pedidoService.atualizarStatus(id, PedidoStatus.ENVIADO);
            return ResponseEntity.ok(pedidoEnviado);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        }
    }

    @Operation(summary = "Confirmar entrega do pedido (apenas Cliente)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Entrega confirmada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado"),
            @ApiResponse(responseCode = "400", description = "Pedido não pode ser marcado como entregue"),
            @ApiResponse(responseCode = "403", description = "Apenas o cliente pode confirmar a entrega")
    })
    @PreAuthorize("@securityConfig.isOwner(authentication.name, #id) and hasRole('CLIENTE')")
    @PatchMapping("/{id}/entregar")
    public ResponseEntity<PedidoDTO> confirmarEntrega(@PathVariable Long id) {
        try {
            PedidoDTO pedidoEntregue = pedidoService.atualizarStatus(id, PedidoStatus.ENTREGUE);
            return ResponseEntity.ok(pedidoEntregue);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        }
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
        try {
            pedidoService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        }
    }
}