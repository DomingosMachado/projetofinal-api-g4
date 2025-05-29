package org.serratec.projetofinal_api_g4.service;

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

    @Transactional
    public PedidoProdutoDTO inserir(PedidoProdutoDTO dto, Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Pedido não encontrado com id: " + pedidoId));

        Produto produto = produtoRepository.findById(dto.getProdutoId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Produto não encontrado com id: " + dto.getProdutoId()));

        PedidoProduto pedidoProduto = new PedidoProduto();
        pedidoProduto.setPedido(pedido);
        pedidoProduto.setProduto(produto);
        pedidoProduto.setQuantidade(dto.getQuantidade());
        pedidoProduto.setPrecoUnitario(dto.getPrecoUnitario());
        pedidoProduto.setSubtotal(dto.getSubtotal());

        pedidoProduto = pedidoProdutoRepository.save(pedidoProduto);

        return new PedidoProdutoDTO(
                pedidoProduto.getProduto().getId(),
                pedidoProduto.getQuantidade(),
                pedidoProduto.getPrecoUnitario(),
                pedidoProduto.getSubtotal());
    }

    @Transactional
    public PedidoProdutoDTO atualizar(Long id, PedidoProdutoDTO dto) {
        PedidoProduto pedidoProduto = pedidoProdutoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "PedidoProduto não encontrado com id: " + id));

        Produto produto = produtoRepository.findById(dto.getProdutoId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Produto não encontrado com id: " + dto.getProdutoId()));

        pedidoProduto.setProduto(produto);
        pedidoProduto.setQuantidade(dto.getQuantidade());
        pedidoProduto.setPrecoUnitario(dto.getPrecoUnitario());
        pedidoProduto.setSubtotal(dto.getSubtotal());

        pedidoProduto = pedidoProdutoRepository.save(pedidoProduto);

        return new PedidoProdutoDTO(
                pedidoProduto.getProduto().getId(),
                pedidoProduto.getQuantidade(),
                pedidoProduto.getPrecoUnitario(),
                pedidoProduto.getSubtotal());
    }

    @Transactional
    public PedidoProdutoDTO buscarPorId(Long id) {
        PedidoProduto pedidoProduto = pedidoProdutoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "PedidoProduto não encontrado com id: " + id));

        return new PedidoProdutoDTO(
                pedidoProduto.getProduto().getId(),
                pedidoProduto.getQuantidade(),
                pedidoProduto.getPrecoUnitario(),
                pedidoProduto.getSubtotal());
    }

    @Transactional
    public List<PedidoProdutoDTO> listarTodos() {
        List<PedidoProduto> lista = pedidoProdutoRepository.findAll();
        return lista.stream()
                .map(pp -> new PedidoProdutoDTO(
                        pp.getProduto().getId(),
                        pp.getQuantidade(),
                        pp.getPrecoUnitario(),
                        pp.getSubtotal()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deletar(Long id) {
        if (!pedidoProdutoRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "PedidoProduto não encontrado com id: " + id);
        }
        pedidoProdutoRepository.deleteById(id);
    }
}