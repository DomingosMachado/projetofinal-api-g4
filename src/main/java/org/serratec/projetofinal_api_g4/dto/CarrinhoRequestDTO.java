package org.serratec.projetofinal_api_g4.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import lombok.Data;

@Data
public class CarrinhoRequestDTO {

    @NotEmpty(message = "O carrinho deve conter pelo menos um item")
    @Valid
    private List<CarrinhoProdutoRequestDTO> itens;

}
