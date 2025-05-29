package org.serratec.projetofinal_api_g4.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Endereco {

    private String cep;
    private String logradouro;
    private String complemento;
    private String bairro;
    private String numero;
    private String uf;
    private String cidade; 
    private Long ibge;

    
    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }
}
