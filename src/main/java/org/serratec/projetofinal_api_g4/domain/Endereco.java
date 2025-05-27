package org.serratec.projetofinal_api_g4.domain;

import jakarta.persistence.Entity;
import lombok.Data;

@Data
@Entity
public class Endereco {

    private String cep;
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String estado;

}
