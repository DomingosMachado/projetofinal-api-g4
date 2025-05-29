package org.serratec.projetofinal_api_g4.controller;

import org.serratec.projetofinal_api_g4.dto.EnderecoDTO;
import org.serratec.projetofinal_api_g4.service.EnderecoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/enderecos")
public class EnderecoController {

    @Autowired
    private EnderecoService enderecoService;

    @GetMapping("/cep/{cep}")
    public ResponseEntity<EnderecoDTO> buscarPorCep(@PathVariable String cep) {
        EnderecoDTO endereco = enderecoService.buscar(cep);
        return ResponseEntity.ok(endereco);
    }

    @PostMapping("/cliente/{clienteId}")
    public ResponseEntity<EnderecoDTO> inserirEndereco(
            @PathVariable Long clienteId,
            @RequestBody EnderecoDTO enderecoDTO) {
        
        EnderecoDTO enderecoSalvo = enderecoService.inserir(enderecoDTO.toEntity(), clienteId);
        return ResponseEntity.ok(enderecoSalvo);
    }
}
