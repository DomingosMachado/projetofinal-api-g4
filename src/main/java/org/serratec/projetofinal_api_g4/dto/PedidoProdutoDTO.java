package org.serratec.projetofinal_api_g4.dto;

import java.math.BigDecimal;

import org.serratec.projetofinal_api_g4.domain.Pedido;
import org.serratec.projetofinal_api_g4.domain.Produto;
// import org.serratec.projetofinal_api_g4.repository.PedidoProdutoRepository;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor 
@AllArgsConstructor 
public class PedidoProdutoDTO {

  
  private Long id;

  private Pedido pedido;

  private Produto produto;

  private Integer quantidade;

  private BigDecimal valorVenda;



  

}
