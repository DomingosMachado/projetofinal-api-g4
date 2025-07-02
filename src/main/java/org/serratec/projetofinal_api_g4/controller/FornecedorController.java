package org.serratec.projetofinal_api_g4.controller;

import java.util.List;

import org.serratec.projetofinal_api_g4.dto.FornecedorDTO;
import org.serratec.projetofinal_api_g4.service.FornecedorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/fornecedores")
@Tag(name = "Fornecedores", description = "API de gerenciamento de fornecedores")
public class FornecedorController {

    private final FornecedorService fornecedorService;

    public FornecedorController(FornecedorService fornecedorService) {
        this.fornecedorService = fornecedorService;
    }

    @GetMapping
    @Operation(summary = "Listar todos os fornecedores")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    })
    public ResponseEntity<List<FornecedorDTO>> listar() {
        return ResponseEntity.ok(fornecedorService.listarTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar fornecedor por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Fornecedor encontrado"),
        @ApiResponse(responseCode = "404", description = "Fornecedor não encontrado")
    })
    public ResponseEntity<FornecedorDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(fornecedorService.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Inserir novo fornecedor")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Fornecedor criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou CNPJ já cadastrado")
    })
    public ResponseEntity<FornecedorDTO> criar(@Valid @RequestBody FornecedorDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(fornecedorService.inserir(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar fornecedor")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Fornecedor atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Fornecedor não encontrado")
    })
    public ResponseEntity<FornecedorDTO> atualizar(@PathVariable Long id, @Valid @RequestBody FornecedorDTO dto) {
        return ResponseEntity.ok(fornecedorService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar fornecedor")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Fornecedor deletado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Fornecedor não encontrado"),
        @ApiResponse(responseCode = "400", description = "Fornecedor com vínculos não pode ser excluído")
    })
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        fornecedorService.deletar(id);
        return ResponseEntity.noContent().build();
    }
} 