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

        String cpfFormatado = formatarCPF(clienteDTO.getCpf());
        
        String telefoneFormatado = formatarTelefone(clienteDTO.getTelefone());
        
        ViaCepDTO enderecoViaCep = viaCepService.getAddressByCep(clienteDTO.getEndereco().getCep());

        Endereco endereco = new Endereco();
        endereco.setLogradouro(enderecoViaCep.getLogradouro());
        endereco.setNumero(clienteDTO.getEndereco().getNumero());
        endereco.setComplemento(clienteDTO.getEndereco().getComplemento());
        endereco.setBairro(enderecoViaCep.getBairro());
        endereco.setCidade(enderecoViaCep.getLocalidade()); // localidade -> cidade
        endereco.setCep(enderecoViaCep.getCep()); // Já formatado pelo ViaCepService
        endereco.setUf(enderecoViaCep.getUf().toUpperCase()); // Garantir que a UF esteja em maiúsculas
        endereco.setIbge(enderecoViaCep.getIbge() != null ? Long.parseLong(enderecoViaCep.getIbge()) : null);

        Cliente cliente = new Cliente();
        cliente.setNome(clienteDTO.getNome());
        cliente.setEmail(clienteDTO.getEmail());
        cliente.setTelefone(telefoneFormatado);
        cliente.setCpf(cpfFormatado);
        cliente.setSenha(clienteDTO.getSenha());
        cliente.setEndereco(endereco);

        cliente = clienteRepository.save(cliente);
        
        try {
            emailService.enviarEmailConfirmacao(cliente.getEmail(), cliente.getNome());
        } catch (Exception e) {
            System.err.println("Erro ao enviar email de confirmação: " + e.getMessage());
        }

        return new ClienteDTO(cliente);
    }

    @Transactional
    public ClienteDTO atualizar(Long id, ClienteDTO clienteDTO) {
        Cliente cliente = clienteRepository.findById(id)
            .orElseThrow(() -> new ClienteNotFoundException("Cliente não encontrado. Id: " + id));

        String cpfFormatado = formatarCPF(clienteDTO.getCpf());
        
        String telefoneFormatado = formatarTelefone(clienteDTO.getTelefone());
        
        if (!cliente.getCpf().equals(cpfFormatado)) {
            validarCpfUnico(cpfFormatado);
        }

        ViaCepDTO enderecoViaCep = viaCepService.getAddressByCep(clienteDTO.getEndereco().getCep());

        Endereco endereco = cliente.getEndereco();
        if (endereco == null) {
            endereco = new Endereco();
        }
        
        endereco.setLogradouro(enderecoViaCep.getLogradouro());
        endereco.setNumero(clienteDTO.getEndereco().getNumero());
        endereco.setComplemento(clienteDTO.getEndereco().getComplemento());
        endereco.setBairro(enderecoViaCep.getBairro());
        endereco.setCidade(enderecoViaCep.getLocalidade());
        endereco.setCep(enderecoViaCep.getCep()); // Já formatado pelo ViaCepService
        endereco.setUf(enderecoViaCep.getUf().toUpperCase()); // Garantir que a UF esteja em maiúsculas
        endereco.setIbge(enderecoViaCep.getIbge() != null ? Long.parseLong(enderecoViaCep.getIbge()) : null);

        cliente.setNome(clienteDTO.getNome());
        cliente.setEmail(clienteDTO.getEmail());
        cliente.setTelefone(telefoneFormatado);
        cliente.setCpf(cpfFormatado);
        if (clienteDTO.getSenha() != null && !clienteDTO.getSenha().isEmpty()) {
            cliente.setSenha(clienteDTO.getSenha());
        }
        cliente.setEndereco(endereco);

        cliente = clienteRepository.save(cliente);
        
        try {
            emailService.enviarEmailAtualizacao(cliente.getEmail(), cliente.getNome());
        } catch (Exception e) {
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
        String cpfNumerico = cpf.replaceAll("\\D", "");
        
        if (clienteRepository.findByCpf(formatarCPF(cpfNumerico)).isPresent()) {
            throw new IllegalArgumentException("CPF já cadastrado");
        }
    }
    

    private String formatarCPF(String cpf) {
        cpf = cpf.replaceAll("\\D", "");
        
        if (cpf.length() != 11) {
            throw new IllegalArgumentException("CPF deve ter 11 dígitos");
        }
        
        return cpf.substring(0, 3) + "." + 
               cpf.substring(3, 6) + "." + 
               cpf.substring(6, 9) + "-" + 
               cpf.substring(9);
    }
    

    private String formatarTelefone(String telefone) {
        telefone = telefone.replaceAll("\\D", "");
        
        if (telefone.length() < 10 || telefone.length() > 11) {
            throw new IllegalArgumentException("Telefone deve ter entre 10 e 11 dígitos");
        }
        
        if (telefone.length() == 10) {
            return "(" + telefone.substring(0, 2) + ") " + 
                   telefone.substring(2, 6) + "-" + 
                   telefone.substring(6);
        } else {
            return "(" + telefone.substring(0, 2) + ") " + 
                   telefone.substring(2, 7) + "-" + 
                   telefone.substring(7);
        }
    }
}