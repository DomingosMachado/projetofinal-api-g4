// EXTRA DOMINGOS MACHADO

package org.serratec.projetofinal_api_g4.service;

import org.serratec.projetofinal_api_g4.domain.DevolucaoPedido;
import org.serratec.projetofinal_api_g4.domain.Pedido;
import org.serratec.projetofinal_api_g4.dto.DevolucaoRespostaDTO;
import org.serratec.projetofinal_api_g4.enums.StatusDevolucao;
import org.serratec.projetofinal_api_g4.repository.DevolucaoPedidoRepository;
import org.serratec.projetofinal_api_g4.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class DevolucaoPedidoService {

    @Autowired
    private DevolucaoPedidoRepository devolucaoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Transactional
    public DevolucaoRespostaDTO solicitarDevolucao(Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Pedido não encontrado com o ID: " + pedidoId));

        DevolucaoPedido devolucao = new DevolucaoPedido(pedido);
        devolucao.setStatus(StatusDevolucao.APROVADA);
        devolucao = devolucaoRepository.save(devolucao);

        return new DevolucaoRespostaDTO(devolucao);
    }

    public Pedido buscarPedidoPorId(Long id) {
        return pedidoRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Pedido não encontrado com o ID: " + id));
    }
}