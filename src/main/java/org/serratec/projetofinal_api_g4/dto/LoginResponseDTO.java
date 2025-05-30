package org.serratec.projetofinal_api_g4.dto;

import org.serratec.projetofinal_api_g4.enums.TipoFuncionario;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDTO {

    private String id;
    private String nome;
    private String email;
    private TipoFuncionario tipoFuncionario;
    private String tipoUsuario;
    private String senha;
    private String token;
    private boolean sucesso;
    private String mensagem;
}
