package org.serratec.projetofinal_api_g4.domain;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
@Entity
@Table(name = "produto")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @NotBlank(message = "O nome do produto é obrigatório")
    private String nome;

    private String descricao;

    @Positive(message = "O  preço do produto deve ser positivo")
    @NotNull(message = "O preço do produto é obrigatório")
    private BigDecimal preco;

    @Positive(message = "A quantidade do produto deve ser positiva")
    @NotNull(message = "A quantidade do produto é obrigatória")
    private Integer quantidade;

    @ManyToOne
    @JoinColumn(name = "categoria_id", nullable = false)
    @NotNull(message = "A categoria do produto é obrigatória")
    private Categoria categoria;
    

}
