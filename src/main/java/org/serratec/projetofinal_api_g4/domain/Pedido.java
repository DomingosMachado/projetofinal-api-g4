package org.serratec.projetofinal_api_g4.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.serratec.projetofinal_api_g4.enums.PedidoStatus;
import org.serratec.projetofinal_api_g4.enums.TipoPedido;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    @NotNull(message = "O cliente é obrigatório")
    private Cliente cliente;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PedidoProduto> produtos = new ArrayList<>();

    @Column(nullable = false, precision = 10, scale = 2)
    @NotNull(message = "O valor total é obrigatório")
    private BigDecimal valorTotal = BigDecimal.ZERO;

    @Column(nullable = false)
    @NotNull(message = "A data do pedido é obrigatória")
    private LocalDateTime dataPedido;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @NotNull(message = "O status do pedido é obrigatório")
    private PedidoStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_pedido", nullable = false)
    private TipoPedido tipoPedido;
    

    public void adicionarProduto(PedidoProduto pedidoProduto) {
        if (pedidoProduto != null) {
            produtos.add(pedidoProduto);
            pedidoProduto.setPedido(this);
            atualizarValorTotal();
        }
    }

    public void removerProduto(PedidoProduto pedidoProduto) {
        if (pedidoProduto != null && produtos.remove(pedidoProduto)) {
            pedidoProduto.setPedido(null);
            atualizarValorTotal();
        }
    }

    public void atualizarValorTotal() {
        this.valorTotal = produtos.stream()
                .map(PedidoProduto::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void setCliente(Pedido cliente2) {
        if (cliente2 != null) {
            this.cliente = cliente2.getCliente();
        } else {
            throw new IllegalArgumentException("Cliente não pode ser nulo");
        }
    }

    public void setCliente(Cliente cliente2) {
        if (cliente2 != null) {
            this.cliente = cliente2;
        } else {
            throw new IllegalArgumentException("Cliente não pode ser nulo");
        }

    }

    public void setDataAtualizacao(LocalDateTime now) {
        
        if (now != null) {
            this.dataPedido = now;
         }
            
        

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