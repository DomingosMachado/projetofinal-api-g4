package org.serratec.projetofinal_api_g4.dto;

import lombok.Data;

@Data
public class ViaCepDTO {
    private String cep;
    private String logradouro;
    private String complemento;
    private String bairro;
    private String localidade; // Campo que vem da API do ViaCep
    private String uf;
    private String ibge;
}

