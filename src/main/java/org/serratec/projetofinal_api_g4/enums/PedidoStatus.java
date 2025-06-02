package org.serratec.projetofinal_api_g4.enums;

public enum PedidoStatus {
    PENDENTE("Pendente"),
    ENVIADO("Enviado"),
    ENTREGUE("Entregue");

    private final String descricao;

    PedidoStatus(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}