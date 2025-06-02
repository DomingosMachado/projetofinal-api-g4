package org.serratec.projetofinal_api_g4.controller;

import java.util.List;

import org.serratec.projetofinal_api_g4.dto.ClienteDTO;
import org.serratec.projetofinal_api_g4.service.ClienteService;
import org.serratec.projetofinal_api_g4.service.EmailService;
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
@RequestMapping("/api/clientes")
@Tag(name = "Clientes", description = "API de gerenciamento de clientes")
public class ClienteController {

    private final ClienteService clienteService;
    private final EmailService emailService;

    public ClienteController(ClienteService clienteService, EmailService emailService) {
        this.clienteService = clienteService;
        this.emailService = emailService;
    }

    @Operation(summary = "Listar todos os clientes")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de clientes retornada com sucesso")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    @GetMapping
    public ResponseEntity<List<ClienteDTO>> listar() {
        return ResponseEntity.ok(clienteService.listar());
    }

    @Operation(summary = "Buscar cliente por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
        @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR') or @securityConfig.isOwner(#id)")
    public ResponseEntity<ClienteDTO> buscar(@PathVariable Long id) {
        ClienteDTO cliente = clienteService.buscarPorId(id);
        return ResponseEntity.ok(cliente);
    }

    @Operation(summary = "Cadastrar novo cliente")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Cliente cadastrado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping
    public ResponseEntity<ClienteDTO> inserir(@Valid @RequestBody ClienteDTO clienteDTO) {
        ClienteDTO clienteCriado = clienteService.inserir(clienteDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteCriado);
    }

    @Operation(summary = "Atualizar cliente existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cliente atualizado"),
        @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR') or (hasRole('CLIENTE') and @securityConfig.isOwner(#id))")
    public ResponseEntity<ClienteDTO> atualizar(
            @Valid @RequestBody ClienteDTO clienteDTO,
            @PathVariable Long id) {
        ClienteDTO clienteAtualizado = clienteService.atualizar(id, clienteDTO);
        
        // Envia e-mail de atualização
        emailService.enviarEmailAtualizacao(
            clienteAtualizado.getEmail(), 
            clienteAtualizado.getNome()
        );
        
        return ResponseEntity.ok(clienteAtualizado);
    }

    @Operation(summary = "Remover cliente")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Cliente removido"),
        @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        clienteService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}