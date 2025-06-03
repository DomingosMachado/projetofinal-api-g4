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
        try {
            System.out.println("Iniciando inserção de produto: " + dto.getNome());
            
            if (dto.getCategoria() == null || dto.getCategoria().getId() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Categoria é obrigatória");
            }
            
            Categoria categoria = categoriaRepository.findById(dto.getCategoria().getId())
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Categoria não encontrada com id: " + dto.getCategoria().getId()));
            
            System.out.println("Categoria encontrada: " + categoria.getNome());            Produto produto = new Produto();
            produto.setNome(dto.getNome());
            produto.setDescricao(dto.getDescricao());
            produto.setPreco(dto.getPreco());
            produto.setPrecoAtual(dto.getPrecoAtual() != null ? dto.getPrecoAtual() : dto.getPreco());
            produto.setEstoque(dto.getEstoque() != null ? dto.getEstoque() : dto.getQuantidade());
            produto.setQuantidade(dto.getQuantidade());
            produto.setCategoria(categoria);        
            
            produto.setFornecedor(null); // Deixar nulo por enquanto
            
            System.out.println("Salvando produto...");
            produto = produtoRepository.save(produto);
            System.out.println("Produto salvo com ID: " + produto.getId());
            
            return new ProdutoDTO(produto);
        } catch (Exception e) {
            System.err.println("Erro ao inserir produto: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Transactional
    public ProdutoDTO atualizar(Long id, ProdutoDTO dto) {
        Produto produto = produtoRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Produto não encontrado com id: " + id));

        Categoria categoria = categoriaRepository.findById(dto.getCategoria().getId())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Categoria não encontrada com id: " + dto.getCategoria().getId()));        produto.setNome(dto.getNome());
        produto.setDescricao(dto.getDescricao());
        produto.setPreco(dto.getPreco());
        produto.setPrecoAtual(dto.getPrecoAtual() != null ? dto.getPrecoAtual() : dto.getPreco());
        produto.setEstoque(dto.getEstoque() != null ? dto.getEstoque() : dto.getQuantidade());
        produto.setQuantidade(dto.getQuantidade());
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
        Produto produto = produtoRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Produto não encontrado com id: " + id));

        // Verifica se o produto está vinculado a algum pedido
        if (produto.getPedidoProdutos() != null && !produto.getPedidoProdutos().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Não é possível deletar o produto, pois ele está vinculado a um ou mais pedidos.");
        }

        produtoRepository.delete(produto);
    }    public List<ProdutoDTO> buscarPorCategoria(Long categoriaId) {
        // Verifica se a categoria existe
        if (!categoriaRepository.existsById(categoriaId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
                "Categoria não encontrada com id: " + categoriaId);
        }
        
        List<Produto> produtos = produtoRepository.findByCategoriaId(categoriaId);
        return produtos.stream()
                .map(ProdutoDTO::new)
                .collect(Collectors.toList());
    }

    public List<ProdutoDTO> buscarPorNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Nome para busca não pode ser vazio");
        }
        
        List<Produto> produtos = produtoRepository.findByNomeContainingIgnoreCase(nome.trim());
        return produtos.stream()
                .map(ProdutoDTO::new)
                .collect(Collectors.toList());
    }
}