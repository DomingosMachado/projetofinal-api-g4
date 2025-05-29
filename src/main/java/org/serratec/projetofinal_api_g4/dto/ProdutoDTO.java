package org.serratec.projetofinal_api_g4.dto;

import java.math.BigDecimal;

import org.serratec.projetofinal_api_g4.domain.Produto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor 
@AllArgsConstructor 
public class ProdutoDTO {
    private Long id;
    
    @NotBlank(message = "O nome do produto é obrigatório")
    @Size(min = 2, max = 200, message = "O nome do produto deve ter entre 2 e 200 caracteres")
    private String nome;
    
    @Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres")
    private String descricao;
    
    @NotNull(message = "O preço do produto é obrigatório")
    @Positive(message = "O preço do produto deve ser positivo")
    private BigDecimal preco;
    
    @NotNull(message = "A quantidade do produto é obrigatória")
    @Positive(message = "A quantidade do produto deve ser positiva")
    private Integer quantidade;
    
    @Valid
    @NotNull(message = "A categoria é obrigatória")
    private CategoriaDTO categoria;

    public ProdutoDTO(Produto produto) {
        this.id = produto.getId();
        this.nome = produto.getNome();
        this.descricao = produto.getDescricao();
        this.preco = produto.getPreco();
        this.quantidade = produto.getQuantidade();
        this.categoria = produto.getCategoria() != null ? new CategoriaDTO(produto.getCategoria()) : null;
    }

    public Produto toEntity() {
        Produto produto = new Produto();
        produto.setId(this.id);
        produto.setNome(this.nome);
        produto.setDescricao(this.descricao);
        produto.setPreco(this.preco);
        produto.setQuantidade(this.quantidade);
        produto.setCategoria(this.categoria != null ? this.categoria.toEntity() : null);
        return produto;
    }
}