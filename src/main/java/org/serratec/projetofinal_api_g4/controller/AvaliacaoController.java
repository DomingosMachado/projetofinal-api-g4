package org.serratec.projetofinal_api_g4.controller;

import java.util.List;

import org.serratec.projetofinal_api_g4.dto.AvaliacaoDTO;
import org.serratec.projetofinal_api_g4.dto.AvaliacaoRequestDTO;
import org.serratec.projetofinal_api_g4.service.AvaliacaoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/avaliacoes")
@Tag(name = "Avaliações", description = "API de avaliações de produtos")
@Validated
@RequiredArgsConstructor
public class AvaliacaoController {

    private static final Logger logger = LoggerFactory.getLogger(AvaliacaoController.class);

    private final AvaliacaoService avaliacaoService;

    @Operation(summary = "Criar nova avaliação")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Avaliação criada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou produto/cliente não encontrado")
    })
    @PreAuthorize("hasRole('CLIENTE')")
    @PostMapping
    public ResponseEntity<AvaliacaoDTO> criar(@Valid @RequestBody AvaliacaoRequestDTO requestDto) {
        try {
            logger.info("Requisição para criar avaliação: {}", requestDto);
            AvaliacaoDTO avaliacao = avaliacaoService.criarAvaliacao(requestDto);
            logger.info("Avaliação criada com sucesso: {}", avaliacao);
            return ResponseEntity.status(HttpStatus.CREATED).body(avaliacao);
        } catch (ResponseStatusException e) {
            logger.error("Erro ResponseStatusException: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Erro inesperado ao criar avaliação", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Erro ao criar avaliação: " + e.getMessage());
        }
    }

    @Operation(summary = "Listar avaliações de um produto")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Avaliações retornadas com sucesso"),
        @ApiResponse(responseCode = "204", description = "Nenhuma avaliação encontrada"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'ESTOQUISTA', 'CLIENTE')")
    @GetMapping("/produto/{idProduto}")
    public ResponseEntity<List<AvaliacaoDTO>> listarPorProduto(@PathVariable @Positive Long idProduto) {
        try {
            List<AvaliacaoDTO> avaliacoes = avaliacaoService.listarPorProduto(idProduto);
            if (avaliacoes.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(avaliacoes);
        } catch (ResponseStatusException e) {
            logger.error("Erro ao listar avaliações: {}", e.getMessage());
            return ResponseEntity.status(e.getStatusCode()).build();
        }
    }

    @Operation(summary = "Calcular média das avaliações de um produto")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Média calculada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'ESTOQUISTA', 'CLIENTE')")
    @GetMapping("/produto/{idProduto}/media")
    public ResponseEntity<Double> calcularMedia(@PathVariable @Positive Long idProduto) {
        try {
            double media = avaliacaoService.calcularMedia(idProduto);
            return ResponseEntity.ok(media);
        } catch (ResponseStatusException e) {
            logger.error("Erro ao calcular média: {}", e.getMessage());
            return ResponseEntity.status(e.getStatusCode()).build();
        }
    }
}