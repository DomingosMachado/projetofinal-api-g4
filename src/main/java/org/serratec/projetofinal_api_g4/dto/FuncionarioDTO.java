package org.serratec.projetofinal_api_g4.dto;

import org.serratec.projetofinal_api_g4.enums.TipoFuncionario;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FuncionarioDTO {

    private Long id;
    private String nome;
    private String email;
    private String senha;
   private TipoFuncionario tipoFuncionario;
   

}
