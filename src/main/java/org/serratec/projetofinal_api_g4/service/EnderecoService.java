package org.serratec.projetofinal_api_g4.service;


import org.serratec.projetofinal_api_g4.domain.Cliente;
import org.serratec.projetofinal_api_g4.domain.Endereco;
import org.serratec.projetofinal_api_g4.dto.EnderecoDTO;
import org.serratec.projetofinal_api_g4.dto.ViaCepDTO;
import org.serratec.projetofinal_api_g4.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import jakarta.transaction.Transactional;

@Service
public class EnderecoService {

    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private ViaCepService viaCepService;

    public EnderecoService(ClienteRepository clienteRepository, ViaCepService viaCepService) {
        this.clienteRepository = clienteRepository;
        this.viaCepService = viaCepService;
    }

    public EnderecoDTO buscar(String cep) {
        try {
            ViaCepDTO viaCepData = viaCepService.getAddressByCep(cep);
            if (viaCepData == null || viaCepData.getCep() == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "CEP não encontrado: " + cep);
            }

            Endereco endereco = new Endereco();
            endereco.setCep(viaCepData.getCep());
            endereco.setLogradouro(viaCepData.getLogradouro());
            endereco.setComplemento(viaCepData.getComplemento());
            endereco.setBairro(viaCepData.getBairro());
            endereco.setCidade(viaCepData.getCidade());
            endereco.setUf(viaCepData.getUf());
            endereco.setIbge(viaCepData.getIbge() != null ? Long.parseLong(viaCepData.getIbge()) : null);

            return new EnderecoDTO(endereco);

        } catch (ResponseStatusException e) {
            throw e; // repassa sem alteração
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "Erro ao buscar endereço para o CEP: " + cep, e);
        }
    }

    @Transactional
    public EnderecoDTO inserir(Endereco endereco, Long clienteId) {
        Cliente cliente = clienteRepository.findById(clienteId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));

        cliente.setEndereco(endereco);
        clienteRepository.save(cliente);

        return new EnderecoDTO(endereco);
    }
}