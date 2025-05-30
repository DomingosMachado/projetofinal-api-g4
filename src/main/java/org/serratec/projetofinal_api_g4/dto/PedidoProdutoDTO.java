package org.serratec.projetofinal_api_g4.dto;

import java.math.BigDecimal;

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
    private Long produtoId;
    
    @NotNull(message = "A quantidade é obrigatória")
    @Positive(message = "A quantidade deve ser positiva")
    private Integer quantidade;
    
    private BigDecimal precoUnitario;
    
    @DecimalMin(value = "0.0", message = "O desconto não pode ser negativo")
    private BigDecimal desconto = BigDecimal.ZERO;
    
    private BigDecimal subtotal;

    public PedidoProdutoDTO(PedidoProduto pedidoProduto) {
        this.id = pedidoProduto.getId();
        this.produtoId = pedidoProduto.getProduto() != null ? pedidoProduto.getProduto().getId() : null;
        this.quantidade = pedidoProduto.getQuantidade();
        this.precoUnitario = pedidoProduto.getPrecoUnitario();
        this.desconto = pedidoProduto.getDesconto() != null ? pedidoProduto.getDesconto() : BigDecimal.ZERO;
        this.subtotal = pedidoProduto.getSubtotal();
    }

    public PedidoProduto toEntity() {
        PedidoProduto pedidoProduto = new PedidoProduto();
        pedidoProduto.setId(this.id);
        pedidoProduto.setQuantidade(this.quantidade);
        

        if (this.precoUnitario != null) {
            pedidoProduto.setPrecoUnitario(this.precoUnitario);
        }
        
        pedidoProduto.setDesconto(this.desconto != null ? this.desconto : BigDecimal.ZERO);
        return pedidoProduto;
    }

    public void calcularSubtotal() {
        if (quantidade != null && precoUnitario != null) {
            BigDecimal valorBruto = precoUnitario.multiply(BigDecimal.valueOf(quantidade));
            BigDecimal valorDesconto = desconto != null ? desconto : BigDecimal.ZERO;
            this.subtotal = valorBruto.subtract(valorDesconto);
            
            if (this.subtotal.compareTo(BigDecimal.ZERO) < 0) {
                this.subtotal = BigDecimal.ZERO;
            }
        }
    }
}