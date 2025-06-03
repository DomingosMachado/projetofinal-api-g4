package org.serratec.projetofinal_api_g4.controller;

import org.serratec.projetofinal_api_g4.dto.CarrinhoRequestDTO;
import org.serratec.projetofinal_api_g4.dto.CarrinhoResponseDTO;
import org.serratec.projetofinal_api_g4.dto.PedidoDTO;
import org.serratec.projetofinal_api_g4.service.CarrinhoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/carrinhos")
@Tag(name = "Carrinhos", description = "API de gerenciamento de carrinhos")
@RequiredArgsConstructor
public class CarrinhoController {

    private final CarrinhoService carrinhoService;

    @Operation(summary = "Criar ou atualizar o carrinho de um cliente")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Carrinho criado ou atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Cliente ou Produto não encontrado"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos no carrinho")
    })
    @PostMapping("/{clienteId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE') and @securityConfig.isOwner(#clienteId)")
    public ResponseEntity<CarrinhoResponseDTO> criarOuAtualizarCarrinho(
            @PathVariable Long clienteId,
            @Valid @RequestBody CarrinhoRequestDTO dto) {
        CarrinhoResponseDTO carrinho = carrinhoService.criarOuAtualizarCarrinho(clienteId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(carrinho);
    }

    @Operation(summary = "Buscar carrinho de um cliente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Carrinho encontrado"),
        @ApiResponse(responseCode = "404", description = "Carrinho não encontrado")
    })
    @GetMapping("/{clienteId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE') and @securityConfig.isOwner(#clienteId)")
    public ResponseEntity<CarrinhoResponseDTO> buscarPorCliente(@PathVariable Long clienteId) {
        CarrinhoResponseDTO carrinho = carrinhoService.buscarPorCliente(clienteId);
        return ResponseEntity.ok(carrinho);
    }

    @Operation(summary = "Remover um item do carrinho")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Item removido com sucesso"),
        @ApiResponse(responseCode = "404", description = "Carrinho ou item não encontrado"),
        @ApiResponse(responseCode = "400", description = "Item não pertence ao carrinho")
    })
    @DeleteMapping("/{carrinhoId}/item/{itemId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    public ResponseEntity<Void> removerItem(@PathVariable Long carrinhoId, @PathVariable Long itemId) {
        carrinhoService.removerItem(carrinhoId, itemId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Limpar o carrinho de um cliente")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Carrinho limpo com sucesso"),
        @ApiResponse(responseCode = "404", description = "Carrinho não encontrado")
    })
    @DeleteMapping("/{clienteId}/limpar")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE') and @securityConfig.isOwner(#clienteId)")
    public ResponseEntity<Void> limparCarrinho(@PathVariable Long clienteId) {
        carrinhoService.finalizarCarrinho(clienteId); 
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Finalizar o pedido a partir do carrinho do cliente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pedido finalizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Carrinho vazio ou erro na finalização"),
        @ApiResponse(responseCode = "404", description = "Carrinho não encontrado")
    })
    @PostMapping("/{clienteId}/finalizar")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE') and @securityConfig.isOwner(#clienteId)")
    public ResponseEntity<PedidoDTO> finalizarPedido(@PathVariable Long clienteId) {
        PedidoDTO pedidoDTO = carrinhoService.finalizarCarrinho(clienteId);
        return ResponseEntity.ok(pedidoDTO);
    }
}
