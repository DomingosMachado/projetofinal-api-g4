package org.serratec.projetofinal_api_g4.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.serratec.projetofinal_api_g4.domain.Carrinho;

import lombok.Data;

@Data
public class CarrinhoResponseDTO {

    private Long id;
    private Long clienteId;
    private BigDecimal total;
    private List<CarrinhoProdutoResponseDTO> itens;

    public CarrinhoResponseDTO() {}

    public CarrinhoResponseDTO(Carrinho carrinho) {
        this.id = carrinho.getId();
        this.clienteId = carrinho.getCliente().getId();
        this.total = carrinho.getTotal();
        this.itens = carrinho.getItens().stream()
                .map(CarrinhoProdutoResponseDTO::new)
                .collect(Collectors.toList());
    }
    
}
