package org.serratec.projetofinal_api_g4.service;

import java.util.List;
import java.util.stream.Collectors;

import org.serratec.projetofinal_api_g4.domain.Cliente;
import org.serratec.projetofinal_api_g4.domain.Endereco;
import org.serratec.projetofinal_api_g4.dto.ClienteDTO;
import org.serratec.projetofinal_api_g4.dto.ViaCepDTO;
import org.serratec.projetofinal_api_g4.exception.ClienteNotFoundException;
import org.serratec.projetofinal_api_g4.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;
    
    @Autowired
    private ViaCepService viaCepService;
    
    @Autowired
    private EmailService emailService;

    public List<ClienteDTO> listar() {
        return clienteRepository.findAll().stream()
            .map(ClienteDTO::new)
            .collect(Collectors.toList());
    }

    public ClienteDTO buscarPorId(Long id) {
        Cliente cliente = clienteRepository.findById(id)
            .orElseThrow(() -> new ClienteNotFoundException("Cliente não encontrado. Id: " + id));
        return new ClienteDTO(cliente);
    }

    @Transactional
    public ClienteDTO inserir(ClienteDTO clienteDTO) {
        validarCpfUnico(clienteDTO.getCpf());

        // Buscar dados do endereço via CEP
        ViaCepDTO enderecoViaCep = viaCepService.getAddressByCep(clienteDTO.getEndereco().getCep());

        // Criar endereço com dados da API + número informado pelo usuário
        Endereco endereco = new Endereco();
        endereco.setLogradouro(enderecoViaCep.getLogradouro());
        endereco.setNumero(clienteDTO.getEndereco().getNumero());
        endereco.setComplemento(clienteDTO.getEndereco().getComplemento());
        endereco.setBairro(enderecoViaCep.getBairro());
        endereco.setCidade(enderecoViaCep.getLocalidade()); // localidade -> cidade
        endereco.setCep(enderecoViaCep.getCep());
        endereco.setUf(enderecoViaCep.getUf());
        endereco.setIbge(enderecoViaCep.getIbge() != null ? Long.parseLong(enderecoViaCep.getIbge()) : null);

        // Criar cliente
        Cliente cliente = new Cliente();
        cliente.setNome(clienteDTO.getNome());
        cliente.setEmail(clienteDTO.getEmail());
        cliente.setTelefone(clienteDTO.getTelefone());
        cliente.setCpf(clienteDTO.getCpf());
        cliente.setEndereco(endereco);

        cliente = clienteRepository.save(cliente);
        
        // Enviar email de confirmação
        try {
            emailService.enviarEmailConfirmacao(cliente.getEmail(), cliente.getNome());
        } catch (Exception e) {
            // Log do erro, mas não falhar a operação
            System.err.println("Erro ao enviar email de confirmação: " + e.getMessage());
        }

        return new ClienteDTO(cliente);
    }

    @Transactional
    public ClienteDTO atualizar(Long id, ClienteDTO clienteDTO) {
        Cliente cliente = clienteRepository.findById(id)
            .orElseThrow(() -> new ClienteNotFoundException("Cliente não encontrado. Id: " + id));

        // Validar CPF apenas se foi alterado
        if (!cliente.getCpf().equals(clienteDTO.getCpf())) {
            validarCpfUnico(clienteDTO.getCpf());
        }

        // Buscar dados do endereço via CEP
        ViaCepDTO enderecoViaCep = viaCepService.getAddressByCep(clienteDTO.getEndereco().getCep());

        // Atualizar endereço
        Endereco endereco = cliente.getEndereco();
        if (endereco == null) {
            endereco = new Endereco();
        }
        
        endereco.setLogradouro(enderecoViaCep.getLogradouro());
        endereco.setNumero(clienteDTO.getEndereco().getNumero());
        endereco.setComplemento(clienteDTO.getEndereco().getComplemento());
        endereco.setBairro(enderecoViaCep.getBairro());
        endereco.setCidade(enderecoViaCep.getLocalidade());
        endereco.setCep(enderecoViaCep.getCep());
        endereco.setUf(enderecoViaCep.getUf());
        endereco.setIbge(enderecoViaCep.getIbge() != null ? Long.parseLong(enderecoViaCep.getIbge()) : null);

        // Atualizar informações do cliente
        cliente.setNome(clienteDTO.getNome());
        cliente.setEmail(clienteDTO.getEmail());
        cliente.setTelefone(clienteDTO.getTelefone());
        cliente.setCpf(clienteDTO.getCpf());
        cliente.setEndereco(endereco);

        cliente = clienteRepository.save(cliente);
        
        // Enviar email de atualização
        try {
            emailService.enviarEmailAtualizacao(cliente.getEmail(), cliente.getNome());
        } catch (Exception e) {
            // Log do erro, mas não falhar a operação
            System.err.println("Erro ao enviar email de atualização: " + e.getMessage());
        }

        return new ClienteDTO(cliente);
    }

    @Transactional
    public void deletar(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new ClienteNotFoundException("Cliente não encontrado. Id: " + id);
        }
        clienteRepository.deleteById(id);
    }

    private void validarCpfUnico(String cpf) {
        if (clienteRepository.findByCpf(cpf).isPresent()) {
            throw new IllegalArgumentException("CPF já cadastrado");
        }
    }
}