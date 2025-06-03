// EXTRA DOMINGOS MACHADO

package org.serratec.projetofinal_api_g4.dto;

import jakarta.validation.constraints.NotNull;

public class DevolucaoPedidoDTO {
    
    @NotNull(message = "ID do pedido é obrigatório")
    private Long pedidoId;
    
    public Long getPedidoId() { 
        return pedidoId; 
    }
    
    public void setPedidoId(Long pedidoId) { 
        this.pedidoId = pedidoId; 
    }
}