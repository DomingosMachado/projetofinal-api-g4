package org.serratec.projetofinal_api_g4.service;

import org.serratec.projetofinal_api_g4.dto.ViaCepDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ViaCepService {

    private final RestTemplate restTemplate;

    public ViaCepService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ViaCepDTO getAddressByCep(String cep) {
        cep = cep.replaceAll("\\D", "");

        if (cep.length() != 8) {
            throw new IllegalArgumentException("CEP deve ter 8 dígitos: " + cep);
        }

        String url = "https://viacep.com.br/ws/" + cep + "/json/";

        try {
            ViaCepDTO viaCepResponse = restTemplate.getForObject(url, ViaCepDTO.class);

            if (viaCepResponse != null && viaCepResponse.getCep() != null) {
                String cepFormatado = formatarCep(viaCepResponse.getCep());
                viaCepResponse.setCep(cepFormatado);
                return viaCepResponse;
            }

            throw new RuntimeException("CEP não encontrado: " + cep);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar CEP: " + cep, e);
        }
    }

    private String formatarCep(String cep) {
        cep = cep.replaceAll("\\D", "");
        if (cep.length() != 8) {
            return cep;
        }
        return cep.substring(0, 5) + "-" + cep.substring(5);
    }
}
