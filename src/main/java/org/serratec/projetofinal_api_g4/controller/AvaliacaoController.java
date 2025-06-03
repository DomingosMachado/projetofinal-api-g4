package org.serratec.projetofinal_api_g4.controller;

import java.util.List;

import org.serratec.projetofinal_api_g4.dto.AvaliacaoDTO;
import org.serratec.projetofinal_api_g4.dto.AvaliacaoRequestDTO;
import org.serratec.projetofinal_api_g4.service.AvaliacaoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/avaliacoes")
@Tag(name = "Avaliações", description = "API de avaliações de produtos")
public class AvaliacaoController {
     
    private final AvaliacaoService avaliacaoService;

    public AvaliacaoController(AvaliacaoService avaliacaoService) {
        this.avaliacaoService = avaliacaoService;
    }

    @Operation(summary = "Criar nova avaliação")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Avaliação criada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou produto/cliente não encontrado")
    })
    @PreAuthorize("hasRole('CLIENTE')")
    @PostMapping
    public ResponseEntity<AvaliacaoDTO> criar(@Valid @RequestBody AvaliacaoRequestDTO requestDto) {
        try {
            System.out.println("=== REQUISIÇÃO RECEBIDA ===");
            System.out.println("DTO recebido: " + requestDto);
            
            AvaliacaoDTO avaliacao = avaliacaoService.criarAvaliacao(requestDto);
            
            System.out.println("Avaliação criada com sucesso: " + avaliacao);
            return ResponseEntity.status(HttpStatus.CREATED).body(avaliacao);
            
        } catch (ResponseStatusException e) {
            System.out.println("Erro ResponseStatusException: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.out.println("Erro inesperado: " + e.getMessage());
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Erro ao criar avaliação: " + e.getMessage());
        }
    }

    @Operation(summary = "Listar avaliações de um produto")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Avaliações retornadas com sucesso"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'ESTOQUISTA', 'CLIENTE')")
    @GetMapping("/produto/{idProduto}")
    public ResponseEntity<List<AvaliacaoDTO>> listarPorProduto(@PathVariable Long idProduto) {
        try {
            List<AvaliacaoDTO> avaliacoes = avaliacaoService.listarPorProduto(idProduto);
            return ResponseEntity.ok(avaliacoes);
        } catch (ResponseStatusException e) {
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
    public ResponseEntity<Double> calcularMedia(@PathVariable Long idProduto) {
        try {
            double media = avaliacaoService.calcularMedia(idProduto);
            return ResponseEntity.ok(media);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        }
    }
}