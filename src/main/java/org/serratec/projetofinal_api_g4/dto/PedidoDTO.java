package org.serratec.projetofinal_api_g4.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.serratec.projetofinal_api_g4.domain.Pedido;
import org.serratec.projetofinal_api_g4.enums.PedidoStatus;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PedidoDTO {

    private Long id;

    @Valid
    @NotNull(message = "O cliente é obrigatório")
    private ClienteDTO cliente;

    @Valid
    @NotNull(message = "A lista de produtos não pode ser nula")
    private List<PedidoProdutoDTO> produtos = new ArrayList<>();

    @NotNull(message = "O valor total é obrigatório")
    private BigDecimal valorTotal;

    @NotNull(message = "A data do pedido é obrigatória")
    private LocalDateTime dataPedido;

    @NotNull(message = "O status do pedido é obrigatório")
    private PedidoStatus status;

    public PedidoDTO(Pedido pedido) {
        this.id = pedido.getId();
        this.cliente = pedido.getCliente() != null ? new ClienteDTO(pedido.getCliente()) : null;
        this.produtos = pedido.getProdutos() != null 
            ? pedido.getProdutos().stream().map(PedidoProdutoDTO::new).collect(Collectors.toList()) 
            : new ArrayList<>();
        this.valorTotal = pedido.getValorTotal();
        this.dataPedido = pedido.getDataPedido();
        this.status = pedido.getStatus();
    }

    public Pedido toEntityWithoutCliente() {
        Pedido pedido = new Pedido();
        pedido.setId(this.id);
        pedido.setDataPedido(this.dataPedido);
        pedido.setStatus(this.status);
        pedido.setValorTotal(this.valorTotal != null ? this.valorTotal : BigDecimal.ZERO);
        if (this.produtos != null) {
            this.produtos.forEach(pp -> pedido.adicionarProduto(pp.toEntity(pedido)));
        }
        return pedido;
    }
}
