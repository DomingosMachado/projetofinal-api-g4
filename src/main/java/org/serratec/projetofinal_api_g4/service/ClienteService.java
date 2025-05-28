package org.serratec.projetofinal_api_g4.service;

import java.util.List;
import java.util.stream.Collectors;

import org.serratec.projetofinal_api_g4.domain.Cliente;
import org.serratec.projetofinal_api_g4.dto.ClienteDTO;
import org.serratec.projetofinal_api_g4.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

   @Transactional
    public ClienteDTO buscarPorId(Long id) {
        Cliente cliente = clienteRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Cliente não encontrado. Id: " + id));
        return new ClienteDTO(cliente);
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

    @Transactional
    public ClienteDTO atualizar(ClienteDTO clienteDTO, Long id) {
        Cliente cliente = clienteRepository.findById(id)
            .orElseThrow(() -> new RuntimeException
            ("Cliente não encontrado. Id: " + id));
        cliente.setNome(clienteDTO.getNome());
        cliente.setEmail(clienteDTO.getEmail());
        cliente.setTelefone(clienteDTO.getTelefone());
        cliente.setEndereco(clienteDTO.getEndereco());
        cliente.setCpf(clienteDTO.getCpf());
        cliente = clienteRepository.save(cliente);
        return new ClienteDTO(cliente);
    }
    @Transactional
    public void deletar(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new RuntimeException("Cliente não encontrado. Id: " + id);
        }
        clienteRepository.deleteById(id);
    }

    @Transactional
    public List<ClienteDTO> listarTodos() {
        List<Cliente> clientes = clienteRepository.findAll();
        return clientes.stream()
            .map(ClienteDTO::new)
            .collect(Collectors.toList());
    }

}