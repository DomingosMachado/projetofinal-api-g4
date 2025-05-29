package org.serratec.projetofinal_api_g4.dto;

import java.math.BigDecimal;

import org.serratec.projetofinal_api_g4.domain.PedidoProduto;

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
    private Long produtoId;
    
    @NotNull(message = "A quantidade é obrigatória")
    @Positive(message = "A quantidade deve ser positiva")
    private Integer quantidade;
    
    @NotNull(message = "O preço unitário é obrigatório")
    @Positive(message = "O preço unitário deve ser positivo")
    private BigDecimal precoUnitario;
    
    @NotNull(message = "O subtotal é obrigatório")
    @Positive(message = "O subtotal deve ser positivo")
    private BigDecimal subtotal;

    public PedidoProdutoDTO(PedidoProduto pedidoProduto) {
        this.id = pedidoProduto.getId();
        this.produtoId = pedidoProduto.getProduto() != null ? pedidoProduto.getProduto().getId() : null;
        this.quantidade = pedidoProduto.getQuantidade();
        this.precoUnitario = pedidoProduto.getPrecoUnitario();
        this.subtotal = pedidoProduto.getSubtotal();
    }

    public PedidoProduto toEntity() {
        PedidoProduto pedidoProduto = new PedidoProduto();
        pedidoProduto.setId(this.id);
        pedidoProduto.setQuantidade(this.quantidade);
        pedidoProduto.setPrecoUnitario(this.precoUnitario);
        pedidoProduto.setSubtotal(this.subtotal);
        // Note: produto e pedido devem ser setados no service
        return pedidoProduto;
    }
}