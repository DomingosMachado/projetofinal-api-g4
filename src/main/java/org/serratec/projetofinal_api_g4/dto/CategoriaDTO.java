package org.serratec.projetofinal_api_g4.dto;

import org.serratec.projetofinal_api_g4.domain.Categoria;

import lombok.Data;


@Data

public class CategoriaDTO {

  
  private Long id;

  private String nome;
  
  private String descricao;

 

  public CategoriaDTO(Categoria categoria) {
    // TO DO Auto-generated constructor stub
  }
    

}
