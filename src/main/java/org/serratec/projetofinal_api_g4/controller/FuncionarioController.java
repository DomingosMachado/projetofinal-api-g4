package org.serratec.projetofinal_api_g4.controller;

import org.serratec.projetofinal_api_g4.domain.Funcionario;
import org.serratec.projetofinal_api_g4.domain.Produto;
import org.serratec.projetofinal_api_g4.dto.FuncionarioDTO;
import org.serratec.projetofinal_api_g4.dto.ProdutoDTO;
import org.serratec.projetofinal_api_g4.service.FuncionarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/funcionarios")
@PreAuthorize("hasRole('ADMIN')") 
@Tag(name = "Funcionários", description = "API de gerenciamento de funcionários")
public class FuncionarioController {

    @Autowired
    private FuncionarioService funcionarioService;

    @Operation(summary = "Cadastrar novo funcionário")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Funcionário criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    
    @PostMapping
    public ResponseEntity<FuncionarioDTO> cadastrarFuncionario(@Valid @RequestBody FuncionarioDTO funcionarioDTO) {
        try {
            Funcionario funcionario = new Funcionario();
            funcionario.setNome(funcionarioDTO.getNome());
            funcionario.setEmail(funcionarioDTO.getEmail());
            funcionario.setSenha(funcionarioDTO.getSenha());
            funcionario.setTipoFuncionario(funcionarioDTO.getTipoFuncionario());

            Funcionario funcionarioSalvo = funcionarioService.salvarFuncionario(funcionario);

            FuncionarioDTO funcionarioRetorno = new FuncionarioDTO();
            funcionarioRetorno.setId(funcionarioSalvo.getId());
            funcionarioRetorno.setNome(funcionarioSalvo.getNome());
            funcionarioRetorno.setEmail(funcionarioSalvo.getEmail());
            funcionarioRetorno.setSenha(funcionarioSalvo.getSenha()); // Em produção, não retornar senha
            funcionarioRetorno.setTipoFuncionario(funcionarioSalvo.getTipoFuncionario());

            return ResponseEntity.status(HttpStatus.CREATED).body(funcionarioRetorno);

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Funcionário cadastrar produto")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Produto cadastrado com sucesso pelo funcionário"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "403", description = "Funcionário sem permissão para cadastrar produtos"),
        @ApiResponse(responseCode = "404", description = "Funcionário não encontrado")
    })
    @PostMapping("/{funcionarioId}/produtos")
    public ResponseEntity<ProdutoDTO> cadastrarProduto(
            @PathVariable Long funcionarioId, 
            @Valid @RequestBody ProdutoDTO produtoDTO) {
        try {
            Produto produto = produtoDTO.toEntity();

            Produto produtoSalvo = funcionarioService.cadastrarProduto(funcionarioId, produto);

            return ResponseEntity.status(HttpStatus.CREATED).body(new ProdutoDTO(produtoSalvo));

        } catch (RuntimeException e) {
            if (e.getMessage().contains("não encontrado")) {
                return ResponseEntity.notFound().build();
            } else if (e.getMessage().contains("permissão") || e.getMessage().contains("Apenas")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            } else {
                return ResponseEntity.badRequest().build();
            }
        }
    }

    @Operation(summary = "Buscar funcionário por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Funcionário encontrado"),
        @ApiResponse(responseCode = "404", description = "Funcionário não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<FuncionarioDTO> buscarPorId(@PathVariable Long id) {
        FuncionarioDTO funcionario = funcionarioService.buscarPorId(id);
        if (funcionario != null) {
            FuncionarioDTO funcionarioDTO = new FuncionarioDTO();
            funcionarioDTO.setId(funcionario.getId());
            funcionarioDTO.setNome(funcionario.getNome());
            funcionarioDTO.setEmail(funcionario.getEmail());
            funcionarioDTO.setTipoFuncionario(funcionario.getTipoFuncionario());
            // Não retornar senha por questões de segurança
            return ResponseEntity.ok(funcionarioDTO);
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Atualizar funcionário")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Funcionário atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Funcionário não encontrado"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<FuncionarioDTO> atualizar(
            @PathVariable Long id, 
            @Valid @RequestBody FuncionarioDTO funcionarioDTO) {
        try {
            FuncionarioDTO funcionarioAtualizado = funcionarioService.atualizar(id, funcionarioDTO);
            FuncionarioDTO funcionarioRetorno = new FuncionarioDTO();
            funcionarioRetorno.setId(funcionarioAtualizado.getId());
            funcionarioRetorno.setNome(funcionarioAtualizado.getNome());
            funcionarioRetorno.setEmail(funcionarioAtualizado.getEmail());
            funcionarioRetorno.setTipoFuncionario(funcionarioAtualizado.getTipoFuncionario());
            // Não retornar senha por questões de segurança
            return ResponseEntity.ok(funcionarioRetorno);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("não encontrado")) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.badRequest().build();
            }
        }
    
    }

    @Operation(summary = "Deletar funcionário")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Funcionário deletado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Funcionário não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        try {
            funcionarioService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            if (e.getMessage().contains("não encontrado")) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }
       
    }

    @Operation(summary = "Listar todos os funcionários")
    @GetMapping
    public ResponseEntity<?> listarTodos() {
        try {
            return ResponseEntity.ok(funcionarioService.listarTodos());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao listar funcionários"); 

}
    }
}