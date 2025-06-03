// EXTRA DOMINGOS MACHADO

package org.serratec.projetofinal_api_g4.dto;

import java.time.LocalDateTime;
import org.serratec.projetofinal_api_g4.domain.DevolucaoPedido;
import org.serratec.projetofinal_api_g4.enums.StatusDevolucao;

public class DevolucaoRespostaDTO {
    
    private Long id;
    private Long pedidoId;
    private LocalDateTime dataSolicitacao;
    private StatusDevolucao status;
    
    public DevolucaoRespostaDTO() {
    }
    
    public DevolucaoRespostaDTO(DevolucaoPedido devolucao) {
        this.id = devolucao.getId();
        this.pedidoId = devolucao.getPedido().getId();
        this.dataSolicitacao = devolucao.getDataSolicitacao();
        this.status = devolucao.getStatus();
    }
    
    public Long getId() { 
        return id; 
    }
    
    public void setId(Long id) { 
        this.id = id; 
    }
    
    public Long getPedidoId() { 
        return pedidoId; 
    }
    
    public void setPedidoId(Long pedidoId) { 
        this.pedidoId = pedidoId; 
    }
    
    public LocalDateTime getDataSolicitacao() { 
        return dataSolicitacao; 
    }
    
    public void setDataSolicitacao(LocalDateTime dataSolicitacao) { 
        this.dataSolicitacao = dataSolicitacao; 
    }
    
    public StatusDevolucao getStatus() { 
        return status; 
    }
    
    public void setStatus(StatusDevolucao status) { 
        this.status = status; 
    }
}