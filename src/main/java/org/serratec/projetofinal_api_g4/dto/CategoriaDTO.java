package org.serratec.projetofinal_api_g4.dto;

import org.serratec.projetofinal_api_g4.domain.Categoria;

<<<<<<< HEAD

import lombok.AllArgsConstructor;
=======
import jakarta.persistence.Id;
>>>>>>> origin/Teste
import lombok.Data;

@Data
@NoArgsConstructor 
@AllArgsConstructor 
public class CategoriaDTO {

  
  private Long id;

  private String nome;
  
  private String descricao;

 

  public CategoriaDTO(Categoria categoria) {
<<<<<<< HEAD
   
=======
    // TO DO Auto-generated constructor stub
>>>>>>> origin/Teste
  }
    

}
