package org.serratec.projetofinal_api_g4.dto;

import jakarta.persistence.Id;
import lombok.Data;

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

}
