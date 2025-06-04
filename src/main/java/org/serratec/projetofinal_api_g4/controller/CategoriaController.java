package org.serratec.projetofinal_api_g4.controller;

import org.serratec.projetofinal_api_g4.dto.CategoriaDTO;
import org.serratec.projetofinal_api_g4.service.CategoriaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/categorias")
@Tag(name = "Categorias", description = "API de gerenciamento de categorias")
@RequiredArgsConstructor
@Validated
public class CategoriaController {

    private static final Logger logger = LoggerFactory.getLogger(CategoriaController.class);
    private final CategoriaService categoriaService;

    @Operation(summary = "Criar nova categoria")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Categoria criada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "401", description = "Não autorizado"),
        @ApiResponse(responseCode = "403", description = "Acesso proibido")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'ESTOQUISTA')")
    @PostMapping
    public ResponseEntity<CategoriaDTO> inserir(@Valid @RequestBody CategoriaDTO categoriaDTO) {
        logger.info("Criando categoria: {}", categoriaDTO);
        CategoriaDTO novaCategoria = categoriaService.inserir(categoriaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaCategoria);
    }

    @Operation(summary = "Atualizar categoria existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Categoria atualizada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Categoria não encontrada"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "401", description = "Não autorizado"),
        @ApiResponse(responseCode = "403", description = "Acesso proibido")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'ESTOQUISTA')")
    @PutMapping("/{id}")
    public ResponseEntity<CategoriaDTO> atualizar(
            @PathVariable @Positive Long id,
            @Valid @RequestBody CategoriaDTO categoriaDTO) {
        logger.info("Atualizando categoria ID {}: {}", id, categoriaDTO);
        CategoriaDTO categoriaAtualizada = categoriaService.atualizar(categoriaDTO, id);
        return ResponseEntity.ok(categoriaAtualizada);
    }

    @Operation(summary = "Listar todas as categorias")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Categorias listadas com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autorizado"),
        @ApiResponse(responseCode = "403", description = "Acesso proibido")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'ESTOQUISTA', 'VENDEDOR')")
    @GetMapping
    public ResponseEntity<List<CategoriaDTO>> listar() {
        logger.info("Listando todas as categorias");
        List<CategoriaDTO> categorias = categoriaService.listarTodas();
        return ResponseEntity.ok(categorias);
    }

    @Operation(summary = "Buscar categoria por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Categoria encontrada"),
        @ApiResponse(responseCode = "404", description = "Categoria não encontrada"),
        @ApiResponse(responseCode = "401", description = "Não autorizado"),
        @ApiResponse(responseCode = "403", description = "Acesso proibido")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'ESTOQUISTA', 'VENDEDOR')")
    @GetMapping("/{id}")
    public ResponseEntity<CategoriaDTO> buscarPorId(@PathVariable @Positive Long id) {
        logger.info("Buscando categoria por ID: {}", id);
        CategoriaDTO categoria = categoriaService.buscarPorId(id);
        return ResponseEntity.ok(categoria);
    }

    @Operation(summary = "Deletar categoria")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Categoria deletada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Categoria não encontrada"),
        @ApiResponse(responseCode = "401", description = "Não autorizado"),
        @ApiResponse(responseCode = "403", description = "Acesso proibido")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'ESTOQUISTA')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable @Positive Long id) {
        logger.info("Deletando categoria ID: {}", id);
        categoriaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}