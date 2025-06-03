package org.serratec.projetofinal_api_g4.dto;

import java.time.LocalDateTime;

import org.serratec.projetofinal_api_g4.domain.Avaliacao;
import org.serratec.projetofinal_api_g4.domain.Cliente;
import org.serratec.projetofinal_api_g4.domain.Produto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvaliacaoDTO {

    
    
    private Long id;

    @JsonProperty("nota")
    @Min(value = 1, message = "A nota mínima é 1")
    @Max(value = 5, message = "A nota máxima é 5")
    @NotNull(message = "A nota é obrigatória")
    private int nota;

    @JsonProperty("comentario")
    @Size(max = 500, message = "O comentário deve ter no máximo 500 caracteres")
    private String comentario;

    @JsonProperty("idProduto")
    @NotNull(message = "O ID do produto é obrigatório")
    private Long idProduto;

    @JsonProperty("idCliente")
    @NotNull(message = "O ID do cliente é obrigatório")
    private Long idCliente;

    private LocalDateTime dataCriacao;

    public AvaliacaoDTO(Avaliacao avaliacao) {
        this.id = avaliacao.getId();
        this.nota = avaliacao.getNota();
        this.comentario = avaliacao.getComentario();
        this.idProduto = avaliacao.getProduto() != null ? avaliacao.getProduto().getId() : null;
        this.idCliente = avaliacao.getCliente() != null ? avaliacao.getCliente().getId() : null;
        this.dataCriacao = avaliacao.getDataAvaliacao();
    }

    public Avaliacao toEntity(Produto produto, Cliente cliente) {
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setId(this.id);
        avaliacao.setNota(this.nota);
        avaliacao.setComentario(this.comentario);
        avaliacao.setProduto(produto);
        avaliacao.setCliente(cliente);
        return avaliacao;
    }
}
