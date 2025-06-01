package org.serratec.projetofinal_api_g4.controller;

import org.serratec.projetofinal_api_g4.dto.EnderecoDTO;
import org.serratec.projetofinal_api_g4.service.EnderecoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/enderecos")
public class EnderecoController {

    @Autowired
    private EnderecoService enderecoService;

    public EnderecoController(EnderecoService enderecoService) {
        this.enderecoService = enderecoService;
    }

    @GetMapping("/cep/{cep}")
    public ResponseEntity<EnderecoDTO> buscarPorCep(@PathVariable String cep) {
        EnderecoDTO endereco = enderecoService.buscar(cep);
        return ResponseEntity.ok(endereco);
    }

    @PostMapping("/cliente/{clienteId}")
    public ResponseEntity<EnderecoDTO> inserirEndereco(
            @PathVariable Long clienteId,
            @Valid @RequestBody EnderecoDTO enderecoDTO) {
        
        EnderecoDTO enderecoSalvo = enderecoService.inserir(enderecoDTO.toEntity(), clienteId);
        return ResponseEntity.status(HttpStatus.CREATED).body(enderecoSalvo);
    }
}
