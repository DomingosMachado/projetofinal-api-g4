package org.serratec.projetofinal_api_g4.domain;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
@Entity
@Table(name = "categoria")

public class Categoria {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Size(min = 2, max = 100, message = "O nome da categoria deve ter entre 2 e 100 caracteres")
    @NotBlank(message = "O nome da categoria é obrigatório")
    @Column
    private String nome;

    private String descricao;

    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL)
    private List<Produto> produtos;
}

 