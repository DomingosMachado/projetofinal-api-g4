package org.serratec.projetofinal_api_g4.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.serratec.projetofinal_api_g4.enums.PedidoStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    @NotNull(message = "O cliente é obrigatório")
    private Cliente cliente;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
    private List<PedidoProduto> produtos;

    @NotNull(message = "O valor total do pedido é obrigatório")
    @Positive(message = "O valor total do pedido deve ser positivo")
    private BigDecimal valorTotal;

    @NotNull(message = "A data do pedido é obrigatória")
    private LocalDateTime dataPedido;

    @Enumerated(EnumType.STRING)
    private PedidoStatus status;

}
