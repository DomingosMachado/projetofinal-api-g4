package org.serratec.projetofinal_api_g4.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.serratec.projetofinal_api_g4.domain.Cliente;
import org.serratec.projetofinal_api_g4.domain.Pedido;
import org.serratec.projetofinal_api_g4.domain.PedidoProduto;
import org.serratec.projetofinal_api_g4.domain.Produto;
import org.serratec.projetofinal_api_g4.dto.PedidoDTO;
import org.serratec.projetofinal_api_g4.dto.PedidoProdutoDTO;
import org.serratec.projetofinal_api_g4.service.ClienteService;
import org.serratec.projetofinal_api_g4.service.PedidoService;
import org.serratec.projetofinal_api_g4.service.ProdutoService;
import org.serratec.projetofinal_api_g4.enums.PedidoStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/pedidos")
@Tag(name = "Pedidos", description = "API de gerenciamento de pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ProdutoService produtoService;

    @Operation(summary = "Listar todos os pedidos")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    @GetMapping
    public ResponseEntity<?> listarTodos() {
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
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        Optional<Pedido> pedido = pedidoService.buscarPorId(id);
        if (pedido.isPresent()) {
            return ResponseEntity.ok(new PedidoDTO(pedido.get()));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Pedido não encontrado com ID: " + id);
    }

    @Operation(summary = "Listar pedidos de um cliente")
    @GetMapping("/cliente/{id}")
    @PreAuthorize("@securityService.isOwner(#id) orhasRole('CLIENTE') or hasRole('ADMIN') or hasRole('VENDEDOR')")
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
    @PreAuthorize("hasRole('CLIENTE')")
    @PostMapping
    public ResponseEntity<PedidoDTO> criar(@Valid @RequestBody PedidoDTO pedidoDTO) {
        try {
            PedidoDTO novoPedido = pedidoService.inserir(pedidoDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoPedido);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Atualizar pedido existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })

    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarPedido(@PathVariable Long id, @Valid @RequestBody PedidoDTO pedidoDTO) {
        try {
            if (!pedidoService.buscarPorId(id).isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Pedido não encontrado com ID: " + id);
            }

            // Usa o ClienteDTO já presente no PedidoDTO
            Cliente cliente = pedidoDTO.getCliente().toNewEntity();

            Pedido pedidoAtualizado = new Pedido();
            pedidoAtualizado.setCliente(cliente);

            pedidoAtualizado.setDataPedido(
                    pedidoDTO.getDataPedido() != null ? pedidoDTO.getDataPedido() : LocalDateTime.now());

            Optional<Pedido> pedidoAtual = pedidoService.buscarPorId(id);
            PedidoStatus status = pedidoDTO.getStatus() != null
                    ? pedidoDTO.getStatus()
                    : (pedidoAtual.isPresent() ? pedidoAtual.get().getStatus() : PedidoStatus.PENDENTE);

            pedidoAtualizado.setStatus(status);
            pedidoAtualizado.setValorTotal(BigDecimal.ZERO);

            for (PedidoProdutoDTO produtoDTO : pedidoDTO.getProdutos()) {
                try {
                    // Usa o ProdutoDTO já presente no PedidoProdutoDTO
                    Produto produto = produtoDTO.getProduto().toEntity();

                    PedidoProduto pedidoProduto = new PedidoProduto();
                    pedidoProduto.setProduto(produto);
                    pedidoProduto.setQuantidade(produtoDTO.getQuantidade());

                    if (produtoDTO.getPrecoUnitario() == null
                            || produtoDTO.getPrecoUnitario().compareTo(BigDecimal.ZERO) <= 0) {
                        pedidoProduto.setPrecoUnitario(produto.getPrecoAtual());
                    } else {
                        pedidoProduto.setPrecoUnitario(produtoDTO.getPrecoUnitario());
                    }

                    if (pedidoProduto.getPrecoUnitario() == null) {
                        pedidoProduto.setPrecoUnitario(produto.getPrecoAtual());
                    }

                    pedidoProduto.calcularSubtotal();

                    pedidoAtualizado.adicionarProduto(pedidoProduto);
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("Produto não encontrado: " + produtoDTO.getProduto().getId());
                }
            }

            Pedido pedido = pedidoService.atualizar(id, pedidoAtualizado);
            return ResponseEntity.ok(new PedidoDTO(pedido));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Cliente não encontrado: " + pedidoDTO.getCliente().getId());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro ao atualizar pedido: " + e.getMessage());
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
    public ResponseEntity<?> deletarPedido(@PathVariable Long id) {
        try {
            pedidoService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (org.springframework.web.server.ResponseStatusException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Pedido não encontrado com ID: " + id);
            } else {
                return ResponseEntity.status(e.getStatusCode())
                        .body(e.getReason());
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro ao excluir pedido: " + e.getMessage());
        }
    }

    @Operation(summary = "Atualizar status do pedido")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado"),
            @ApiResponse(responseCode = "400", description = "Status inválido ou pedido não pode ser atualizado")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> atualizarStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            Optional<Pedido> pedidoOpt = pedidoService.buscarPorId(id);
            if (!pedidoOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Pedido não encontrado com ID: " + id);
            }

            PedidoStatus novoStatus;
            try {
                novoStatus = PedidoStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Status inválido. Valores permitidos: " +
                                java.util.Arrays.stream(PedidoStatus.values())
                                        .map(Enum::name)
                                        .collect(Collectors.joining(", ")));
            }

            Pedido pedido = pedidoOpt.get();

            PedidoStatus statusAtual = pedido.getStatus();
            if (!isValidStatusTransition(statusAtual, novoStatus)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Transição de status inválida: de " + statusAtual + " para " + novoStatus);
            }

            pedido.setStatus(novoStatus);
            Pedido pedidoAtualizado = pedidoService.salvar(pedido);
            return ResponseEntity.ok(new PedidoDTO(pedidoAtualizado));

        } catch (ResponseStatusException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro ao atualizar status: " + e.getMessage());
        }
    }

    private boolean isValidStatusTransition(PedidoStatus atual, PedidoStatus novo) {
        if (atual == PedidoStatus.CANCELADO || atual == PedidoStatus.ENTREGUE) {
            return false;
        }

        if (novo == PedidoStatus.PENDENTE && atual != PedidoStatus.PENDENTE) {
            return false;
        }

        return true;
    }
}