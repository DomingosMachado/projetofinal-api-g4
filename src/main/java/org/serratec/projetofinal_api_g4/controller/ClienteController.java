package org.serratec.projetofinal_api_g4.controller;

import java.util.List;

import org.serratec.projetofinal_api_g4.dto.ClienteDTO;
import org.serratec.projetofinal_api_g4.service.ClienteService;
import org.serratec.projetofinal_api_g4.service.EmailService;

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
@RequestMapping("/api/clientes")
@Tag(name = "Clientes", description = "API de gerenciamento de clientes")
@Validated
@RequiredArgsConstructor
public class ClienteController {

    private static final Logger logger = LoggerFactory.getLogger(ClienteController.class);

    private final ClienteService clienteService;
    private final EmailService emailService;

    @Operation(summary = "Listar todos os clientes")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de clientes retornada com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autorizado"),
        @ApiResponse(responseCode = "403", description = "Acesso proibido")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    @GetMapping
    public ResponseEntity<List<ClienteDTO>> listar() {
        logger.info("Listando todos os clientes");
        return ResponseEntity.ok(clienteService.listar());
    }

    @Operation(summary = "Buscar cliente por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
        @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
        @ApiResponse(responseCode = "401", description = "Não autorizado"),
        @ApiResponse(responseCode = "403", description = "Acesso proibido")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR') or @securityConfig.isOwner(#id)")
    public ResponseEntity<ClienteDTO> buscar(@PathVariable @Positive Long id) {
        logger.info("Buscando cliente por ID: {}", id);
        ClienteDTO cliente = clienteService.buscarPorId(id);
        return ResponseEntity.ok(cliente);
    }

    @Operation(summary = "Cadastrar novo cliente")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Cliente cadastrado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    @PostMapping
    public ResponseEntity<ClienteDTO> inserir(@Valid @RequestBody ClienteDTO clienteDTO) {
        logger.info("Cadastrando novo cliente: {}", clienteDTO.getNome());
        ClienteDTO clienteCriado = clienteService.inserir(clienteDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteCriado);
    }

    @Operation(summary = "Atualizar cliente existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cliente atualizado"),
        @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "401", description = "Não autorizado"),
        @ApiResponse(responseCode = "403", description = "Acesso proibido")
    })    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR') or (hasRole('CLIENTE') and @securityConfig.isOwner(#id))")
    public ResponseEntity<ClienteDTO> atualizar(
            @Valid @RequestBody ClienteDTO clienteDTO,
            @PathVariable @Positive Long id) {
        logger.info("Atualizando cliente ID {}: {}", id, clienteDTO.getNome());
        ClienteDTO clienteAtualizado = clienteService.atualizar(id, clienteDTO);

        // Melhor evitar passar null — ajuste o método para receber dados úteis
        emailService.enviarEmailAtualizacao(
            clienteAtualizado.getEmail(), 
            clienteAtualizado.getNome(), 
            "Seu cadastro foi atualizado com sucesso."
        );

        return ResponseEntity.ok(clienteAtualizado);
    }

    @Operation(summary = "Remover cliente")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Cliente removido"),
        @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
        @ApiResponse(responseCode = "401", description = "Não autorizado"),
        @ApiResponse(responseCode = "403", description = "Acesso proibido")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<Void> deletar(@PathVariable @Positive Long id) {
        logger.info("Removendo cliente ID: {}", id);
        clienteService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
