package org.serratec.projetofinal_api_g4.controller;

import org.serratec.projetofinal_api_g4.dto.CategoriaDTO;
import org.serratec.projetofinal_api_g4.service.CategoriaService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@Tag(name = "Categorias", description = "API de gerenciamento de categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    // Injeção por construtor (melhor prática)
    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @Operation(summary = "Criar nova categoria")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Categoria criada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'ESTOQUISTA')")
    @PostMapping
    public ResponseEntity<CategoriaDTO> inserir(@Valid @RequestBody CategoriaDTO categoriaDTO) {
        CategoriaDTO novaCategoria = categoriaService.inserir(categoriaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaCategoria);
    }

    @Operation(summary = "Atualizar categoria existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Categoria atualizada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Categoria não encontrada"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'ESTOQUISTA')")
    @PutMapping("/{id}")
    public ResponseEntity<CategoriaDTO> atualizar(@PathVariable Long id, @Valid @RequestBody CategoriaDTO categoriaDTO) {
        CategoriaDTO categoriaAtualizada = categoriaService.atualizar(categoriaDTO, id);
        return ResponseEntity.ok(categoriaAtualizada);
    }

    @Operation(summary = "Listar todas as categorias")
    @PreAuthorize("hasAnyRole('ADMIN', 'ESTOQUISTA', 'VENDEDOR')")
    @GetMapping
    public ResponseEntity<List<CategoriaDTO>> listar() {
        List<CategoriaDTO> categorias = categoriaService.listarTodas();
        return ResponseEntity.ok(categorias);
    }

    @Operation(summary = "Buscar categoria por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Categoria encontrada"),
        @ApiResponse(responseCode = "404", description = "Categoria não encontrada")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'ESTOQUISTA', 'VENDEDOR')")
    @GetMapping("/{id}")
    public ResponseEntity<CategoriaDTO> buscarPorId(@PathVariable Long id) {
        CategoriaDTO categoria = categoriaService.buscarPorId(id);
        return ResponseEntity.ok(categoria);
    }

    @Operation(summary = "Deletar categoria")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Categoria deletada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Categoria não encontrada")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'ESTOQUISTA')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        categoriaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}