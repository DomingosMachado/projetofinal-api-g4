package org.serratec.projetofinal_api_g4.controller;

import java.util.List;

import org.serratec.projetofinal_api_g4.dto.ProdutoDTO;
import org.serratec.projetofinal_api_g4.service.ProdutoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/produtos")
@Tag(name = "Produtos", description = "API de gerenciamento de produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @Operation(summary = "Listar todos os produtos")
    @ApiResponse(responseCode = "200", description = "Lista de produtos retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<ProdutoDTO>> listar() {
        return ResponseEntity.ok(produtoService.listarTodos());
    }

    @Operation(summary = "Buscar produto por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produto encontrado"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProdutoDTO> buscarPorId(@PathVariable Long id) {
        ProdutoDTO produto = produtoService.buscarPorId(id);
        return ResponseEntity.ok(produto);
    }

    @Operation(summary = "Inserir novo produto")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Produto criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'ESTOQUISTA')")
    @PostMapping
    public ResponseEntity<ProdutoDTO> inserir(@Valid @RequestBody ProdutoDTO produtoDTO) {
        ProdutoDTO novo = produtoService.inserir(produtoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(novo);
    }

    @Operation(summary = "Atualizar produto")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'ESTOQUISTA')")
    @PutMapping("/{id}")
    public ResponseEntity<ProdutoDTO> atualizar(@PathVariable Long id, @Valid @RequestBody ProdutoDTO produtoDTO) {
        ProdutoDTO atualizado = produtoService.atualizar(id, produtoDTO);
        return ResponseEntity.ok(atualizado);
    }

    @Operation(summary = "Deletar produto")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Produto deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'ESTOQUISTA')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        produtoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Buscar produtos por categoria")
    @ApiResponse(responseCode = "200", description = "Lista de produtos filtrados por categoria")
    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<List<ProdutoDTO>> buscarPorCategoria(@PathVariable Long categoriaId) {
        List<ProdutoDTO> produtos = produtoService.buscarPorCategoria(categoriaId);
        return ResponseEntity.ok(produtos);
    }

    @Operation(summary = "Buscar produtos por nome")
    @ApiResponse(responseCode = "200", description = "Lista de produtos filtrados por nome")
    @GetMapping("/buscar")
    public ResponseEntity<List<ProdutoDTO>> buscarPorNome(@RequestParam String nome) {
        List<ProdutoDTO> produtos = produtoService.buscarPorNome(nome);
        return ResponseEntity.ok(produtos);
    }
}
