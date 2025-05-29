package org.serratec.projetofinal_api_g4.dto;


import org.serratec.projetofinal_api_g4.domain.Endereco;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EnderecoDTO {
    
    private String cep;
    private String logradouro;
    private String complemento;
    private String bairro;
    private String uf;
    private String numero;
    private String cidade;
    private Long ibge;

    public EnderecoDTO(Endereco endereco) {
        this.cep = endereco.getCep();
        this.logradouro = endereco.getLogradouro();
        this.complemento = endereco.getComplemento();
        this.bairro = endereco.getBairro();
        this.uf = endereco.getUf();
        this.numero = endereco.getNumero();
        this.cidade = endereco.getCidade();
        this.ibge = endereco.getIbge();
    }

    // Método para conversão de DTO para entidade
    public Endereco toEntity() {
        Endereco endereco = new Endereco();
        endereco.setCep(this.cep);
        endereco.setLogradouro(this.logradouro);
        endereco.setComplemento(this.complemento);
        endereco.setBairro(this.bairro);
        endereco.setUf(this.uf);
        endereco.setNumero(this.numero);
        endereco.setCidade(this.cidade);
        endereco.setIbge(this.ibge);
        return endereco;
    }
}