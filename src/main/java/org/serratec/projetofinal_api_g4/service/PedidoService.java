package org.serratec.projetofinal_api_g4.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.serratec.projetofinal_api_g4.domain.Cliente;
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

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

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

    public List<Pedido> buscarPorClienteId(Long clienteId) {
        return pedidoRepository.findByClienteId(clienteId);
    }

    @Transactional
    public PedidoDTO inserir(PedidoDTO pedidoDTO) {
        if (pedidoDTO.getCliente() == null || pedidoDTO.getCliente().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cliente é obrigatório");
        }
    
        if (pedidoDTO.getItens() == null || pedidoDTO.getItens().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pedido deve ter pelo menos um item");
        }
    
        Cliente cliente = clienteRepository.findById(pedidoDTO.getCliente().getId())
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, 
                    "Cliente não encontrado com ID: " + pedidoDTO.getCliente().getId()
                ));
    
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setDataPedido(LocalDateTime.now());
        pedido.setStatus(PedidoStatus.PENDENTE);
        pedido.setValorTotal(BigDecimal.ZERO);
    
        for (PedidoProdutoDTO ppDTO : pedidoDTO.getItens()) {
            if (ppDTO.getProduto() == null || ppDTO.getProduto().getId() == null) {
                throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, 
                    "ID do produto é obrigatório em todos os itens"
                );
            }
    
            Produto produto = produtoRepository.findById(ppDTO.getProduto().getId())
                    .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, 
                        "Produto não encontrado com ID: " + ppDTO.getProduto().getId()
                    ));
    
            if (produto.getEstoque() < ppDTO.getQuantidade()) {
                throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, 
                    "Estoque insuficiente para o produto: " + produto.getNome() + 
                    ". Estoque disponível: " + produto.getEstoque()
                );
            }
    
            PedidoProduto pedidoProduto = new PedidoProduto();
            pedidoProduto.setProduto(produto);
            pedidoProduto.setQuantidade(ppDTO.getQuantidade());
            pedidoProduto.setPrecoUnitario(produto.getPrecoAtual());
            pedidoProduto.setDesconto(ppDTO.getDesconto() != null ? ppDTO.getDesconto() : BigDecimal.ZERO);
            
            pedidoProduto.calcularSubtotal();
            
            pedido.adicionarProduto(pedidoProduto);
        }
    
        pedido.setValorTotal(pedido.getProdutos().stream()
                .map(PedidoProduto::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
    
        Pedido pedidoSalvo = pedidoRepository.save(pedido);
    
        for (PedidoProduto pp : pedidoSalvo.getProdutos()) {
            Produto produto = pp.getProduto();
            produto.setEstoque(produto.getEstoque() - pp.getQuantidade());
            produtoRepository.save(produto);
        }
    
        try {
            emailService.enviarEmailPedido(
                pedidoSalvo.getCliente().getEmail(),
                pedidoSalvo.getCliente().getNome(),
                pedidoSalvo.getId().toString()
            );
        } catch (Exception e) {
            System.err.println("Erro ao enviar email de confirmação: " + e.getMessage());
        }
    
        return new PedidoDTO(pedidoSalvo);
    }

    @Transactional
    public Pedido atualizar(Long id, Pedido pedidoAtualizado) {
        Pedido pedidoExistente = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "Pedido não encontrado com ID: " + id));

        if ( pedidoExistente.getStatus() == PedidoStatus.ENTREGUE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Pedido não pode ser atualizado. Status atual: " + pedidoExistente.getStatus());
        }

        pedidoExistente.setStatus(pedidoAtualizado.getStatus());
        
        if (pedidoAtualizado.getProdutos() != null && !pedidoAtualizado.getProdutos().isEmpty()) {
            pedidoExistente.getProdutos().clear();
            for (PedidoProduto item : pedidoAtualizado.getProdutos()) {
                item.setPedido(pedidoExistente);
                pedidoExistente.adicionarProduto(item);
            }
            pedidoExistente.atualizarValorTotal();
        }

        return pedidoRepository.save(pedidoExistente);
    }

    @Transactional
    public void deletar(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "Pedido não encontrado com ID: " + id));

        if (pedido.getStatus() == PedidoStatus.ENVIADO || 
            pedido.getStatus() == PedidoStatus.ENTREGUE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Pedido não pode ser excluído. Status atual: " + pedido.getStatus());
        } 

        pedidoRepository.delete(pedido);
    }

    @Transactional
    public Pedido salvar(Pedido pedido) {
        return pedidoRepository.save(pedido);
    }

    public boolean isValidStatusTransition(PedidoStatus atual, PedidoStatus novo) {
        if (atual == PedidoStatus.ENTREGUE) {
            return false;
        }
    
        if (novo == PedidoStatus.PENDENTE && atual != PedidoStatus.PENDENTE) {
            return false;
        }
    
        return true;
    }
}