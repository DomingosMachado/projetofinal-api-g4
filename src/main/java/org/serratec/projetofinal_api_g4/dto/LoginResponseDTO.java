package org.serratec.projetofinal_api_g4.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {
        private String id;
    private String nome;
    private String email;
    private String role;  // CLIENTE ou FUNCIONARIO
    private String token;
    private boolean sucesso;
    private String mensagem;
}
