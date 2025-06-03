package org.serratec.projetofinal_api_g4.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.serratec.projetofinal_api_g4.domain.Avaliacao;
import org.serratec.projetofinal_api_g4.domain.Cliente;
import org.serratec.projetofinal_api_g4.domain.Produto;
import org.serratec.projetofinal_api_g4.dto.AvaliacaoDTO;
import org.serratec.projetofinal_api_g4.dto.AvaliacaoRequestDTO;
import org.serratec.projetofinal_api_g4.repository.AvaliacaoRepository;
import org.serratec.projetofinal_api_g4.repository.ClienteRepository;
import org.serratec.projetofinal_api_g4.repository.ProdutoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.transaction.annotation.Transactional;


@Service
public class AvaliacaoService {
    
    private final AvaliacaoRepository avaliacaoRepository;
    private final ProdutoRepository produtoRepository;
    private final ClienteRepository clienteRepository;

    public AvaliacaoService(AvaliacaoRepository avaliacaoRepository,
                            ProdutoRepository produtoRepository,
                            ClienteRepository clienteRepository) {
        this.avaliacaoRepository = avaliacaoRepository;
        this.produtoRepository = produtoRepository;
        this.clienteRepository = clienteRepository;
    }

    @Transactional
    public AvaliacaoDTO criarAvaliacao(AvaliacaoRequestDTO requestDto) {
        Produto produto = produtoRepository.findById(requestDto.getIdProduto())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Produto não encontrado com id: " + requestDto.getIdProduto()));

        Cliente cliente = clienteRepository.findById(requestDto.getIdCliente())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cliente não encontrado com id: " + requestDto.getIdCliente()));

        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setNota(requestDto.getNota());
        avaliacao.setComentario(requestDto.getComentario());
        avaliacao.setProduto(produto);
        avaliacao.setCliente(cliente);
        avaliacao.setDataAvaliacao(LocalDateTime.now());

        avaliacao = avaliacaoRepository.save(avaliacao);
        return new AvaliacaoDTO(avaliacao);
    }

       @Transactional(readOnly = true)
    public List<AvaliacaoDTO> listarPorProduto(Long idProduto) {
        produtoRepository.findById(idProduto)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Produto não encontrado com id: " + idProduto));

        return avaliacaoRepository.findByProdutoId(idProduto).stream()
                .map(AvaliacaoDTO::new)
                .collect(Collectors.toList());
    }

      @Transactional(readOnly = true)
    public double calcularMedia(Long idProduto) {
        List<Avaliacao> avaliacoes = avaliacaoRepository.findByProdutoId(idProduto);
        return avaliacoes.stream()
                .mapToInt(Avaliacao::getNota)
                .average()
                .orElse(0.0);
    }

}
