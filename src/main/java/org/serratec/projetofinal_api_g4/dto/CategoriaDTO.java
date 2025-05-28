package org.serratec.projetofinal_api_g4.dto;

import org.serratec.projetofinal_api_g4.domain.Categoria;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor 
@AllArgsConstructor 
public class CategoriaDTO {

  
  private Long id;

  private String nome;
  
  private String descricao;

 

  public CategoriaDTO(Categoria categoria) {
   
  }
    

}
