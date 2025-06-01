package org.serratec.projetofinal_api_g4.service;


import org.serratec.projetofinal_api_g4.domain.Pedido;
import org.serratec.projetofinal_api_g4.domain.PedidoProduto;
import org.serratec.projetofinal_api_g4.domain.Produto;
import org.serratec.projetofinal_api_g4.dto.PedidoDTO;
import org.serratec.projetofinal_api_g4.dto.PedidoProdutoDTO;
import org.serratec.projetofinal_api_g4.enums.PedidoStatus;
import org.serratec.projetofinal_api_g4.repository.ClienteRepository;
import org.serratec.projetofinal_api_g4.repository.PedidoRepository;
import org.serratec.projetofinal_api_g4.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import jakarta.transaction.Transactional;
import java.math.BigDecimal;
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

    @Autowired
    private ClienteRepository clienteRepository;

    public List<Pedido> listarTodos() {
        Optional<List<Pedido>> pedidosOpt = Optional.ofNullable(pedidoRepository.findAll());
        if (pedidosOpt.isEmpty() || pedidosOpt.get().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nenhum pedido encontrado");
        }
        return pedidoRepository.findAll();
    }

    public Optional<Pedido> buscarPorId(Long id) {
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID do pedido não pode ser nulo");
        }
        return pedidoRepository.findById(id);
    }

    @Transactional
    public Pedido salvar(Pedido pedido) {
        verificarEstoque(pedido);
        
        if (pedido.getDataPedido() == null) {
            pedido.setDataPedido(LocalDateTime.now());
        }
        
        if (pedido.getStatus() == null) {
            pedido.setStatus(PedidoStatus.PENDENTE);
        }
        
        recalcularValorTotal(pedido);
        
        Pedido pedidoSalvo = pedidoRepository.save(pedido);
        
        atualizarEstoque(pedidoSalvo);
        
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
    public void buscarPedidoCliente(Long id, String emailCliente){
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Pedido não encontrado"
                ));

        if (!pedido.getCliente().getEmail().equals(emailCliente)) {
            throw new ResponseStatusException(
                HttpStatus.FORBIDDEN, "Você não tem permissão para acessar este pedido"
            );
        }
        
    
    }

    
    @Transactional
    public Pedido atualizar(Long id, Pedido pedidoAtualizado) {
        return pedidoRepository.findById(id)
                .map(pedido -> {
                    PedidoStatus statusAntigo = pedido.getStatus();
                    
                    if (statusAntigo == PedidoStatus.CANCELADO || statusAntigo == PedidoStatus.ENTREGUE) {
                        throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST, 
                            "Não é possível atualizar um pedido com status " + statusAntigo
                        );
                    }
                    
                    if (pedidoAtualizado.getProdutos() != null && !pedidoAtualizado.getProdutos().isEmpty()) {
                        restaurarEstoque(pedido);
                        verificarEstoque(pedidoAtualizado);
                    }
                    
                    pedido.setDataPedido(pedidoAtualizado.getDataPedido());
                    pedido.setStatus(pedidoAtualizado.getStatus());
                    pedido.setCliente(pedidoAtualizado.getCliente());
                    
                    pedido.getProdutos().clear();
                    if (pedidoAtualizado.getProdutos() != null) {
                        for (PedidoProduto pp : pedidoAtualizado.getProdutos()) {
                            pedido.adicionarProduto(pp);
                        }
                    }
                    
                    recalcularValorTotal(pedido);
                    
                    Pedido pedidoAtualizadoNoBanco = pedidoRepository.save(pedido);
                    
                    if (pedidoAtualizadoNoBanco.getStatus() != PedidoStatus.CANCELADO) {
                        atualizarEstoque(pedidoAtualizadoNoBanco);
                    }
                    
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
            
            if (pedido.getStatus() != PedidoStatus.PENDENTE) {
                throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, 
                    "Só é possível excluir pedidos com status PENDENTE"
                );
            }
            
            restaurarEstoque(pedido);
            
            pedidoRepository.deleteById(id);
            
            // Enviar email notificando exclusão do pedido
            try {
                emailService.enviarEmailPedido(
                    pedido.getCliente().getEmail(),
                    pedido.getCliente().getNome(),
                    String.valueOf(pedido.getId())
                );
            } catch (Exception e) {
                System.err.println("Erro ao enviar email de exclusão do pedido: " + e.getMessage());
            }
            
        } else {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Pedido não encontrado"
            );
        }
    }
    
    private void verificarEstoque(Pedido pedido) {
        for (PedidoProduto pp : pedido.getProdutos()) {
            Produto produto = pp.getProduto();
            
            Produto produtoAtualizado = produtoRepository.findById(produto.getId())
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Produto não encontrado: " + produto.getId()
                ));
            
            if (produtoAtualizado.getEstoque() < pp.getQuantidade()) {
                throw new ResponseStatusException(
                     HttpStatus.BAD_REQUEST,
                    "Estoque insuficiente para o produto " + produtoAtualizado.getNome() + 
                    ". Disponível: " + produtoAtualizado.getEstoque() + 
                    ", Solicitado: " + pp.getQuantidade()
                );
            }
            
            pp.setProduto(produtoAtualizado);
            
            if (pp.getPrecoUnitario() == null) {
                pp.setPrecoUnitario(produtoAtualizado.getPrecoAtual());
            }
        }
    }
    
    private void atualizarEstoque(Pedido pedido) {
        if (pedido.getStatus() == PedidoStatus.CANCELADO) {
            return;
        }
        
        for (PedidoProduto pp : pedido.getProdutos()) {
            Produto produto = pp.getProduto();
            
            produto.setEstoque(produto.getEstoque() - pp.getQuantidade());
            produtoRepository.save(produto);
        }
    }
    
    private void restaurarEstoque(Pedido pedido) {
        if (pedido.getStatus() == PedidoStatus.CANCELADO) {
            return;
        }
        
        for (PedidoProduto pp : pedido.getProdutos()) {
            Produto produto = pp.getProduto();
            
            Produto produtoAtualizado = produtoRepository.findById(produto.getId())
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Produto não encontrado: " + produto.getId()
                ));
            
            produtoAtualizado.setEstoque(produtoAtualizado.getEstoque() + pp.getQuantidade());
            produtoRepository.save(produtoAtualizado);
        }
    }
    
    private void recalcularValorTotal(Pedido pedido) {
        java.math.BigDecimal valorTotal = java.math.BigDecimal.ZERO;
        
        for (PedidoProduto pp : pedido.getProdutos()) {
            pp.calcularSubtotal();
            if (pp.getSubtotal() != null) {
                valorTotal = valorTotal.add(pp.getSubtotal());
            }
        }
        
        pedido.setValorTotal(valorTotal);
    }

    public PedidoDTO inserir(PedidoDTO pedidoDTO) {
      Pedido cliente = clienteRepository.findById(pedidoDTO.getCliente()).toEntityWithoutCliente();

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setDataPedido(LocalDateTime.now());
        pedido.setStatus(PedidoStatus.PENDENTE);
        pedido.setValorTotal(BigDecimal.ZERO);

        if (pedidoDTO.getProdutos() == null || pedidoDTO.getProdutos().isEmpty()) {
            throw new IllegalArgumentException("O pedido precisa conter pelo menos um produto.");
        }

        for (PedidoProdutoDTO ppDTO : pedidoDTO.getProdutos()) {
            pedido.adicionarProduto(ppDTO.toEntity(pedido));
        }

        Pedido salvo = pedidoRepository.save(pedido);
        return new PedidoDTO(salvo);
    }

  
}
    