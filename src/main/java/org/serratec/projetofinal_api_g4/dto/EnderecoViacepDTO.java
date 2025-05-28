package org.serratec.projetofinal_api_g4.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true) 
public class EnderecoViacepDTO {
 
  
    private String cep;
   
    private String logradouro;
   
    private String complemento;
   
    private String bairro;
   
    private String localidade;
   
    private String uf;

    private String ibge;
      
    
}