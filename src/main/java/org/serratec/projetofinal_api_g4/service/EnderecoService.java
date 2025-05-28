package org.serratec.projetofinal_api_g4.service;

import org.serratec.projetofinal_api_g4.dto.EnderecoDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class EnderecoService {

    private static final String VIACEP_URL = "https://viacep.com.br/ws/{cep}/json/";
   
    public EnderecoDTO buscarEnderecoPorCep(String cep) {
       RestTemplate restTemplate = new RestTemplate();
        EnderecoDTO endereco = restTemplate.getForObject(VIACEP_URL, EnderecoDTO.class, cep);
        
        if (endereco == null || endereco.getCep() == null) {
            throw new RuntimeException("CEP não encontrado: " + cep);
        }
        
        return endereco;    
    }

}
