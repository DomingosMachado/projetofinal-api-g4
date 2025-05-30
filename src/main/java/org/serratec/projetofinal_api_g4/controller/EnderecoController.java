package org.serratec.projetofinal_api_g4.controller;

import org.serratec.projetofinal_api_g4.dto.EnderecoDTO;
import org.serratec.projetofinal_api_g4.service.EnderecoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/enderecos")
@Tag(name = "Endereços", description = "API de gerenciamento de endereços")
public class EnderecoController {

    @Autowired
    private EnderecoService enderecoService;

    @Operation(summary = "Buscar endereço pelo CEP")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Endereço encontrado"),
        @ApiResponse(responseCode = "404", description = "Endereço não encontrado")
    })
    @GetMapping("/cep/{cep}")
    public ResponseEntity<EnderecoDTO> buscarPorCep(@PathVariable String cep) {
        EnderecoDTO endereco = enderecoService.buscar(cep);
        return ResponseEntity.ok(endereco);
    }

    @Operation(summary = "Inserir endereço para um cliente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Endereço cadastrado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou cliente não encontrado")
    })
    @PostMapping("/cliente/{clienteId}")
    public ResponseEntity<EnderecoDTO> inserirEndereco(
            @PathVariable Long clienteId,
            @Valid @RequestBody EnderecoDTO enderecoDTO) {

        EnderecoDTO enderecoSalvo = enderecoService.inserir(enderecoDTO.toEntity(), clienteId);
        return ResponseEntity.ok(enderecoSalvo);
    }
}