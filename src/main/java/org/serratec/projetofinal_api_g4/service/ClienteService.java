package org.serratec.projetofinal_api_g4.service;

import java.util.List;
import java.util.stream.Collectors;

import org.serratec.projetofinal_api_g4.domain.Cliente;
import org.serratec.projetofinal_api_g4.dto.ClienteDTO;
import org.serratec.projetofinal_api_g4.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
// @Service
// public class ClienteService {

//     @Autowired
//     private ClienteRepository clienteRepository;

//     @Autowired
//     private EmailService emailService;

//     @Transactional
//     public ClienteDTO buscarPorId(Long id) {
//         Cliente cliente = clienteRepository.findById(id)
//             .orElseThrow(() -> new RuntimeException("Cliente não encontrado. Id: " + id));
//         return new ClienteDTO(cliente);
//     }

//     @Transactional
//     public ClienteDTO inserir(ClienteDTO clienteDTO) {
//         Cliente cliente = converterDtoParaEntity(clienteDTO);
//         cliente = clienteRepository.save(cliente);
//         emailService.enviarEmailConfirmacao(cliente);
//         return new ClienteDTO(cliente);
//     }

//     @Transactional
//     public ClienteDTO atualizar(ClienteDTO clienteDTO, Long id) {
//         Cliente cliente = clienteRepository.findById(id)
//             .orElseThrow(() -> new RuntimeException("Cliente não encontrado. Id: " + id));
//         atualizarClienteComDto(cliente, clienteDTO);
//         cliente = clienteRepository.save(cliente);
//         emailService.enviarEmailConfirmacao(cliente);
//         return new ClienteDTO(cliente);
//     }

//     @Transactional
//     public void deletar(Long id) {
//         if (!clienteRepository.existsById(id)) {
//             throw new RuntimeException("Cliente não encontrado. Id: " + id);
//         }
//         clienteRepository.deleteById(id);
//     }

//     @Transactional
//     public List<ClienteDTO> listarTodos() {
//         return clienteRepository.findAll().stream()
//             .map(ClienteDTO::new)
//             .collect(Collectors.toList());
//     }

//     private Cliente converterDtoParaEntity(ClienteDTO dto) {
//         Cliente c = new Cliente();
//         c.setNome(dto.getNome());
//         c.setEmail(dto.getEmail());
//         c.setTelefone(dto.getTelefone());
//         c.setCpf(dto.getCpf());
//         c.setEndereco(converterEnderecoDto(dto.getEndereco()));
//         return c;
//     }

//     private void atualizarClienteComDto(Cliente cliente, ClienteDTO dto) {
//         cliente.setNome(dto.getNome());
//         cliente.setEmail(dto.getEmail());
//         cliente.setTelefone(dto.getTelefone());
//         cliente.setCpf(dto.getCpf());
//         cliente.setEndereco(converterEnderecoDto(dto.getEndereco()));
//     }

//     private Endereco converterEnderecoDto(EnderecoDTO enderecoDTO) {
//         if (enderecoDTO == null) {
//             return null;
//         }
//         Endereco endereco = new Endereco();
//         endereco.setCep(enderecoDTO.getCep());
//         endereco.setLogradouro(enderecoDTO.getLogradouro());
//         endereco.setBairro(enderecoDTO.getBairro());
//         endereco.setCidade(enderecoDTO.getCidade());
//         endereco.setUf(enderecoDTO.getUf());
//         return endereco;
//     }
// }
