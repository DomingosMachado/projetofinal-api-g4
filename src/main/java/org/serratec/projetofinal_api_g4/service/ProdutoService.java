package org.serratec.projetofinal_api_g4.service;


import java.util.List;
import java.util.stream.Collectors;

import org.serratec.projetofinal_api_g4.domain.Categoria;
import org.serratec.projetofinal_api_g4.domain.Produto;
import org.serratec.projetofinal_api_g4.dto.ProdutoDTO;
import org.serratec.projetofinal_api_g4.repository.CategoriaRepository;
import org.serratec.projetofinal_api_g4.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import jakarta.transaction.Transactional;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Transactional
    public ProdutoDTO inserir(ProdutoDTO produtoDTO) {
        Categoria categoria = categoriaRepository.findById(produtoDTO.getCategoria().getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Categoria não encontrada com id: " + produtoDTO.getCategoria().getId()));

        Produto produto = new Produto();
        produto.setNome(produtoDTO.getNome());
        produto.setDescricao(produtoDTO.getDescricao());
        produto.setPreco(produtoDTO.getPreco());
        produto.setQuantidade(produtoDTO.getQuantidade());
        produto.setCategoria(categoria);

        produto = produtoRepository.save(produto);
        return new ProdutoDTO(produto.getNome(), produto.getDescricao(),
                produto.getPreco(), produto.getQuantidade(),
                produto.getCategoria());
    }

    @Transactional
    public ProdutoDTO atualizar(Long id, ProdutoDTO produtoDTO) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Produto não encontrado com id: " + id));

        Categoria categoria = categoriaRepository.findById(produtoDTO.getCategoria().getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Categoria não encontrada com id: " + produtoDTO.getCategoria().getId()));

        produto.setNome(produtoDTO.getNome());
        produto.setDescricao(produtoDTO.getDescricao());
        produto.setPreco(produtoDTO.getPreco());
        produto.setQuantidade(produtoDTO.getQuantidade());
        produto.setCategoria(categoria);

        produto = produtoRepository.save(produto);

        return new ProdutoDTO(produto.getNome(), produto.getDescricao(),
                produto.getPreco(), produto.getQuantidade(),
                produto.getCategoria());
    }

    @Transactional
    public ProdutoDTO buscarPorId(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Produto não encontrado com id: " + id));

        return new ProdutoDTO(produto.getNome(), produto.getDescricao(),
                produto.getPreco(), produto.getQuantidade(),
                produto.getCategoria());
    }

    @Transactional
    public List<ProdutoDTO> listarTodos() {
        List<Produto> produtos = produtoRepository.findAll();
        return produtos.stream()
                .map(p -> new ProdutoDTO(p.getNome(), p.getDescricao(),
                        p.getPreco(), p.getQuantidade(),
                        p.getCategoria()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deletar(Long id) {
        if (!produtoRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado com id: " + id);
        }
        produtoRepository.deleteById(id);
    }
}