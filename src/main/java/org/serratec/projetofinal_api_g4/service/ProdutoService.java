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
    public ProdutoDTO inserir(ProdutoDTO dto) {
        Categoria categoria = categoriaRepository.findById(dto.getCategoria().getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Categoria não encontrada com id: " + dto.getCategoria().getId()));

        Produto produto = new Produto();
        produto.setNome(dto.getNome());
        produto.setDescricao(dto.getDescricao());
        produto.setPreco(dto.getPreco());
        produto.setPrecoAtual(dto.getPreco()); // Usar o mesmo valor do preço
        produto.setQuantidade(dto.getQuantidade());
        produto.setEstoque(dto.getQuantidade()); // Usar o mesmo valor da quantidade
        produto.setCategoria(categoria);

        produto = produtoRepository.save(produto);
        return new ProdutoDTO(produto);
    }

    @Transactional
    public ProdutoDTO atualizar(Long id, ProdutoDTO dto) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Produto não encontrado com id: " + id));

        Categoria categoria = categoriaRepository.findById(dto.getCategoria().getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Categoria não encontrada com id: " + dto.getCategoria().getId()));

        produto.setNome(dto.getNome());
        produto.setDescricao(dto.getDescricao());
        produto.setPreco(dto.getPreco());
        produto.setPrecoAtual(dto.getPreco()); // Atualizar o preço atual também
        produto.setQuantidade(dto.getQuantidade());
        produto.setEstoque(dto.getQuantidade()); // Atualizar o estoque também
        produto.setCategoria(categoria);

        produto = produtoRepository.save(produto);
        return new ProdutoDTO(produto);
    }

    @Transactional
    public ProdutoDTO buscarPorId(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Produto não encontrado com id: " + id));

        return new ProdutoDTO(produto);
    }

    @Transactional
    public List<ProdutoDTO> listarTodos() {
        List<Produto> produtos = produtoRepository.findAll();
        return produtos.stream()
                .map(ProdutoDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deletar(Long id) {
        if (!produtoRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado com id: " + id);
        }
        produtoRepository.deleteById(id);
    }

//     @Transactional
//     public List<ProdutoDTO> buscarPorCategoria(Long categoriaId) {
//         List<Produto> produtos = produtoRepository.findByCategoriaId(categoriaId);
//         return produtos.stream()
//                 .map(ProdutoDTO::new)
//                 .collect(Collectors.toList());
//     }

//     @Transactional
//     public List<ProdutoDTO> buscarPorNome(String nome) {
//         List<Produto> produtos = produtoRepository.findByNomeContainingIgnoreCase(nome);
//         return produtos.stream()
//                 .map(ProdutoDTO::new)
//                 .collect(Collectors.toList());
//     }
}