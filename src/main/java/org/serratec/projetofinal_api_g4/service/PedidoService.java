package org.serratec.projetofinal_api_g4.service;

import org.serratec.projetofinal_api_g4.domain.Pedido;
import org.serratec.projetofinal_api_g4.enums.PedidoStatus;
import org.serratec.projetofinal_api_g4.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;
    
    @Autowired
    private EmailService emailService;

    public List<Pedido> listarTodos() {
        return pedidoRepository.findAll();
    }

    public Optional<Pedido> buscarPorId(Long id) {
        return pedidoRepository.findById(id);
    }

    public Pedido salvar(Pedido pedido) {
        Pedido pedidoSalvo = pedidoRepository.save(pedido);
        
        try {
            String numeroPedido = String.valueOf(pedidoSalvo.getId());
            emailService.enviarEmailPedido(
                pedidoSalvo.getCliente().getEmail(),
                pedidoSalvo.getCliente().getNome(),
                numeroPedido
            );
        } catch (Exception e) {
            System.err.println("Erro ao enviar email de confirmação do pedido: " + e.getMessage());
        }
        
        return pedidoSalvo;
    }

    public Pedido atualizar(Long id, Pedido pedidoAtualizado) {
        return pedidoRepository.findById(id)
                .map(pedido -> {
                    PedidoStatus statusAntigo = pedido.getStatus();
                    
                    pedido.setDataPedido(pedidoAtualizado.getDataPedido());
                    pedido.setStatus(pedidoAtualizado.getStatus());
                    pedido.setCliente(pedidoAtualizado.getCliente());
                    pedido.setProdutos(pedidoAtualizado.getProdutos());
                    
                    Pedido pedidoAtualizadoNoBanco = pedidoRepository.save(pedido);
                    
                    if (!statusAntigo.equals(pedidoAtualizado.getStatus())) {
                        try {
                            String numeroPedido = String.valueOf(pedidoAtualizadoNoBanco.getId());
                            emailService.enviarEmailPedido(
                                pedidoAtualizadoNoBanco.getCliente().getEmail(),
                                pedidoAtualizadoNoBanco.getCliente().getNome(),
                                numeroPedido
                            );
                        } catch (Exception e) {
                            System.err.println("Erro ao enviar email de atualização do pedido: " + e.getMessage());
                        }
                    }
                    
                    return pedidoAtualizadoNoBanco;
                })
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Pedido não encontrado"
                ));
    }

    public void deletar(Long id) {
        pedidoRepository.deleteById(id);
    }
}