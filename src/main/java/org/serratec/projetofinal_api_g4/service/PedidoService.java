package org.serratec.projetofinal_api_g4.service;

import org.serratec.projetofinal_api_g4.domain.Pedido;
import org.serratec.projetofinal_api_g4.domain.PedidoProduto;
import org.serratec.projetofinal_api_g4.domain.Produto;
import org.serratec.projetofinal_api_g4.enums.PedidoStatus;
import org.serratec.projetofinal_api_g4.exception.EstoqueInsuficienteException;
import org.serratec.projetofinal_api_g4.repository.PedidoRepository;
import org.serratec.projetofinal_api_g4.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;
    
    @Autowired
    private ProdutoRepository produtoRepository;
    
    @Autowired
    private EmailService emailService;

    public List<Pedido> listarTodos() {
        return pedidoRepository.findAll();
    }

    public Optional<Pedido> buscarPorId(Long id) {
        return pedidoRepository.findById(id);
    }

    @Transactional
    public Pedido salvar(Pedido pedido) {
        // Verificar estoque para todos os produtos
        verificarEstoque(pedido);
        
        // Se a data não foi fornecida, usa a data atual
        if (pedido.getDataPedido() == null) {
            pedido.setDataPedido(LocalDateTime.now());
        }
        
        // Se o status não foi definido, assume como PENDENTE
        if (pedido.getStatus() == null) {
            pedido.setStatus(PedidoStatus.PENDENTE);
        }
        
        // Recalcular o valor total do pedido
        recalcularValorTotal(pedido);
        
        // Salvar o pedido
        Pedido pedidoSalvo = pedidoRepository.save(pedido);
        
        // Atualizar o estoque dos produtos
        atualizarEstoque(pedidoSalvo);
        
        // Enviar email de confirmação
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

    @Transactional
    public Pedido atualizar(Long id, Pedido pedidoAtualizado) {
        return pedidoRepository.findById(id)
                .map(pedido -> {
                    PedidoStatus statusAntigo = pedido.getStatus();
                    
                    // Verificar se o pedido pode ser atualizado
                    if (statusAntigo == PedidoStatus.CANCELADO || statusAntigo == PedidoStatus.ENTREGUE) {
                        throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST, 
                            "Não é possível atualizar um pedido com status " + statusAntigo
                        );
                    }
                    
                    // Caso esteja alterando os produtos, verificar estoque
                    if (pedidoAtualizado.getProdutos() != null && !pedidoAtualizado.getProdutos().isEmpty()) {
                        // Desfazer as alterações de estoque do pedido anterior
                        restaurarEstoque(pedido);
                        
                        // Verificar estoque para novos produtos
                        verificarEstoque(pedidoAtualizado);
                    }
                    
                    pedido.setDataPedido(pedidoAtualizado.getDataPedido());
                    pedido.setStatus(pedidoAtualizado.getStatus());
                    pedido.setCliente(pedidoAtualizado.getCliente());
                    
                    // Limpar produtos anteriores e adicionar novos
                    pedido.getProdutos().clear();
                    if (pedidoAtualizado.getProdutos() != null) {
                        for (PedidoProduto pp : pedidoAtualizado.getProdutos()) {
                            pedido.adicionarProduto(pp);
                        }
                    }
                    
                    // Recalcular valor total
                    recalcularValorTotal(pedido);
                    
                    Pedido pedidoAtualizadoNoBanco = pedidoRepository.save(pedido);
                    
                    // Atualizar estoque se não foi cancelado
                    if (pedidoAtualizadoNoBanco.getStatus() != PedidoStatus.CANCELADO) {
                        atualizarEstoque(pedidoAtualizadoNoBanco);
                    }
                    
                    // Enviar email se mudou o status
                    if (!statusAntigo.equals(pedidoAtualizadoNoBanco.getStatus())) {
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

    @Transactional
    public void deletar(Long id) {
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(id);
        if (pedidoOpt.isPresent()) {
            Pedido pedido = pedidoOpt.get();
            
            // Verificar se pode ser excluído
            if (pedido.getStatus() != PedidoStatus.PENDENTE) {
                throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, 
                    "Só é possível excluir pedidos com status PENDENTE"
                );
            }
            
            // Restaurar estoque
            restaurarEstoque(pedido);
            
            // Excluir pedido
            pedidoRepository.deleteById(id);
        } else {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Pedido não encontrado"
            );
        }
    }
    

    private void verificarEstoque(Pedido pedido) {
        for (PedidoProduto pp : pedido.getProdutos()) {
            Produto produto = pp.getProduto();
            
            java.math.BigDecimal precoUnitarioAtual = pp.getPrecoUnitario();
            
            Produto produtoAtualizado = produtoRepository.findById(produto.getId())
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Produto não encontrado: " + produto.getId()
                ));
            
            if (produtoAtualizado.getEstoque() < pp.getQuantidade()) {
                throw new EstoqueInsuficienteException(
                    "Estoque insuficiente para o produto " + produtoAtualizado.getNome() + 
                    ". Disponível: " + produtoAtualizado.getEstoque() + 
                    ", Solicitado: " + pp.getQuantidade()
                );
            }
            
            pp.setProduto(produtoAtualizado);
            
            if (precoUnitarioAtual != null) {
                pp.setPrecoUnitario(precoUnitarioAtual);
            } else if (pp.getPrecoUnitario() == null) {
                pp.setPrecoUnitario(produtoAtualizado.getPrecoAtual());
            }
        }
    }
    
    /**
     * Atualiza o estoque dos produtos após confirmação do pedido
     */
    private void atualizarEstoque(Pedido pedido) {
        // Só atualiza estoque se o pedido não estiver cancelado
        if (pedido.getStatus() == PedidoStatus.CANCELADO) {
            return;
        }
        
        for (PedidoProduto pp : pedido.getProdutos()) {
            Produto produto = pp.getProduto();
            
            // Reduzir o estoque
            produto.setEstoque(produto.getEstoque() - pp.getQuantidade());
            
            // Salvar produto atualizado
            produtoRepository.save(produto);
        }
    }
    
    /**
     * Restaura o estoque em caso de cancelamento ou atualização
     */
    private void restaurarEstoque(Pedido pedido) {
        // Só restaura se o pedido não estava cancelado
        if (pedido.getStatus() == PedidoStatus.CANCELADO) {
            return;
        }
        
        for (PedidoProduto pp : pedido.getProdutos()) {
            Produto produto = pp.getProduto();
            
            // Buscar a versão mais atualizada do produto
            Produto produtoAtualizado = produtoRepository.findById(produto.getId())
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Produto não encontrado: " + produto.getId()
                ));
            
            // Restaurar o estoque
            produtoAtualizado.setEstoque(produtoAtualizado.getEstoque() + pp.getQuantidade());
            
            // Salvar produto atualizado
            produtoRepository.save(produtoAtualizado);
        }
    }
    
    /**
     * Recalcula o valor total do pedido com base nos produtos
     */
    private void recalcularValorTotal(Pedido pedido) {
        java.math.BigDecimal valorTotal = java.math.BigDecimal.ZERO;
        
        for (PedidoProduto pp : pedido.getProdutos()) {
            // Forçar cálculo do subtotal
            pp.calcularSubtotal();
            
            // Somar ao valor total
            if (pp.getSubtotal() != null) {
                valorTotal = valorTotal.add(pp.getSubtotal());
            }
        }
        
        pedido.setValorTotal(valorTotal);
    }
}