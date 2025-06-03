package org.serratec.projetofinal_api_g4.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.serratec.projetofinal_api_g4.domain.*;
import org.serratec.projetofinal_api_g4.dto.*;
import org.serratec.projetofinal_api_g4.enums.PedidoStatus;
import org.serratec.projetofinal_api_g4.repository.*;

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
    private PedidoProdutoRepository pedidoProdutoRepository;

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

    public Pedido salvar(Pedido pedido) {
        if (pedido == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pedido não pode ser nulo");
        }
        return pedidoRepository.save(pedido);
    }

    @Transactional
    public void deletar(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado com id: " + id));

        if (pedido.getStatus() == PedidoStatus.ENTREGUE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pedido entregue não pode ser deletado");
        }

        pedido.getProdutos().forEach(pp -> {
            Produto produto = pp.getProduto();
            produto.setEstoque(produto.getEstoque() + pp.getQuantidade());
            produtoRepository.save(produto);
        });

        pedidoProdutoRepository.deleteAll(pedido.getProdutos());
        pedidoRepository.delete(pedido);
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
                        "Cliente não encontrado com ID: " + pedidoDTO.getCliente().getId()));

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setDataPedido(LocalDateTime.now());
        pedido.setStatus(PedidoStatus.PENDENTE);
        pedido.setValorTotal(BigDecimal.ZERO);

        for (PedidoProdutoDTO ppDTO : pedidoDTO.getItens()) {
            if (ppDTO.getProduto() == null || ppDTO.getProduto().getId() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID do produto é obrigatório em todos os itens");
            }

            Produto produto = produtoRepository.findById(ppDTO.getProduto().getId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Produto não encontrado com ID: " + ppDTO.getProduto().getId()));

            if (produto.getEstoque() < ppDTO.getQuantidade()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Estoque insuficiente para o produto: " + produto.getNome() +
                                ". Estoque disponível: " + produto.getEstoque());
            }

            PedidoProduto pedidoProduto = new PedidoProduto();
            pedidoProduto.setProduto(produto);
            pedidoProduto.setQuantidade(ppDTO.getQuantidade());
            pedidoProduto.setPrecoUnitario(produto.getPrecoAtual());
            pedidoProduto.setDesconto(ppDTO.getDesconto() != null ? ppDTO.getDesconto() : BigDecimal.ZERO);
            pedidoProduto.calcularSubtotal();
            pedidoProduto.setPedido(pedido);

            pedido.adicionarProduto(pedidoProduto);
        }

        pedido.atualizarValorTotal();

        Pedido pedidoSalvo = pedidoRepository.save(pedido);
        pedidoProdutoRepository.saveAll(pedido.getProdutos());

        pedido.getProdutos().forEach(pp -> {
            Produto p = pp.getProduto();
            p.setEstoque(p.getEstoque() - pp.getQuantidade());
            produtoRepository.save(p);
        });

        try {
            emailService.enviarEmailPedido(
                    pedidoSalvo.getCliente().getEmail(),
                    pedidoSalvo.getCliente().getNome(),
                    pedidoSalvo.getId().toString());
        } catch (Exception e) {
            System.err.println("Erro ao enviar email de confirmação: " + e.getMessage());
        }

        return new PedidoDTO(pedidoSalvo);
    }

    @Transactional
    public Pedido criarPedido(Cliente cliente, List<CarrinhoProduto> itens, BigDecimal total, PedidoStatus statusInicial) {
        if (cliente == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cliente é obrigatório");
        }

        if (itens == null || itens.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Itens do pedido são obrigatórios");
        }

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setDataPedido(LocalDateTime.now());
        pedido.setStatus(statusInicial != null ? statusInicial : PedidoStatus.PENDENTE);
        pedido.setValorTotal(BigDecimal.ZERO);

        List<PedidoProduto> pedidoProdutos = itens.stream().map(carrinhoProduto -> {
            Produto produto = carrinhoProduto.getProduto();
            if (produto == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Produto inválido no carrinho");
            }
            if (produto.getEstoque() < carrinhoProduto.getQuantidade()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estoque insuficiente para o produto: " + produto.getNome());
            }

            PedidoProduto pedidoProduto = new PedidoProduto();
            pedidoProduto.setPedido(pedido);
            pedidoProduto.setProduto(produto);
            pedidoProduto.setQuantidade(carrinhoProduto.getQuantidade());
            pedidoProduto.setPrecoUnitario(carrinhoProduto.getPrecoUnitario());
            pedidoProduto.setSubtotal(carrinhoProduto.getSubtotal());
            return pedidoProduto;
        }).toList();

        pedido.setProdutos(pedidoProdutos);
        pedido.atualizarValorTotal();

        Pedido pedidoSalvo = pedidoRepository.save(pedido);
        pedidoProdutoRepository.saveAll(pedidoProdutos);

        pedidoProdutos.forEach(pp -> {
            Produto p = pp.getProduto();
            p.setEstoque(p.getEstoque() - pp.getQuantidade());
            produtoRepository.save(p);
        });

        try {
            emailService.enviarEmailPedido(
                    pedidoSalvo.getCliente().getEmail(),
                    pedidoSalvo.getCliente().getNome(),
                    pedidoSalvo.getId().toString());
        } catch (Exception e) {
            System.err.println("Erro ao enviar email de confirmação: " + e.getMessage());
        }

        return pedidoSalvo;
    }

    @Transactional
    public PedidoDTO atualizarPedido(Long id, PedidoDTO pedidoDTO) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado"));

        if (pedidoDTO.getCliente() != null && pedidoDTO.getCliente().getId() != null) {
            Cliente cliente = clienteRepository.findById(pedidoDTO.getCliente().getId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Cliente não encontrado com ID: " + pedidoDTO.getCliente().getId()));
            pedido.setCliente(cliente);
        }

        if (pedidoDTO.getItens() != null && !pedidoDTO.getItens().isEmpty()) {
            List<PedidoProduto> novosProdutos = pedidoDTO.getItens().stream().map(ppDTO -> {
                Produto produto = produtoRepository.findById(ppDTO.getProduto().getId())
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Produto não encontrado com ID: " + ppDTO.getProduto().getId()));

                if (produto.getEstoque() < ppDTO.getQuantidade()) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Estoque insuficiente para o produto: " + produto.getNome());
                }

                PedidoProduto pedidoProduto = new PedidoProduto();
                pedidoProduto.setPedido(pedido);
                pedidoProduto.setProduto(produto);
                pedidoProduto.setQuantidade(ppDTO.getQuantidade());
                pedidoProduto.setPrecoUnitario(produto.getPrecoAtual());
                pedidoProduto.setDesconto(ppDTO.getDesconto() != null ? ppDTO.getDesconto() : BigDecimal.ZERO);
                pedidoProduto.calcularSubtotal();
                return pedidoProduto;
            }).collect(Collectors.toList());

            List<PedidoProduto> produtosAntigos = pedido.getProdutos();
            produtosAntigos.forEach(pp -> {
                Produto p = pp.getProduto();
                p.setEstoque(p.getEstoque() + pp.getQuantidade());
            });

            pedidoProdutoRepository.deleteAll(produtosAntigos);
            pedido.setProdutos(novosProdutos);
        }

        pedido.atualizarValorTotal();
        Pedido pedidoAtualizado = pedidoRepository.save(pedido);
        return new PedidoDTO(pedidoAtualizado);
    }

    @Transactional
    public PedidoDTO atualizarStatus(Long id, PedidoStatus novoStatus) {
        Optional<Pedido> optionalPedido = pedidoRepository.findById(id);
        if (!optionalPedido.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado com ID: " + id);
        }

        Pedido pedido = optionalPedido.get();

        if (!isValidStatusTransition(pedido.getStatus(), novoStatus)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Transição de status inválida de " + pedido.getStatus() + " para " + novoStatus);
        }

        pedido.setStatus(novoStatus);
        pedido.setDataAtualizacao(LocalDateTime.now());
        Pedido pedidoAtualizado = pedidoRepository.save(pedido);

        try {
            emailService.enviarEmailAtualizacaoStatus(
                    pedido.getCliente().getEmail(),
                    pedido.getCliente().getNome(),
                    pedido.getId().toString(),
                    novoStatus);
        } catch (Exception e) {
            System.err.println("Erro ao enviar email de atualização de status: " + e.getMessage());
        }

        return new PedidoDTO(pedidoAtualizado);
    }

 @Transactional
