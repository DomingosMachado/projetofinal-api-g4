package org.serratec.projetofinal_api_g4.dto;

import org.serratec.projetofinal_api_g4.domain.Categoria;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaDTO {
    private Long id;

    @Size(min = 2, max = 100, message = "O nome da categoria deve ter entre 2 e 100 caracteres")
    @NotBlank(message = "O nome da categoria é obrigatório")
    private String nome;

    @Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres")
    private String descricao;

    // Conversão de Entidade para DTO
     public CategoriaDTO(Categoria categoria) {
        this.id = categoria.getId();
        this.nome = categoria.getNome();
        this.descricao = categoria.getDescricao();
    }
    

    // Conversão de DTO para Entidade
    public Categoria toEntity() {
        Categoria categoria = new Categoria();
        categoria.setId(this.id);
        categoria.setNome(this.nome);
        categoria.setDescricao(this.descricao);
        return categoria;
    }

   
}

