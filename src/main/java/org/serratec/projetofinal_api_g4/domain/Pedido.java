package org.serratec.projetofinal_api_g4.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.serratec.projetofinal_api_g4.enums.PedidoStatus;
import org.serratec.projetofinal_api_g4.enums.TipoPedido;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
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
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id; 

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    @NotNull(message = "O cliente é obrigatório")
    private Cliente cliente;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PedidoProduto> produtos = new ArrayList<>();

    @NotNull(message = "O valor total do pedido é obrigatório")
    @Positive(message = "O valor total do pedido deve ser positivo")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotal;

    @NotNull(message = "A data do pedido é obrigatória")
    @Column(nullable = false)
    private LocalDateTime dataPedido;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "O status do pedido é obrigatório")
    @Column(nullable = false, length = 20)
    private PedidoStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_pedido", nullable = false)
    private TipoPedido tipoPedido;
    

    public void adicionarProduto(PedidoProduto pedidoProduto) {
        produtos.add(pedidoProduto);
        pedidoProduto.setPedido(this);
    }

    public void removerProduto(PedidoProduto pedidoProduto) {
        produtos.remove(pedidoProduto);
        pedidoProduto.setPedido(null);
    }
    //Método para garantir que o cliente ou o forneccedor estejam preenchidos
    public void validarClienteOuFornecedor() {
        if (cliente == null && tipoPedido == TipoPedido.CLIENTE) {
            throw new IllegalArgumentException("O cliente é obrigatório para pedidos do tipo CLIENTE");
        }
        if (cliente != null && tipoPedido == TipoPedido.FORNECEDOR) {
            throw new IllegalArgumentException("O fornecedor não deve ser preenchido para pedidos do tipo FORNECEDOR");
        }
    }
}