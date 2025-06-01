package org.serratec.projetofinal_api_g4.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "produto")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id; 

    @NotBlank(message = "O nome do produto é obrigatório")
    @Size(min = 2, max = 200, message = "O nome do produto deve ter entre 2 e 200 caracteres")
    @Column(nullable = false, length = 200)
    private String nome;

    @Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres")
    @Column(length = 500)
    private String descricao;

    @Positive(message = "O preço do produto deve ser positivo")
    @NotNull(message = "O preço do produto é obrigatório")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    @Positive(message = "O preço atual do produto deve ser positivo")
    @NotNull(message = "O preço atual do produto é obrigatório")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precoAtual;

    @Positive(message = "O estoque do produto deve ser positivo")
    @NotNull(message = "O estoque do produto é obrigatório")
    @Column(nullable = false)
    private Integer estoque;

    @Positive(message = "A quantidade do produto deve ser positiva")
    @NotNull(message = "A quantidade do produto é obrigatória")
    @Column(nullable = false)
    private Integer quantidade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    @NotNull(message = "A categoria do produto é obrigatória")
    private Categoria categoria;

    @OneToMany(mappedBy = "produto", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PedidoProduto> pedidoProdutos = new ArrayList<>();

    public void adicionarPedidoProduto(PedidoProduto pedidoProduto) {
        pedidoProdutos.add(pedidoProduto);
        pedidoProduto.setProduto(this);
    }

    public void removerPedidoProduto(PedidoProduto pedidoProduto) {
        pedidoProdutos.remove(pedidoProduto);
        pedidoProduto.setProduto(null);
    }
}