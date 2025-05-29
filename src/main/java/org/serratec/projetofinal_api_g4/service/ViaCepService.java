package org.serratec.projetofinal_api_g4.service;

import org.serratec.projetofinal_api_g4.dto.ViaCepDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ViaCepService {

    private final RestTemplate restTemplate;

    public ViaCepService() {
        this.restTemplate = new RestTemplate();
    }

    public ViaCepDTO getAddressByCep(String cep) {
        String url = "https://viacep.com.br/ws/" + cep + "/json/";
        
        try {
            ViaCepDTO viaCepResponse = restTemplate.getForObject(url, ViaCepDTO.class);
            
            if (viaCepResponse != null && viaCepResponse.getCep() != null) {
                // Remove o traço do CEP se existir
                viaCepResponse.setCep(viaCepResponse.getCep().replaceAll("-", ""));
                return viaCepResponse;
            }
            
            throw new RuntimeException("CEP não encontrado: " + cep);
            
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar CEP: " + cep, e);
        }
    }
}