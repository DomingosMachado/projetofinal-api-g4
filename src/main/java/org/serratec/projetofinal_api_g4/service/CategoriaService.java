package org.serratec.projetofinal_api_g4.service;

import java.util.List;
import java.util.stream.Collectors;

import org.serratec.projetofinal_api_g4.domain.Categoria;
import org.serratec.projetofinal_api_g4.dto.CategoriaDTO;
import org.serratec.projetofinal_api_g4.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import jakarta.transaction.Transactional;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    public List<CategoriaDTO> listarTodas() {
        return categoriaRepository.findAll()
            .stream()
            .map(CategoriaDTO::new)
            .collect(Collectors.toList());
    }

    @Transactional
    public CategoriaDTO inserir(CategoriaDTO categoriaDTO) {
        // Validação manual do nome da categoria
        if (!StringUtils.hasText(categoriaDTO.getNome())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "O nome da categoria é obrigatório");
        }
        
        // Verificar se já existe categoria com o mesmo nome
        if (categoriaRepository.existsByNome(categoriaDTO.getNome())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Já existe uma categoria com o nome: " + categoriaDTO.getNome());
        }
        
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
        
        // Validação manual do nome da categoria
        if (!StringUtils.hasText(categoriaDTO.getNome())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "O nome da categoria é obrigatório");
        }
        
        // Verificar se já existe outra categoria com o mesmo nome
        if (!categoria.getNome().equals(categoriaDTO.getNome()) && 
            categoriaRepository.existsByNome(categoriaDTO.getNome())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Já existe uma categoria com o nome: " + categoriaDTO.getNome());
        }
        
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
}