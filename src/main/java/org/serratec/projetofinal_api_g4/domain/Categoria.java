package org.serratec.projetofinal_api_g4.domain;



import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
    private Long id;

    @Size(min = 2, max = 100, message = "O nome da categoria deve ter entre 2 e 100 caracteres")
    @NotBlank(message = "O nome da categoria é obrigatório")
    @Column(nullable = false, length = 100, unique = true)
    private String nome;

    @Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres")
    @Column(length = 500)
    private String descricao;

    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore // Evita serialização recursiva
    private List<Produto> produtos = new ArrayList<>();



    public void adicionarProduto(Produto produto) {
        if (produto != null && !produtos.contains(produto)) {
            produtos.add(produto);
            produto.setCategoria(this);
        }
    }

    public void removerProduto(Produto produto) {
        if (produto != null && produtos.remove(produto)) {
            produto.setCategoria(null);
        }
    }

    @Override
    public String toString() {
        return "Categoria{id=" + id + ", nome='" + nome + "', descricao='" + descricao + "'}";
    }
}


