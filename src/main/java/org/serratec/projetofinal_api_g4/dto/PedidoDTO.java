package org.serratec.projetofinal_api_g4.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.serratec.projetofinal_api_g4.domain.Pedido;
import org.serratec.projetofinal_api_g4.enums.PedidoStatus;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;

public class PedidoDTO {

    private Long id;

    @NotNull(message = "Cliente é obrigatório")
    private ClienteDTO cliente;

    @JsonProperty("itens") // Mapear tanto "itens" quanto "produtos"
    @NotNull(message = "Lista de itens é obrigatória")
    private List<PedidoProdutoDTO> itens;

    private LocalDateTime dataPedido;
    private LocalDateTime dataAtualizacao;
    private PedidoStatus status;
    private BigDecimal valorTotal;


    // Construtores
    public PedidoDTO() {}

    public PedidoDTO(Pedido pedido) {
        this.id = pedido.getId();
        this.cliente = new ClienteDTO(pedido.getCliente());
        this.dataPedido = pedido.getDataPedido();
        this.dataAtualizacao = pedido.getDataAtualizacao();
        this.status = pedido.getStatus();
        this.valorTotal = pedido.getValorTotal();
        
        if (pedido.getProdutos() != null) {
            this.itens = pedido.getProdutos().stream()
                    .map(PedidoProdutoDTO::new)
                    .toList();
        }
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ClienteDTO getCliente() { return cliente; }
    public void setCliente(ClienteDTO cliente) { this.cliente = cliente; }

    public List<PedidoProdutoDTO> getItens() { return itens; }
    public void setItens(List<PedidoProdutoDTO> itens) { this.itens = itens; }

    // Método alias para manter compatibilidade
    public List<PedidoProdutoDTO> getProdutos() { return itens; }
    public void setProdutos(List<PedidoProdutoDTO> produtos) { this.itens = produtos; }

    public LocalDateTime getDataPedido() { return dataPedido; }
    public void setDataPedido(LocalDateTime dataPedido) { this.dataPedido = dataPedido; }
    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }

    public PedidoStatus getStatus() { return status; }
    public void setStatus(PedidoStatus status) { this.status = status; }

    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }
}