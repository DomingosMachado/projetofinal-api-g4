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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ProdutoService produtoService;

    @Operation(summary = "Listar todos os pedidos")
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
    @GetMapping("/{id}")
    public ResponseEntity<PedidoDTO> buscarPorId(@PathVariable Long id) {
        Optional<Pedido> pedido = pedidoService.buscarPorId(id);
        if (pedido.isPresent()) {
            return ResponseEntity.ok(new PedidoDTO(pedido.get()));
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Criar novo pedido")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Pedido criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Cliente ou produto não encontrado")
    })
    @PostMapping
    public ResponseEntity<PedidoDTO> criarPedido(@Valid @RequestBody PedidoDTO pedidoDTO) {
        try {
            // Buscar cliente
            ClienteDTO clienteDTO = clienteService.buscarPorId(pedidoDTO.getClienteId());
            Cliente cliente = clienteDTO.toEntity();

            // Criar pedido
            Pedido pedido = new Pedido();
            pedido.setCliente(cliente);
            pedido.setDataPedido(pedidoDTO.getDataPedido());
            pedido.setStatus(pedidoDTO.getStatus());
            pedido.setValorTotal(pedidoDTO.getValorTotal());

            // Processar produtos do pedido
            for (PedidoProdutoDTO produtoDTO : pedidoDTO.getProdutos()) {
                try {
                    // Buscar produto
                    ProdutoDTO produtoDTOEncontrado = produtoService.buscarPorId(produtoDTO.getProdutoId());
                    Produto produto = produtoDTOEncontrado.toEntity();

                    // Criar PedidoProduto
                    PedidoProduto pedidoProduto = new PedidoProduto();
                    pedidoProduto.setProduto(produto);
                    pedidoProduto.setQuantidade(produtoDTO.getQuantidade());
                    pedidoProduto.setPrecoUnitario(produtoDTO.getPrecoUnitario());
                    pedidoProduto.calcularSubtotal();

                    // Adicionar ao pedido
                    pedido.adicionarProduto(pedidoProduto);
                } catch (Exception e) {
                    // Produto não encontrado
                    return ResponseEntity.notFound().build();
                }
            }

            // Salvar pedido
            Pedido pedidoSalvo = pedidoService.salvar(pedido);
            return ResponseEntity.status(HttpStatus.CREATED).body(new PedidoDTO(pedidoSalvo));

        } catch (ClienteNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Atualizar pedido existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pedido atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Pedido não encontrado"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<PedidoDTO> atualizarPedido(@PathVariable Long id, @Valid @RequestBody PedidoDTO pedidoDTO) {
        try {
            ClienteDTO clienteDTO = clienteService.buscarPorId(pedidoDTO.getClienteId());
            Cliente cliente = clienteDTO.toEntity();

            Pedido pedidoAtualizado = new Pedido();
            pedidoAtualizado.setCliente(cliente);
            pedidoAtualizado.setDataPedido(pedidoDTO.getDataPedido());
            pedidoAtualizado.setStatus(pedidoDTO.getStatus());
            pedidoAtualizado.setValorTotal(pedidoDTO.getValorTotal());

            for (PedidoProdutoDTO produtoDTO : pedidoDTO.getProdutos()) {
                try {
                    ProdutoDTO produtoDTOEncontrado = produtoService.buscarPorId(produtoDTO.getProdutoId());
                    Produto produto = produtoDTOEncontrado.toEntity();

                    PedidoProduto pedidoProduto = new PedidoProduto();
                    pedidoProduto.setProduto(produto);
                    pedidoProduto.setQuantidade(produtoDTO.getQuantidade());
                    pedidoProduto.setPrecoUnitario(produtoDTO.getPrecoUnitario());
                    pedidoProduto.calcularSubtotal();

                    pedidoAtualizado.adicionarProduto(pedidoProduto);
                } catch (Exception e) {
                    return ResponseEntity.notFound().build();
                }
            }

            Pedido pedido = pedidoService.atualizar(id, pedidoAtualizado);
            return ResponseEntity.ok(new PedidoDTO(pedido));

        } catch (ClienteNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Deletar pedido")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Pedido deletado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPedido(@PathVariable Long id) {
        try {
            pedidoService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Atualizar status do pedido")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<PedidoDTO> atualizarStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            Optional<Pedido> pedidoOpt = pedidoService.buscarPorId(id);
            if (pedidoOpt.isPresent()) {
                Pedido pedido = pedidoOpt.get();
                pedido.setStatus(org.serratec.projetofinal_api_g4.enums.PedidoStatus.valueOf(status.toUpperCase()));
                Pedido pedidoAtualizado = pedidoService.salvar(pedido);
                return ResponseEntity.ok(new PedidoDTO(pedidoAtualizado));
            }
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}