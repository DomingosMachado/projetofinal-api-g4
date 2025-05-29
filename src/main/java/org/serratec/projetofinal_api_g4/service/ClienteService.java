<<<<<<< HEAD
=======
package org.serratec.projetofinal_api_g4.service;

import java.util.List;
import java.util.stream.Collectors;

import org.serratec.projetofinal_api_g4.domain.Cliente;
import org.serratec.projetofinal_api_g4.domain.Endereco;
import org.serratec.projetofinal_api_g4.dto.ClienteDTO;
import org.serratec.projetofinal_api_g4.exception.ClienteNotFoundException;
import org.serratec.projetofinal_api_g4.repository.ClienteRepository;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ViaCepService viaCepService;
    private final EmailService emailService;

    public ClienteService(ClienteRepository clienteRepository, 
                         ViaCepService viaCepService,
                         EmailService emailService) {
        this.clienteRepository = clienteRepository;
        this.viaCepService = viaCepService;
        this.emailService = emailService;
    }

    public List<ClienteDTO> listar() {
        return clienteRepository.findAll().stream()
            .map(ClienteDTO::new)
            .collect(Collectors.toList());
    }

    @Transactional
    public ClienteDTO buscarPorId(Long id) {
        Cliente cliente = clienteRepository.findById(id)
            .orElseThrow(() -> new ClienteNotFoundException("Cliente não encontrado. Id: " + id));
        return new ClienteDTO(cliente);
    }

    @Transactional
    public ClienteDTO inserir(ClienteDTO clienteDTO) {
        validarCpfUnico(clienteDTO.getCpf());

        var enderecoViaCep = viaCepService.getAddressByCep(clienteDTO.getEndereco().getCep());

        Endereco endereco = new Endereco();
        endereco.setLogradouro(enderecoViaCep.getLogradouro());
        endereco.setNumero(clienteDTO.getEndereco().getNumero());
        endereco.setBairro(enderecoViaCep.getBairro());
        endereco.setCidade(enderecoViaCep.getLocalidade());
        endereco.setCep(clienteDTO.getEndereco().getCep());

        Cliente cliente = new Cliente();
        cliente.setNome(clienteDTO.getNome());
        cliente.setEmail(clienteDTO.getEmail());
        cliente.setTelefone(clienteDTO.getTelefone());
        cliente.setCpf(clienteDTO.getCpf());
        cliente.setEndereco(endereco);

        cliente = clienteRepository.save(cliente);
        emailService.enviarEmailConfirmacao(cliente.getEmail(), cliente.getNome());

        return new ClienteDTO(cliente);
    }

    @Transactional
    public ClienteDTO atualizar(Long id, ClienteDTO clienteDTO) {
        Cliente cliente = clienteRepository.findById(id)
            .orElseThrow(() -> new ClienteNotFoundException("Cliente não encontrado. Id: " + id));

        if (!cliente.getCpf().equals(clienteDTO.getCpf())) {
            validarCpfUnico(clienteDTO.getCpf());
        }

        var enderecoViaCep = viaCepService.getAddressByCep(clienteDTO.getEndereco().getCep());

        Endereco endereco = new Endereco();
        endereco.setLogradouro(enderecoViaCep.getLogradouro());
        endereco.setNumero(clienteDTO.getEndereco().getNumero());
        endereco.setBairro(enderecoViaCep.getBairro());
        endereco.setCidade(enderecoViaCep.getLocalidade());
        endereco.setCep(clienteDTO.getEndereco().getCep());

        cliente.setNome(clienteDTO.getNome());
        cliente.setEmail(clienteDTO.getEmail());
        cliente.setTelefone(clienteDTO.getTelefone());
        cliente.setCpf(clienteDTO.getCpf());
        cliente.setEndereco(endereco);

        cliente = clienteRepository.save(cliente);
        emailService.enviarEmailAtualizacao(cliente.getEmail(), cliente.getNome());

        return new ClienteDTO(cliente);
    }

    @Transactional
    public void deletar(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new ClienteNotFoundException("Cliente não encontrado. Id: " + id);
        }
        clienteRepository.deleteById(id);
    }

    @Transactional
    private void validarCpfUnico(String cpf) {
        if (clienteRepository.findByCpf(cpf).isPresent()) {
            throw new IllegalArgumentException("CPF já cadastrado");
        }
    }


}
>>>>>>> origin/Teste
