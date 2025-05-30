package org.serratec.projetofinal_api_g4.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.serratec.projetofinal_api_g4.domain.Pedido;
import org.serratec.projetofinal_api_g4.enums.PedidoStatus;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor 
@AllArgsConstructor 
public class PedidoDTO {

    private Long id;

    @NotNull(message = "O cliente é obrigatório")
    private Long clienteId;

    @Valid
    @NotEmpty(message = "O pedido deve conter pelo menos um produto")
    private List<PedidoProdutoDTO> produtos;

    // Campos preenchidos automaticamente pelo controller
    private BigDecimal valorTotal;
    private LocalDateTime dataPedido;
    private PedidoStatus status;

    public PedidoDTO(Pedido pedido) {
        this.id = pedido.getId();
        this.clienteId = pedido.getCliente() != null ? pedido.getCliente().getId() : null;
        this.produtos = pedido.getProdutos() != null ? 
            pedido.getProdutos().stream()
                .map(PedidoProdutoDTO::new)
                .collect(Collectors.toList()) : null;
        this.valorTotal = pedido.getValorTotal();
        this.dataPedido = pedido.getDataPedido();
        this.status = pedido.getStatus();
    }
}