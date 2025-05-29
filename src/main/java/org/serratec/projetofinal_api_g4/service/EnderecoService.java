package org.serratec.projetofinal_api_g4.service;
import java.util.Optional;
import org.serratec.projetofinal_api_g4.domain.Cliente;
import org.serratec.projetofinal_api_g4.domain.Endereco;
import org.serratec.projetofinal_api_g4.dto.EnderecoDTO;
import org.serratec.projetofinal_api_g4.dto.ViaCepDTO;
import org.serratec.projetofinal_api_g4.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class EnderecoService {

    @Autowired
    private ClienteRepository clienteRepository;
    
    @Autowired
    private ViaCepService viaCepService;

    public EnderecoDTO buscar(String cep) {
        // Buscar na API Externa (ViaCep)
        try {
            ViaCepDTO viaCepData = viaCepService.getAddressByCep(cep);
            
            // Converter ViaCepDTO para Endereco
            Endereco endereco = new Endereco();
            endereco.setCep(viaCepData.getCep());
            endereco.setLogradouro(viaCepData.getLogradouro());
            endereco.setComplemento(viaCepData.getComplemento());
            endereco.setBairro(viaCepData.getBairro());
            endereco.setCidade(viaCepData.getLocalidade()); // localidade -> cidade
            endereco.setUf(viaCepData.getUf());
            endereco.setIbge(viaCepData.getIbge() != null ? Long.parseLong(viaCepData.getIbge()) : null);
            
            return new EnderecoDTO(endereco);
            
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar endereço para o CEP: " + cep, e);
        }
    }

    @Transactional
    public EnderecoDTO inserir(Endereco endereco, Long clienteId) {
        Optional<Cliente> clienteOpt = clienteRepository.findById(clienteId);
        if (clienteOpt.isEmpty()) {
            throw new RuntimeException("Cliente não encontrado");
        }
        
        Cliente cliente = clienteOpt.get();
        cliente.setEndereco(endereco);
        cliente = clienteRepository.save(cliente);
        
        return new EnderecoDTO(endereco);
    }
}