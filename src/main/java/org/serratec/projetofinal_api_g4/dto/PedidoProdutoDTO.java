package org.serratec.projetofinal_api_g4.dto;

import java.math.BigDecimal;

import org.serratec.projetofinal_api_g4.domain.Pedido;
import org.serratec.projetofinal_api_g4.domain.PedidoProduto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoProdutoDTO {
    private Long id;

    @NotNull(message = "O produto é obrigatório")
    private ProdutoDTO produto;

    @NotNull(message = "A quantidade é obrigatória")
    @Positive(message = "A quantidade deve ser positiva")
    private Integer quantidade;

    @DecimalMin(value = "0.0", message = "O desconto não pode ser negativo")
    private BigDecimal desconto;

    public PedidoProdutoDTO(PedidoProduto pedidoProduto) {
        this.id = pedidoProduto.getId();
        this.produto = pedidoProduto.getProduto() != null ? new ProdutoDTO(pedidoProduto.getProduto()) : null;
        this.quantidade = pedidoProduto.getQuantidade();
        this.desconto = pedidoProduto.getDesconto();
    }

    public PedidoProduto toEntity() {
        PedidoProduto entity = new PedidoProduto();
        entity.setId(this.id);
        entity.setProduto(this.produto != null ? this.produto.toEntity() : null);
        entity.setQuantidade(this.quantidade);
        entity.setDesconto(this.desconto != null ? this.desconto : BigDecimal.ZERO);
        entity.setPrecoUnitario(this.produto != null ? this.produto.getPreco() : BigDecimal.ZERO);
        entity.calcularSubtotal();
        return entity;
    }

    public PedidoProduto toEntity(Pedido pedido) {
        PedidoProduto entity = toEntity(); // Reaproveita o método acima
        entity.setPedido(pedido);          // Associa o pedido recebido
        return entity;
    }

    public BigDecimal getPrecoUnitario() {
        return produto != null ? produto.getPreco() : BigDecimal.ZERO;
    }

}
