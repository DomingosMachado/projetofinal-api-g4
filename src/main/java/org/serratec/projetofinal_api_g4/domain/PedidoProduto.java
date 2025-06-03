package org.serratec.projetofinal_api_g4.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "pedido_produto")
public class PedidoProduto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    @NotNull(message = "O pedido é obrigatório")
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id", nullable = false)
    @NotNull(message = "O produto é obrigatório")
    private Produto produto;

    @NotNull(message = "A quantidade é obrigatória")
    @Positive(message = "A quantidade deve ser positiva")
    @Column(nullable = false)
    private Integer quantidade;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precoUnitario;

    @DecimalMin(value = "0.0", message = "O desconto não pode ser negativo")
    @Column(precision = 10, scale = 2)
    private BigDecimal desconto = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal subtotal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compra_fornecedor_id")
    private CompraFornecedor compraFornecedor;


    public void calcularSubtotal() {
        if (quantidade != null && precoUnitario != null) {
            BigDecimal valorBruto = precoUnitario.multiply(BigDecimal.valueOf(quantidade));
            BigDecimal valorDesconto = desconto != null ? desconto : BigDecimal.ZERO;
            this.subtotal = valorBruto.subtract(valorDesconto);
            
            if (this.subtotal.compareTo(BigDecimal.ZERO) < 0) {
                this.subtotal = BigDecimal.ZERO;
            }
        }
    }

    public BigDecimal getValorBruto() {
        if (quantidade != null && precoUnitario != null) {
            return precoUnitario.multiply(BigDecimal.valueOf(quantidade));
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getPercentualDesconto() {
        BigDecimal valorBruto = getValorBruto();
        if (valorBruto.compareTo(BigDecimal.ZERO) > 0 && desconto != null) {
            return desconto.divide(valorBruto, 4, RoundingMode.HALF_UP)
                          .multiply(BigDecimal.valueOf(100));
        }
        return BigDecimal.ZERO;
    }
}