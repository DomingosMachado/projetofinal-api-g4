package org.serratec.projetofinal_api_g4.enums;

public enum PedidoStatus {
    CARRINHO("Carrinho"), 
    PENDENTE("Pendente"),
    CONFIRMADO("Confirmado"),
    ENVIADO("Enviado"),
    ENTREGUE("Entregue"),
    CANCELADO("Cancelado");
    private final String descricao;

    PedidoStatus(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}