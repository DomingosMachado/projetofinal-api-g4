package org.serratec.projetofinal_api_g4.service;

import java.util.List;
import java.util.stream.Collectors;

import org.serratec.projetofinal_api_g4.domain.Categoria;
import org.serratec.projetofinal_api_g4.dto.CategoriaDTO;
import org.serratec.projetofinal_api_g4.repository.CategoriaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import jakarta.transaction.Transactional;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    // Injeção de dependência por construtor (boas práticas)
    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public List<CategoriaDTO> listarTodas() {
        return categoriaRepository.findAll()
            .stream()
            .map(CategoriaDTO::new)
            .collect(Collectors.toList());
    }

    @Transactional
    public CategoriaDTO inserir(CategoriaDTO categoriaDTO) {
        validarCategoria(categoriaDTO, null); // Validação extra modularizada
        Categoria categoria = new Categoria();
        categoria.setNome(categoriaDTO.getNome());
        categoria.setDescricao(categoriaDTO.getDescricao());
        categoria = categoriaRepository.save(categoria);
        return new CategoriaDTO(categoria);
    }

    @Transactional
    public CategoriaDTO atualizar(CategoriaDTO categoriaDTO, Long id) {
        Categoria categoria = categoriaRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Categoria não encontrada. ID: " + id));

        validarCategoria(categoriaDTO, id); // Validação extra modularizada
        
        categoria.setNome(categoriaDTO.getNome());
        categoria.setDescricao(categoriaDTO.getDescricao());
        categoria = categoriaRepository.save(categoria);
        return new CategoriaDTO(categoria);
    }

    public CategoriaDTO buscarPorId(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Categoria não encontrada. ID: " + id));
        return new CategoriaDTO(categoria);
    }

    @Transactional
    public void deletar(Long id) {
        if (!categoriaRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Categoria não encontrada. ID: " + id);
        }
        categoriaRepository.deleteById(id);
    }

    // Validação extra modularizada para reuso e legibilidade
    private void validarCategoria(CategoriaDTO categoriaDTO, Long idAtual) {
        if (!StringUtils.hasText(categoriaDTO.getNome())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
            "O nome da categoria é obrigatório");
        }

        boolean nomeExiste = categoriaRepository.existsByNome(categoriaDTO.getNome());
        if (nomeExiste) {
            // Se for inserção (idAtual == null) ou alteração para nome já existente
            if (idAtual == null || !categoriaRepository.findById(idAtual)
                    .map(c -> c.getNome().equals(categoriaDTO.getNome()))
                    .orElse(false)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Já existe uma categoria com o nome: " + categoriaDTO.getNome());
            }
        }
    }
}