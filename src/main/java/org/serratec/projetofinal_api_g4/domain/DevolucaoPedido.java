// EXTRA DOMINGOS MACHADO

package org.serratec.projetofinal_api_g4.domain;

import java.time.LocalDateTime;

import org.serratec.projetofinal_api_g4.enums.StatusDevolucao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "devolucoes")
public class DevolucaoPedido {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;
    
    @Column(name = "data_solicitacao", nullable = false)
    private LocalDateTime dataSolicitacao;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StatusDevolucao status;
    
    public DevolucaoPedido() {}
    
    public DevolucaoPedido(Pedido pedido) {
        this.pedido = pedido;
        this.dataSolicitacao = LocalDateTime.now();
        this.status = StatusDevolucao.SOLICITADA;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; }
    
    public LocalDateTime getDataSolicitacao() { return dataSolicitacao; }
    public void setDataSolicitacao(LocalDateTime dataSolicitacao) { this.dataSolicitacao = dataSolicitacao; }
    
    public StatusDevolucao getStatus() { return status; }
    public void setStatus(StatusDevolucao status) { this.status = status; }
}