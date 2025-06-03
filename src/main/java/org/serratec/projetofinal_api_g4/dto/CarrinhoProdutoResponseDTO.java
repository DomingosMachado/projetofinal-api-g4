package org.serratec.projetofinal_api_g4.dto;

import java.math.BigDecimal;

import org.serratec.projetofinal_api_g4.domain.CarrinhoProduto;

import lombok.Data;

@Data
public class CarrinhoProdutoResponseDTO {

    private Long id;
    private Long produtoId;
    private Integer quantidade;
    private BigDecimal precoUnitario;
    private BigDecimal subtotal;

    public CarrinhoProdutoResponseDTO() {}

    public CarrinhoProdutoResponseDTO(CarrinhoProduto item) {
        this.id = item.getId();
        this.produtoId = item.getProduto().getId();
        this.quantidade = item.getQuantidade();
        this.precoUnitario = item.getPrecoUnitario();
        this.subtotal = item.getSubtotal();
    }
}
