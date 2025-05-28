package org.serratec.projetofinal_api_g4.service;

import org.serratec.projetofinal_api_g4.dto.ViaCepResponse;
import org.serratec.projetofinal_api_g4.exception.EnderecoNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ViaCepService {
    
    private static final String VIA_CEP_URL = "https://viacep.com.br/ws/%s/json/";
    
    private final RestTemplate restTemplate;
    
    public ViaCepService() {
        this.restTemplate = new RestTemplate();
    }
    
    public ViaCepResponse getAddressByCep(String cep) {
        try {
            String url = String.format(VIA_CEP_URL, cep);
            ViaCepResponse response = restTemplate.getForObject(url, ViaCepResponse.class);
            
            if (response == null || response.getCep() == null) {
                throw new EnderecoNotFoundException("CEP não encontrado: " + cep);
            }
            
            return response;
        } catch (Exception e) {
            throw new EnderecoNotFoundException("Erro ao pegar endereço ViaCEP: " + e.getMessage());
        }
    }
} 