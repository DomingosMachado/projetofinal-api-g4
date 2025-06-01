package org.serratec.projetofinal_api_g4.dto;


import lombok.Data;
import org.serratec.projetofinal_api_g4.domain.Endereco;

@Data
public class ViaCepDTO {
    private String cep;
    private String logradouro;
    private String complemento;
    private String bairro;
    private String cidade; 
    private String uf;
    private String ibge;

    // Método para converter ViaCepDTO em Endereco
    public Endereco toEndereco() {
        Endereco endereco = new Endereco();
        endereco.setCep(this.cep);
        endereco.setLogradouro(this.logradouro);
        endereco.setComplemento(this.complemento);
        endereco.setBairro(this.bairro);
        endereco.setCidade(this.cidade);
        endereco.setUf(this.uf);
        try {
            endereco.setIbge(Long.parseLong(this.ibge));
        } catch (NumberFormatException e) {
            endereco.setIbge(null);
        }
        endereco.setNumero(null); // ViaCEP não retorna o número
        return endereco;
    }
}