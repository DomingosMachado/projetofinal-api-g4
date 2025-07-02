package org.serratec.projetofinal_api_g4.controller;

import org.serratec.projetofinal_api_g4.dto.CarrinhoRequestDTO;
import org.serratec.projetofinal_api_g4.dto.CarrinhoResponseDTO;
import org.serratec.projetofinal_api_g4.dto.PedidoDTO;
import org.serratec.projetofinal_api_g4.service.CarrinhoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/carrinhos")
@Tag(name = "Carrinhos", description = "API de gerenciamento de carrinhos")
@RequiredArgsConstructor
@Validated
public class CarrinhoController {

    private static final Logger logger = LoggerFactory.getLogger(CarrinhoController.class);
    private final CarrinhoService carrinhoService;

    @Operation(summary = "Criar ou atualizar o carrinho de um cliente")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Carrinho criado ou atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos no carrinho"),
        @ApiResponse(responseCode = "404", description = "Cliente ou Produto não encontrado"),
        @ApiResponse(responseCode = "401", description = "Não autorizado"),
        @ApiResponse(responseCode = "403", description = "Acesso proibido")
    })
    @PostMapping("/{clienteId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE') and @securityConfig.isOwner(#clienteId)")
    public ResponseEntity<CarrinhoResponseDTO> criarOuAtualizarCarrinho(
            @PathVariable @Positive Long clienteId,
            @Valid @RequestBody CarrinhoRequestDTO dto) {
        logger.info("Criando ou atualizando carrinho do cliente ID: {}", clienteId);
        CarrinhoResponseDTO carrinho = carrinhoService.criarOuAtualizarCarrinho(clienteId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(carrinho);
    }

    @Operation(summary = "Buscar carrinho de um cliente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Carrinho encontrado"),
        @ApiResponse(responseCode = "404", description = "Carrinho não encontrado"),
        @ApiResponse(responseCode = "401", description = "Não autorizado"),
        @ApiResponse(responseCode = "403", description = "Acesso proibido")
    })
    @GetMapping("/{clienteId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE') and @securityConfig.isOwner(#clienteId)")
    public ResponseEntity<CarrinhoResponseDTO> buscarPorCliente(@PathVariable @Positive Long clienteId) {
        logger.info("Buscando carrinho do cliente ID: {}", clienteId);
        CarrinhoResponseDTO carrinho = carrinhoService.buscarPorCliente(clienteId);
        return ResponseEntity.ok(carrinho);
    }

    @Operation(summary = "Remover um item do carrinho")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Item removido com sucesso"),
        @ApiResponse(responseCode = "400", description = "Item não pertence ao carrinho"),
        @ApiResponse(responseCode = "404", description = "Carrinho ou item não encontrado"),
        @ApiResponse(responseCode = "401", description = "Não autorizado"),
        @ApiResponse(responseCode = "403", description = "Acesso proibido")
    })
    @DeleteMapping("/{carrinhoId}/item/{itemId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    public ResponseEntity<Void> removerItem(
            @PathVariable @Positive Long carrinhoId,
            @PathVariable @Positive Long itemId) {
        logger.info("Removendo item ID {} do carrinho ID {}", itemId, carrinhoId);
        carrinhoService.removerItem(carrinhoId, itemId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Limpar o carrinho de um cliente")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Carrinho limpo com sucesso"),
        @ApiResponse(responseCode = "404", description = "Carrinho não encontrado"),
        @ApiResponse(responseCode = "401", description = "Não autorizado"),
        @ApiResponse(responseCode = "403", description = "Acesso proibido")
    })
    @DeleteMapping("/{clienteId}/limpar")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE') and @securityConfig.isOwner(#clienteId)")
    public ResponseEntity<Void> limparCarrinho(@PathVariable @Positive Long clienteId) {
        logger.info("Limpando carrinho do cliente ID: {}", clienteId);
        carrinhoService.finalizarCarrinho(clienteId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Finalizar o pedido a partir do carrinho do cliente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pedido finalizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Carrinho vazio ou erro na finalização"),
        @ApiResponse(responseCode = "404", description = "Carrinho não encontrado"),
        @ApiResponse(responseCode = "401", description = "Não autorizado"),
        @ApiResponse(responseCode = "403", description = "Acesso proibido")
    })
    @PostMapping("/{clienteId}/finalizar")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE') and @securityConfig.isOwner(#clienteId)")
    public ResponseEntity<PedidoDTO> finalizarPedido(@PathVariable @Positive Long clienteId) {
        logger.info("Finalizando pedido para cliente ID: {}", clienteId);
        PedidoDTO pedidoDTO = carrinhoService.finalizarCarrinho(clienteId);
        return ResponseEntity.ok(pedidoDTO);
    }
}