package org.serratec.projetofinal_api_g4.dto;

import org.serratec.projetofinal_api_g4.domain.Endereco;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnderecoDTO {

    @NotBlank(message = "O CEP é obrigatório")
    @Pattern(regexp = "\\d{5}-?\\d{3}", message = "CEP deve estar no formato XXXXX-XXX")
    private String cep;

    @NotBlank(message = "O logradouro é obrigatório")
    @Size(max = 100, message = "Logradouro deve ter no máximo 100 caracteres")
    private String logradouro;

    @Size(max = 100, message = "Complemento deve ter no máximo 100 caracteres")
    private String complemento;

    @NotBlank(message = "O bairro é obrigatório")
    @Size(max = 50, message = "Bairro deve ter no máximo 50 caracteres")
    private String bairro;

    @NotBlank(message = "O número é obrigatório")
    @Size(max = 10, message = "Número deve ter no máximo 10 caracteres")
    private String numero;

    @NotBlank(message = "A UF é obrigatória")
    @Pattern(regexp = "[A-Z]{2}", message = "UF deve ter 2 letras maiúsculas")
    private String uf;

    @NotBlank(message = "A cidade é obrigatória")
    @Size(max = 50, message = "Cidade deve ter no máximo 50 caracteres")
    private String cidade;

    private Long ibge;

    // Construtor a partir da entidade 
    public EnderecoDTO(Endereco endereco) {
        if (endereco != null) {
            this.cep = endereco.getCep();
            this.logradouro = endereco.getLogradouro();
            this.complemento = endereco.getComplemento();
            this.bairro = endereco.getBairro();
            this.numero = endereco.getNumero();
            this.uf = endereco.getUf();
            this.cidade = endereco.getCidade();
            this.ibge = endereco.getIbge();
        }
    }

    // Conversão do DTO para a entidade
    public Endereco toEntity() {
        Endereco endereco = new Endereco();
        endereco.setCep(this.cep);
        endereco.setLogradouro(this.logradouro);
        endereco.setComplemento(this.complemento);
        endereco.setBairro(this.bairro);
        endereco.setNumero(this.numero);
        endereco.setUf(this.uf);
        endereco.setCidade(this.cidade);
        endereco.setIbge(this.ibge);
        return endereco;
    }

    // Conversão da entidade para DTO
    public static EnderecoDTO fromEntity(Endereco endereco) {
        return new EnderecoDTO(endereco);
    }
}