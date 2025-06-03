package org.serratec.projetofinal_api_g4.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FornecedorDTO {

   
    private Long id;   

    @NotBlank(message = "O nome do fornecedor é obrigatório")
    @Size(max = 200, message = "O nome deve ter no máximo 200 caracteres")
    private String nome;

    @NotBlank(message = "O CNPJ do fornecedor é obrigatório")
    @Size(min = 14, max = 18, message = "O CNPJ deve ter entre 14 e 18 caracteres")
    private String cnpj;

   
    
}
