package org.serratec.projetofinal_api_g4.dto;

import java.math.BigDecimal;

import org.serratec.projetofinal_api_g4.domain.Categoria;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor 
@AllArgsConstructor 
public class ProdutoDTO {

  private String nome;

  private String descricao;

  private BigDecimal preco;

  private Integer quantidade;

  private Categoria categoria;

  
}
