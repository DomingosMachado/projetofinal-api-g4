package org.serratec.projetofinal_api_g4.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AvaliacaoRequestDTO {

    @Min(value = 1, message = "A nota mínima é 1")
    @Max(value = 5, message = "A nota máxima é 5")
    @NotNull(message = "A nota é obrigatória")
    private Integer nota;

    @Size(max = 500, message = "O comentário deve ter no máximo 500 caracteres")
    private String comentario;

    @NotNull(message = "O ID do produto é obrigatório")
    private Long idProduto;

    @NotNull(message = "O ID do cliente é obrigatório")
    private Long idCliente;

    public AvaliacaoRequestDTO() {}

    public AvaliacaoRequestDTO(Integer nota, String comentario, Long idProduto, Long idCliente) {
        this.nota = nota;
        this.comentario = comentario;
        this.idProduto = idProduto;
        this.idCliente = idCliente;
    }

    public Integer getNota() {
        return nota;
    }

    public void setNota(Integer nota) {
        this.nota = nota;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public Long getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(Long idProduto) {
        this.idProduto = idProduto;
    }

    public Long getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Long idCliente) {
        this.idCliente = idCliente;
    }

    @Override
    public String toString() {
        return "AvaliacaoRequestDTO{" +
                "nota=" + nota +
                ", comentario='" + comentario + '\'' +
                ", idProduto=" + idProduto +
                ", idCliente=" + idCliente +
                '}';
    }
}