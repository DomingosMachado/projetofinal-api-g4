package org.serratec.projetofinal_api_g4.dto;

import org.serratec.projetofinal_api_g4.domain.Categoria;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
<<<<<<< HEAD
@NoArgsConstructor 
@AllArgsConstructor 
=======
>>>>>>> 4f3ccc66a92b4c75e8eb1daf378b0e17d27f9f82
public class CategoriaDTO {

  @Id
  private Long id;

  private String nome;
  
  private String descricao;

 
<<<<<<< HEAD
=======
public CategoriaDTO() {
  }

  public CategoriaDTO(Long id, String nome, String descricao) {
    this.id = id;
    this.nome = nome;
    this.descricao = descricao;
  }
>>>>>>> 4f3ccc66a92b4c75e8eb1daf378b0e17d27f9f82

  public CategoriaDTO(Categoria categoria) {
    //TODO Auto-generated constructor stub
  }
    

}