public PedidoDTO cancelarPedido(Long id) {
    Optional<Pedido> optionalPedido = pedidoRepository.findById(id);
    if (!optionalPedido.isPresent()) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado com ID: " + id);
    }

    Pedido pedido = optionalPedido.get();

    if (pedido.getStatus() != PedidoStatus.PENDENTE) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Apenas pedidos pendentes podem ser cancelados");
    }

    pedido.setStatus(PedidoStatus.CANCELADO);
    pedido.setDataAtualizacao(LocalDateTime.now()); // garanta que esse campo exista na entidade Pedido
    pedidoRepository.save(pedido);

    emailService.enviarEmailCancelamento(pedido.getCliente().getEmail(), pedido);

    return new PedidoDTO(pedido);
}

    @Transactional
    public PedidoDTO finalizarPedido(Long id) {
        Optional<Pedido> optionalPedido = pedidoRepository.findById(id);
        if (!optionalPedido.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado com ID: " + id);
        }

        Pedido pedido = optionalPedido.get();

        if (pedido.getStatus() != PedidoStatus.ENVIADO) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Apenas pedidos enviados podem ser finalizados");
        }

        pedido.setStatus(PedidoStatus.ENTREGUE);
        pedido.setDataAtualizacao(LocalDateTime.now());
        Pedido pedidoFinalizado = pedidoRepository.save(pedido);

        try {
            emailService.enviarEmailAtualizacao(
                    pedidoFinalizado.getCliente().getEmail(),
                    pedidoFinalizado.getCliente().getNome(),
                    pedidoFinalizado.getId().toString());
        } catch (Exception e) {
            System.err.println("Erro ao enviar email de confirmação de entrega: " + e.getMessage());
        }

        return new PedidoDTO(pedidoFinalizado);
    }

    private boolean isTransicaoValida(PedidoStatus statusAtual, PedidoStatus novoStatus) {
        if (statusAtual == null || novoStatus == null) return false;

        switch (statusAtual) {
            case PENDENTE:
                return novoStatus == PedidoStatus.CONFIRMADO || novoStatus == PedidoStatus.CANCELADO;
            case CONFIRMADO:
                return novoStatus == PedidoStatus.ENVIADO || novoStatus == PedidoStatus.CANCELADO;
            case ENVIADO:
                return novoStatus == PedidoStatus.ENTREGUE;
            case ENTREGUE:
            case CANCELADO:
                return false;
            default:
                return false;
        }
    }

    public boolean isValidStatusTransition(PedidoStatus statusAtual, PedidoStatus novoStatus) {
        return isTransicaoValida(statusAtual, novoStatus);
    }
}

