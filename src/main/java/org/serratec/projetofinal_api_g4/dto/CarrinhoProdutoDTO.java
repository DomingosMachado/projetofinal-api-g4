package org.serratec.projetofinal_api_g4.dto;

import java.math.BigDecimal;

import org.serratec.projetofinal_api_g4.domain.CarrinhoProduto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CarrinhoProdutoDTO {

    private Long id;

    @NotNull(message = "O ID do produto é obrigatório")
    private Long produtoId;

    @Positive(message = "A quantidade deve ser positiva")
    private Integer quantidade;

    private BigDecimal precoUnitario;
    private BigDecimal subtotal;

    public CarrinhoProdutoDTO() {}

    public CarrinhoProdutoDTO(CarrinhoProduto item) {
        this.id = item.getId();
        this.produtoId = item.getProduto().getId();
        this.quantidade = item.getQuantidade();
        this.precoUnitario = item.getPrecoUnitario();
        this.subtotal = item.getSubtotal();
    }
}