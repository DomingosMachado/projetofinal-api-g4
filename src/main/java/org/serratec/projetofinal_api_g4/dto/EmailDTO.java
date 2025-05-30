package org.serratec.projetofinal_api_g4.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailDTO {

    private String destinatario;
    private String assunto;
    private String mensagem;
}
