package org.serratec.projetofinal_api_g4.domain;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
public class Avaliacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Min(value = 1, message = "A nota mínima é 1")
    @Max(value = 5, message = "A nota máxima é 5")
    @Column(nullable = false)
    private int nota;

    @Size(max = 500, message = "O comentário deve ter no máximo 500 caracteres")
    private String comentario;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime dataAvaliacao = LocalDateTime.now();

    @JoinColumn(name = "produto_id")
    @ManyToOne
    private Produto produto;

    @JoinColumn(name = "cliente_id")
    @ManyToOne
    private Cliente cliente;
}
