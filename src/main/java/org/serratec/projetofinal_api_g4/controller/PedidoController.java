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
import org.serratec.projetofinal_api_g4.dto.ClienteDTO;
import org.serratec.projetofinal_api_g4.dto.ProdutoDTO;
import org.serratec.projetofinal_api_g4.service.ClienteService;
import org.serratec.projetofinal_api_g4.service.PedidoService;
import org.serratec.projetofinal_api_g4.service.ProdutoService;
import org.serratec.projetofinal_api_g4.exception.ClienteNotFoundException;
import org.serratec.projetofinal_api_g4.exception.EstoqueInsuficienteException;
import org.serratec.projetofinal_api_g4.enums.PedidoStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

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
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        Optional<Pedido> pedido = pedidoService.buscarPorId(id);
        if (pedido.isPresent()) {
            return ResponseEntity.ok(new PedidoDTO(pedido.get()));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Pedido não encontrado com ID: " + id);
    }

    @Operation(summary = "Criar novo pedido")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Pedido criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Cliente ou produto não encontrado")
    })
    @PostMapping
    public ResponseEntity<?> criarPedido(@Valid @RequestBody PedidoDTO pedidoDTO) {
        try {
            // Buscar cliente
            ClienteDTO clienteDTO = clienteService.buscarPorId(pedidoDTO.getClienteId());
            Cliente cliente = clienteDTO.toEntity();

            // Criar pedido
            Pedido pedido = new Pedido();
            pedido.setCliente(cliente);
            pedido.setDataPedido(LocalDateTime.now());
            pedido.setStatus(PedidoStatus.PENDENTE);
            pedido.setValorTotal(java.math.BigDecimal.ZERO);

            // Processar produtos do pedido
            for (PedidoProdutoDTO produtoDTO : pedidoDTO.getProdutos()) {
                try {
                    // Buscar produto
                    ProdutoDTO produtoDTOEncontrado = produtoService.buscarPorId(produtoDTO.getProdutoId());
                    Produto produto = produtoDTOEncontrado.toEntity();

                    PedidoProduto pedidoProduto = new PedidoProduto();
                    pedidoProduto.setProduto(produto);
                    pedidoProduto.setQuantidade(produtoDTO.getQuantidade());
                    
                    if (produtoDTO.getPrecoUnitario() == null || produtoDTO.getPrecoUnitario().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                        pedidoProduto.setPrecoUnitario(produto.getPrecoAtual());
                    } else {
                        pedidoProduto.setPrecoUnitario(produtoDTO.getPrecoUnitario());
                    }

                    if (pedidoProduto.getPrecoUnitario() == null) {
                        pedidoProduto.setPrecoUnitario(produto.getPrecoAtual());
                    }
                    
                    pedidoProduto.calcularSubtotal();

                    pedido.adicionarProduto(pedidoProduto);
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Produto não encontrado: " + produtoDTO.getProdutoId());
                }
            }

            Pedido pedidoSalvo = pedidoService.salvar(pedido);
            return ResponseEntity.status(HttpStatus.CREATED).body(new PedidoDTO(pedidoSalvo));

        } catch (ClienteNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Cliente não encontrado: " + pedidoDTO.getClienteId());
        } catch (EstoqueInsuficienteException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Erro ao processar pedido: " + e.getMessage());
        }
    }

    @Operation(summary = "Atualizar pedido existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pedido atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Pedido não encontrado"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarPedido(@PathVariable Long id, @Valid @RequestBody PedidoDTO pedidoDTO) {
        try {
            if (!pedidoService.buscarPorId(id).isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Pedido não encontrado com ID: " + id);
            }
            
            ClienteDTO clienteDTO = clienteService.buscarPorId(pedidoDTO.getClienteId());
            Cliente cliente = clienteDTO.toEntity();

            Pedido pedidoAtualizado = new Pedido();
            pedidoAtualizado.setCliente(cliente);
            
            pedidoAtualizado.setDataPedido(pedidoDTO.getDataPedido() != null ? 
                pedidoDTO.getDataPedido() : LocalDateTime.now());
            
            Optional<Pedido> pedidoAtual = pedidoService.buscarPorId(id);
            PedidoStatus status = pedidoDTO.getStatus() != null ? 
                pedidoDTO.getStatus() : 
                (pedidoAtual.isPresent() ? pedidoAtual.get().getStatus() : PedidoStatus.PENDENTE);
            
            pedidoAtualizado.setStatus(status);
            
            pedidoAtualizado.setValorTotal(java.math.BigDecimal.ZERO);

            for (PedidoProdutoDTO produtoDTO : pedidoDTO.getProdutos()) {
                try {
                    ProdutoDTO produtoDTOEncontrado = produtoService.buscarPorId(produtoDTO.getProdutoId());
                    Produto produto = produtoDTOEncontrado.toEntity();

                    PedidoProduto pedidoProduto = new PedidoProduto();
                    pedidoProduto.setProduto(produto);
                    pedidoProduto.setQuantidade(produtoDTO.getQuantidade());
                    
                    if (produtoDTO.getPrecoUnitario() == null || produtoDTO.getPrecoUnitario().compareTo(java.math.BigDecimal.ZERO) <= 0) {
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
                        .body("Produto não encontrado: " + produtoDTO.getProdutoId());
                }
            }

            Pedido pedido = pedidoService.atualizar(id, pedidoAtualizado);
            return ResponseEntity.ok(new PedidoDTO(pedido));

        } catch (ClienteNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Cliente não encontrado: " + pedidoDTO.getClienteId());
        } catch (EstoqueInsuficienteException e) {
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
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarPedido(@PathVariable Long id) {
        try {
            pedidoService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (EstoqueInsuficienteException e) {
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
            
        } catch (EstoqueInsuficienteException e) {
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