package org.serratec.projetofinal_api_g4.service;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.serratec.projetofinal_api_g4.domain.Pedido;
import org.serratec.projetofinal_api_g4.domain.PedidoProduto;
import org.serratec.projetofinal_api_g4.domain.Produto;
import org.serratec.projetofinal_api_g4.dto.PedidoProdutoDTO;
import org.serratec.projetofinal_api_g4.repository.PedidoProdutoRepository;
import org.serratec.projetofinal_api_g4.repository.PedidoRepository;
import org.serratec.projetofinal_api_g4.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import jakarta.transaction.Transactional;

@Service
public class PedidoProdutoService {

    @Autowired
    private PedidoProdutoRepository pedidoProdutoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    // Exemplo simples de envio de email (substitua pela implementação real)
    private void enviarEmail(String assunto, String mensagem) {
        // Aqui você integraria com serviço real de envio de email
        System.out.println("EMAIL ENVIADO: " + assunto);
        System.out.println(mensagem);
    }

    @Transactional
    public PedidoProdutoDTO inserir(PedidoProdutoDTO dto, Long pedidoId) {
        validarDados(dto);

        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Pedido não encontrado com id: " + pedidoId));

        Long produtoId = dto.getProduto().getId();

        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Produto não encontrado com id: " + produtoId));

        // Garante que o preço unitário do DTO esteja correto conforme o produto real
        dto.getProduto().setPreco(produto.getPreco());

        PedidoProduto pedidoProduto = dto.toEntityWithPedido(pedido);
        pedidoProduto.setPrecoUnitario(produto.getPreco());
        pedidoProduto.calcularSubtotal();

        pedidoProduto = pedidoProdutoRepository.save(pedidoProduto);

        recalcularValorTotalPedido(pedidoId);

        enviarEmail("Novo item adicionado ao pedido",
                "Produto: " + produto.getNome() + "\nQuantidade: " + dto.getQuantidade() +
                        "\nPedido ID: " + pedidoId);

        return new PedidoProdutoDTO(pedidoProduto);
    }

    @Transactional
    public PedidoProdutoDTO atualizar(Long id, PedidoProdutoDTO dto) {
        validarDados(dto);

        PedidoProduto pedidoProduto = pedidoProdutoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "PedidoProduto não encontrado com id: " + id));

        Long produtoId = dto.getProduto().getId();

        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Produto não encontrado com id: " + produtoId));

        dto.getProduto().setPreco(produto.getPreco());

        pedidoProduto.setProduto(produto);
        pedidoProduto.setQuantidade(dto.getQuantidade());
        pedidoProduto.setPrecoUnitario(produto.getPreco());
        pedidoProduto.setDesconto(dto.getDesconto() != null ? dto.getDesconto() : BigDecimal.ZERO);
        pedidoProduto.calcularSubtotal();

        pedidoProduto = pedidoProdutoRepository.save(pedidoProduto);

        recalcularValorTotalPedido(pedidoProduto.getPedido().getId());

        enviarEmail("Item do pedido atualizado",
                "Produto: " + produto.getNome() + "\nQuantidade: " + dto.getQuantidade() +
                        "\nPedidoProduto ID: " + id);

        return new PedidoProdutoDTO(pedidoProduto);
    }

    @Transactional
    public PedidoProdutoDTO buscarPorId(Long id) {
        PedidoProduto pedidoProduto = pedidoProdutoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "PedidoProduto não encontrado com id: " + id));

        return new PedidoProdutoDTO(pedidoProduto);
    }

    @Transactional
    public List<PedidoProdutoDTO> listarTodos() {
        List<PedidoProduto> lista = pedidoProdutoRepository.findAll();
        return lista.stream()
                .map(PedidoProdutoDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deletar(Long id) {
        PedidoProduto pedidoProduto = pedidoProdutoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "PedidoProduto não encontrado com id: " + id));

        Long pedidoId = pedidoProduto.getPedido().getId();
        pedidoProdutoRepository.deleteById(id);

        recalcularValorTotalPedido(pedidoId);

        enviarEmail("Item do pedido removido",
                "PedidoProduto ID: " + id + "\nPedido ID: " + pedidoId);
    }

    private void validarDados(PedidoProdutoDTO dto) {
        if (dto.getQuantidade() == null || dto.getQuantidade() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Quantidade deve ser maior que zero");
        }

        if (dto.getProduto() == null || dto.getProduto().getPreco() == null
                || dto.getProduto().getPreco().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Preço unitário deve ser maior que zero");
        }

        if (dto.getDesconto() != null && dto.getDesconto().compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Desconto não pode ser negativo");
        }
    }

    private void recalcularValorTotalPedido(Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Pedido não encontrado"));

        List<PedidoProduto> itens = pedidoProdutoRepository.findByPedidoId(pedidoId);

        BigDecimal valorTotal = itens.stream()
                .map(PedidoProduto::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        pedido.setValorTotal(valorTotal);
        pedidoRepository.save(pedido);
    }
}