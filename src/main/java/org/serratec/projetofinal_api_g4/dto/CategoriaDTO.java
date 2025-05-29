package org.serratec.projetofinal_api_g4.dto;

import org.serratec.projetofinal_api_g4.domain.Categoria;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaDTO {
  
    private Long id;
    private String nome;
    private String descricao;

    // Construtor para conversão de entidade para DTO
    public CategoriaDTO(Categoria categoria) {
        this.id = categoria.getId();
        this.nome = categoria.getNome();
        this.descricao = categoria.getDescricao();
    }
    
    // Método para conversão de DTO para entidade
    public Categoria toEntity() {
        Categoria categoria = new Categoria();
        categoria.setId(this.id);
        categoria.setNome(this.nome);
        categoria.setDescricao(this.descricao);
        return categoria;
    }
}
