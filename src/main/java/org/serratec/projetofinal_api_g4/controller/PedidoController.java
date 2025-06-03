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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
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
        try {
            Optional<Pedido> pedido = pedidoService.buscarPorId(id);
            if (pedido.isPresent()) {
                return ResponseEntity.ok(new PedidoDTO(pedido.get()));
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Listar pedidos de um cliente")
    @GetMapping("/cliente/{id}")
<<<<<<< HEAD
    @PreAuthorize("(@securityConfig.isOwner(#id) and hasRole('CLIENTE')) or hasAnyRole('ADMIN', 'VENDEDOR')")
=======
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR') or (hasRole('CLIENTE') and @securityConfig.isOwner(#id))")
>>>>>>> origin/DomingosMAchado
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedidos do cliente encontrados"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
            @ApiResponse(responseCode = "403", description = "Cliente só pode ver seus próprios pedidos")
    })
    public ResponseEntity<List<PedidoDTO>> listarPedidosDoCliente(@PathVariable Long id) {
        try {
            List<Pedido> pedidos = pedidoService.buscarPorClienteId(id);
            List<PedidoDTO> pedidosDTO = pedidos.stream()
                    .map(PedidoDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(pedidosDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Criar novo pedido")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pedido criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Cliente ou produto não encontrado"),
            @ApiResponse(responseCode = "403", description = "Cliente não pode criar pedido para outro cliente")
    })
     @PreAuthorize("(@securityConfig.isOwner(#id) and hasRole('CLIENTE')) or hasAnyRole('ADMIN', 'VENDEDOR')")
    @PostMapping
    public ResponseEntity<PedidoDTO> criar(@Valid @RequestBody PedidoDTO pedidoDTO) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            
            // Verificar se é cliente e se está tentando criar pedido para ele mesmo
            if (auth != null && auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_CLIENTE"))) {
                
                // Extrair ID do usuário do token JWT (formato: "id:nome:email")
                String[] userInfo = auth.getName().split(":");
                Long userId = Long.parseLong(userInfo[0]);
                
                // Verificar se o cliente está criando pedido para ele mesmo
                if (pedidoDTO.getCliente() == null || !userId.equals(pedidoDTO.getCliente().getId())) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
            }
            
            PedidoDTO novoPedido = pedidoService.inserir(pedidoDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoPedido);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        } catch (Exception e) {
            e.printStackTrace(); // Para debug
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Cancelar pedido")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido cancelado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado"),
<<<<<<< HEAD
            @ApiResponse(responseCode = "400", description = "Pedido não pode ser cancelado"),
            @ApiResponse(responseCode = "403", description = "Não autorizado para cancelar este pedido")
    })
    @PreAuthorize("(@securityConfig.isPedidoOwner(#id) and hasRole('CLIENTE')) or hasAnyRole('ADMIN', 'VENDEDOR')")
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

=======
            @ApiResponse(responseCode = "400", description = "Pedido não pode ser cancelado")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR') or hasRole('CLIENTE')")
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<PedidoDTO> cancelarPedido(@PathVariable Long id) {
        try {
>>>>>>> origin/DomingosMAchado
            PedidoDTO pedidoCancelado = pedidoService.cancelarPedido(id);
            return ResponseEntity.ok(pedidoCancelado);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
<<<<<<< HEAD
=======
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
>>>>>>> origin/DomingosMAchado
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
    @PreAuthorize("@securityConfig.isPedidoOwner(#id) and hasRole('CLIENTE')")
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
    @DeleteMapping("/{id}/deletar")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        try {
            pedidoService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        }
    }


    // função antiga para atualizar status do pedido, agora substituída por métodos específicos

    // @Operation(summary = "Atualizar status do pedido")
    // @ApiResponses({
    //         @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso"),
    //         @ApiResponse(responseCode = "404", description = "Pedido não encontrado"),
    //         @ApiResponse(responseCode = "400", description = "Status inválido ou pedido não pode ser atualizado")
    // })
    // @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR') or hasRole('CLIENTE')")
    // @PatchMapping("/{id}/status")
    // public ResponseEntity<PedidoDTO> atualizarStatus(@PathVariable Long id, @RequestParam String status) {
    //     try {
    //         Optional<Pedido> pedidoOpt = pedidoService.buscarPorId(id);
    //         if (!pedidoOpt.isPresent()) {
    //             return ResponseEntity.notFound().build();
    //         }
            
    //         PedidoStatus novoStatus;
    //         try {
    //             novoStatus = PedidoStatus.valueOf(status.toUpperCase());
    //         } catch (IllegalArgumentException e) {
    //             return ResponseEntity.badRequest().build();
    //         }
            
    //         Pedido pedido = pedidoOpt.get();
    //         PedidoStatus statusAtual = pedido.getStatus();
            
    //         if (!pedidoService.isValidStatusTransition(statusAtual, novoStatus)) {
    //             return ResponseEntity.badRequest().build();
    //         }
            
    //         pedido.setStatus(novoStatus);
    //         Pedido pedidoAtualizado = pedidoService.salvar(pedido);
    //         return ResponseEntity.ok(new PedidoDTO(pedidoAtualizado));
    //     } catch (Exception e) {
    //         return ResponseEntity.badRequest().build();
    //     }
    // }
}