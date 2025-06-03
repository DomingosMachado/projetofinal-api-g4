package org.serratec.projetofinal_api_g4.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "carrinho")
public class Carrinho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @OneToMany(mappedBy = "carrinho", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CarrinhoProduto> itens = new ArrayList<>();

    @Column(precision = 10, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    public void adicionarItem(CarrinhoProduto item) {
        itens.add(item);
        item.setCarrinho(this);
        calcularTotal();
    }

    public void removerItem(CarrinhoProduto item) {
        itens.remove(item);
        item.setCarrinho(null);
        calcularTotal();
    }

    public void calcularTotal() {
        total = itens.stream()
            .map(CarrinhoProduto::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void limpar() {
        itens.clear();
        total = BigDecimal.ZERO;
    }

}