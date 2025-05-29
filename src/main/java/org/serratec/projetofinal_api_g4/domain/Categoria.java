package org.serratec.projetofinal_api_g4.domain;

import java.util.List;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "categoria")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id; // 'id' em minúsculo

    @Size(min = 2, max = 100, message = "O nome da categoria deve ter entre 2 e 100 caracteres")
    @NotBlank(message = "O nome da categoria é obrigatório")
    @Column(nullable = false, length = 100)
    private String nome;

    @Column(length = 500) // Definindo tamanho máximo
    private String descricao;

    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Produto> produtos;
}
 