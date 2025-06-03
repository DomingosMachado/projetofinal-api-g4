package org.serratec.projetofinal_api_g4.dto;



import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvaliacaoRequestDTO {

  
    @Min(value = 1, message = "A nota mínima é 1")
    @Max(value = 5, message = "A nota máxima é 5")
    @NotNull(message = "A nota é obrigatória")
    private Integer nota;

    
    @Size(max = 500, message = "O comentário deve ter no máximo 500 caracteres")
    private String comentario;

    
    @NotNull(message = "O ID do produto é obrigatório")
    private Long idProduto;

   
    @NotNull(message = "O ID do cliente é obrigatório")
    private Long idCliente;

}
