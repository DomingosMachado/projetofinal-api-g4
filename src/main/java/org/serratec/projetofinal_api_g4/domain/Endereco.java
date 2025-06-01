package org.serratec.projetofinal_api_g4.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Endereco {

    
    @NotBlank(message = "O CEP é obrigatório")
    @Pattern(regexp = "\\d{5}-?\\d{3}", message = "CEP deve estar no formato XXXXX-XXX")
    @Column(length = 9)
    private String cep;

    @NotBlank(message = "O logradouro é obrigatório")
    @Size(max = 100, message = "Logradouro deve ter no máximo 100 caracteres")
    @Column(nullable = false, length = 100)
    private String logradouro;

    @Size(max = 100, message = "Complemento deve ter no máximo 100 caracteres")
    @Column(length = 100)
    private String complemento;

    @NotBlank(message = "O bairro é obrigatório")
    @Size(max = 50, message = "Bairro deve ter no máximo 50 caracteres")
    @Column(nullable = false, length = 50)
    private String bairro;

    @NotBlank(message = "O número é obrigatório")
    @Size(max = 10, message = "Número deve ter no máximo 10 caracteres")
    @Column(nullable = false, length = 10)
    private String numero;

    @NotBlank(message = "A UF é obrigatória")
    @Pattern(regexp = "[A-Z]{2}", message = "UF deve ter 2 letras maiúsculas")
    @Column(nullable = false, length = 2)
    private String uf;

    @NotBlank(message = "A cidade é obrigatória")
    @Size(max = 50, message = "Cidade deve ter no máximo 50 caracteres")
    @Column(nullable = false, length = 50)
    private String cidade;

    private Long ibge;
}