package org.serratec.projetofinal_api_g4.service;

import org.serratec.projetofinal_api_g4.domain.Cliente;
import org.serratec.projetofinal_api_g4.dto.ClienteDTO;
import org.serratec.projetofinal_api_g4.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;}

    @Transactional
    public ClienteDTO findById(Long id) {
        return clienteRepository.findById(id)
                .map(cliente -> new ClienteDTO(cliente.getId(), cliente.getNome(), cliente.getEmail()))
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado. Id: " + id));
    }

    @Transactional
    public ClienteDTO inserir(ClienteDTO clienteDTO) {
        Cliente cliente = new Cliente();
        cliente.setNome(clienteDTO.getNome());
        cliente.setEmail(clienteDTO.getEmail());
        cliente.setTelefone(clienteDTO.getTelefone());
        cliente.setEndereco(clienteDTO.getEndereco());
        cliente.setCpf(clienteDTO.getCpf());
        cliente = clienteRepository.save(cliente);
        return new ClienteDTO(cliente);
    }



package org.serratec.projetofinal_api_g4.service;

public class ClienteService {

  
}
