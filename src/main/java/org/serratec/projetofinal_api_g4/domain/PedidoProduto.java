package org.serratec.projetofinal_api_g4.domain;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
@Entity
@Table(name = "pedido_produto")
public class PedidoProduto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @ManyToOne
    @JoinColumn(name = "pedido_id", nullable = false)
    @NotNull(message = "O pedido é obrigatório")
    private Pedido pedido;

    @ManyToOne
    @JoinColumn(name = "produto_id", nullable = false)
    @NotNull(message = "O produto é obrigatório")
    private Produto produto;

    @Positive(message = "A quantidade deve ser positiva")
    @NotNull(message = "A quantidade é obrigatória")
    private Integer quantidade;

    @Positive(message = "O preço deve ser positivo")
    @NotNull(message = "O preço é obrigatório")
    private BigDecimal preco;

}
