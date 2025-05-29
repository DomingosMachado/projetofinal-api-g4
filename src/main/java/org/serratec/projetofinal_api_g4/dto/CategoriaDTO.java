package org.serratec.projetofinal_api_g4.dto;

import org.serratec.projetofinal_api_g4.domain.Categoria;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class CategoriaDTO {

  @Id
  private Long id;

  private String nome;
  
  private String descricao;

 
public CategoriaDTO() {
  }

  public CategoriaDTO(Long id, String nome, String descricao) {
    this.id = id;
    this.nome = nome;
    this.descricao = descricao;
  }

  public CategoriaDTO(Categoria categoria) {
    //TODO Auto-generated constructor stub
  }
 

}
