package org.serratec.projetofinal_api_g4.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.serratec.projetofinal_api_g4.enums.PedidoStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
<<<<<<< HEAD
@NoArgsConstructor 
@AllArgsConstructor 
=======
@AllArgsConstructor
@NoArgsConstructor
>>>>>>> origin/Teste
public class PedidoDTO {

  private Long clienteId;

  private List<PedidoProdutoDTO> produtos;

  private BigDecimal valorTotal;

  private LocalDateTime dataPedido;

  private PedidoStatus status;
  
  

  


  
}
