package org.serratec.projetofinal_api_g4.controller;

import java.util.List;

import org.serratec.projetofinal_api_g4.dto.ProdutoDTO;
// import org.serratec.projetofinal_api_g4.exception.ProdutoNotFoundException;
import org.serratec.projetofinal_api_g4.service.ProdutoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    @GetMapping
    public ResponseEntity<List<ProdutoDTO>> listar() {
        return ResponseEntity.ok(produtoService.listarTodos());
    }

    @Operation(summary = "Inserir novo produto")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Produto criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping
    public ResponseEntity<ProdutoDTO> inserir(@Valid @RequestBody ProdutoDTO produtoDTO) {
        ProdutoDTO novo = produtoService.inserir(produtoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(novo);
    }

    // @Operation(summary = "Atualizar produto")
    // @ApiResponses({
    //     @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso"),
    //     @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    // })
    // @PutMapping("/{id}")
    // public ResponseEntity<ProdutoDTO> atualizar(@PathVariable Long id, @Valid @RequestBody ProdutoDTO produtoDTO) {
    //     try {
    //         ProdutoDTO atualizado = produtoService.atualizar(id, produtoDTO);
    //         return ResponseEntity.ok(atualizado);
    //     } catch (ProdutoNotFoundException e) {
    //         return ResponseEntity.notFound().build();
    //     }
    // }

    // @Operation(summary = "Deletar produto")
    // @ApiResponses({
    //     @ApiResponse(responseCode = "204", description = "Produto deletado com sucesso"),
    //     @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    // })
    // @DeleteMapping("/{id}")
    // public ResponseEntity<Void> deletar(@PathVariable Long id) {
    //     try {
    //         produtoService.deletar(id);
    //         return ResponseEntity.noContent().build();
    //     } catch (ProdutoNotFoundException e) {
    //         return ResponseEntity.notFound().build();
    //     }
    // }
}
