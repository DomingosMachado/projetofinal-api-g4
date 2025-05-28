package org.serratec.projetofinal_api_g4.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.serratec.projetofinal_api_g4.enums.PedidoStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor 
@AllArgsConstructor 
public class PedidoDTO {

  private Long clienteId;

  private List<PedidoProdutoDTO> produtos = new ArrayList<>();

  private BigDecimal valorTotal;

  private LocalDateTime dataPedido;

  private PedidoStatus status;

}
