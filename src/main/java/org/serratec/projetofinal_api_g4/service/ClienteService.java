package org.serratec.projetofinal_api_g4.service;

import java.util.List;
import java.util.stream.Collectors;

import org.serratec.projetofinal_api_g4.domain.Cliente;
//import org.serratec.projetofinal_api_g4.domain.Endereco;
import org.serratec.projetofinal_api_g4.dto.ClienteDTO;
//import org.serratec.projetofinal_api_g4.dto.ViaCepDTO;
import org.serratec.projetofinal_api_g4.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import jakarta.transaction.Transactional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    // @Autowired
    // private ViaCepService viaCepService;

    @Autowired
    private EmailService emailService;

    public List<ClienteDTO> listar() {
        return clienteRepository.findAll().stream()
                .map(ClienteDTO::new)
                .collect(Collectors.toList());
    }

    public ClienteDTO buscarPorId(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Cliente não encontrado. Id: " + id));
        return new ClienteDTO(cliente);
    }

    @Transactional
    public Cliente buscarEntidadePorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado. Id: " + id));
    }

    @Transactional
    public ClienteDTO inserir(ClienteDTO clienteDTO) {
        if (clienteRepository.findByEmail(clienteDTO.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "E-mail já cadastrado");
        }
        validarCpfUnico(clienteDTO.getCpf());
        Cliente novoCliente = clienteDTO.toNewEntity();

        // Criptografar a senha antes de salvar
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        novoCliente.setSenha(encoder.encode(clienteDTO.getSenha()));

        clienteRepository.save(novoCliente);

        try {
            emailService.enviarEmailConfirmacao(
                novoCliente.getEmail(),
                novoCliente.getNome()
            );
        } catch (Exception e) {
            System.err.println("Erro ao enviar e-mail de confirmação: " + e.getMessage());
            // Não interrompe o fluxo, apenas registra o erro
        }
        
        return new ClienteDTO(novoCliente);
    }

    @Transactional
    public ClienteDTO atualizar(Long id, ClienteDTO clienteDTO) {
        Cliente cliente = buscarEntidadePorId(id);
        if (!cliente.getCpf().equals(formatarCPF(clienteDTO.getCpf()))) {
            validarCpfUnico(clienteDTO.getCpf());
        }
        if (!cliente.getEmail().equals(clienteDTO.getEmail()) &&
                clienteRepository.findByEmail(clienteDTO.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "E-mail já cadastrado");
        }
        cliente.setNome(clienteDTO.getNome());
        cliente.setEmail(clienteDTO.getEmail());
        cliente.setTelefone(clienteDTO.getTelefone());
        cliente.setCpf(formatarCPF(clienteDTO.getCpf()));

        // Criptografar a nova senha, se ela for alterada
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        cliente.setSenha(encoder.encode(clienteDTO.getSenha()));

        cliente.setEndereco(clienteDTO.getEndereco() != null ? clienteDTO.getEndereco().toEntity() : null);
        clienteRepository.save(cliente);
        return new ClienteDTO(cliente);
    }

    @Transactional
    public void deletar(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado. Id: " + id);
        }
        clienteRepository.deleteById(id);
    }

    private void validarCpfUnico(String cpf) {
        String cpfFormatado = formatarCPF(cpf.replaceAll("\\D", ""));
        if (clienteRepository.findByCpf(cpfFormatado).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF já cadastrado");
        }
    }

    private String formatarCPF(String cpf) {
        cpf = cpf.replaceAll("\\D", "");
        if (cpf.length() != 11) {
            throw new IllegalArgumentException("CPF deve ter 11 dígitos");
        }
        return cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "." + cpf.substring(6, 9) + "-" + cpf.substring(9);
    }

    // private String formatarTelefone(String telefone) {
    //     telefone = telefone.replaceAll("\\D", "");
    //     if (telefone.length() < 10 || telefone.length() > 11) {
    //         throw new IllegalArgumentException("Telefone deve ter entre 10 e 11 dígitos");
    //     }
    //     if (telefone.length() == 10) {
    //         return "(" + telefone.substring(0, 2) + ") " +
    //                 telefone.substring(2, 6) + "-" +
    //                 telefone.substring(6);
    //     } else {
    //         return "(" + telefone.substring(0, 2) + ") " +
    //                 telefone.substring(2, 7) + "-" +
    //                 telefone.substring(7);
    //     }
    // }

    // private Endereco criarEndereco(ViaCepDTO viaCep, ClienteDTO clienteDTO) {
    //     Endereco endereco = new Endereco();
    //     return atualizarEndereco(endereco, viaCep, clienteDTO);
    // }

   // private Endereco atualizarEndereco(Endereco endereco, ViaCepDTO viaCep, ClienteDTO clienteDTO) {
   //     endereco.setLogradouro(viaCep.getLogradouro());
   //     endereco.setNumero(clienteDTO.getEndereco().getNumero());
   //     endereco.setComplemento(clienteDTO.getEndereco().getComplemento());
   //     endereco.setBairro(viaCep.getBairro());
   //     endereco.setCidade(viaCep.getCidade());
   //     endereco.setCep(viaCep.getCep()); // Formatado
   //     endereco.setUf(viaCep.getUf().toUpperCase());
   //     endereco.setIbge(viaCep.getIbge() != null ? Long.parseLong(viaCep.getIbge()) : null);
   //     return endereco;
  //  }

    public boolean isClienteAutenticado(Long id) {
        String principalUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        String[] parts = principalUsername.split(":");
        Long idLogado = Long.valueOf(parts[0]);
        return idLogado.equals(id);
    }
}
