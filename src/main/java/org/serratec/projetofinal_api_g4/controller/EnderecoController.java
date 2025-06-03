package org.serratec.projetofinal_api_g4.controller;

import org.serratec.projetofinal_api_g4.dto.EnderecoDTO;
import org.serratec.projetofinal_api_g4.service.EnderecoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/enderecos")
@PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN', 'VENDEDOR')")
public class EnderecoController {

    @Autowired
    private EnderecoService enderecoService;

    public EnderecoController(EnderecoService enderecoService) {
        this.enderecoService = enderecoService;
    }

    @GetMapping("/cep/{cep}")
    public ResponseEntity<EnderecoDTO> buscarPorCep(@PathVariable String cep) {
        try {
            EnderecoDTO endereco = enderecoService.buscar(cep);
            return ResponseEntity.ok(endereco);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/cliente/{clienteId}")
    public ResponseEntity<EnderecoDTO> inserirEndereco(
            @PathVariable Long clienteId,
            @Valid @RequestBody EnderecoDTO enderecoDTO) {
        try {
            EnderecoDTO enderecoSalvo = enderecoService.inserir(enderecoDTO.toEntity(), clienteId);
            return ResponseEntity.status(HttpStatus.CREATED).body(enderecoSalvo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<EnderecoDTO> atualizarEndereco(
            @PathVariable Long id,
            @Valid @RequestBody EnderecoDTO enderecoDTO) {
        try {
            EnderecoDTO enderecoAtualizado = enderecoService.atualizar(id, enderecoDTO.toEntity());
            return ResponseEntity.ok(enderecoAtualizado);
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("não encontrado")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarEndereco(@PathVariable Long id) {
        try {
            enderecoService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
