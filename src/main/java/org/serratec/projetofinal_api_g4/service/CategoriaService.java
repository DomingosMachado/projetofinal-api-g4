package org.serratec.projetofinal_api_g4.service;

import org.serratec.projetofinal_api_g4.domain.Categoria;
import org.serratec.projetofinal_api_g4.dto.CategoriaDTO;
import org.serratec.projetofinal_api_g4.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class CategoriaService {

  @Autowired
  private CategoriaRepository categoriaRepository;

  @Transactional
  public CategoriaDTO salvar(CategoriaDTO categoriaDTO) {
    Categoria categoria = new Categoria();
    categoria.setId(categoriaDTO.getId());
    categoria.setNome(categoriaDTO.getNome());
    categoria.setDescricao(categoriaDTO.getDescricao());
    categoria = categoriaRepository.save(categoria);
    
    return new CategoriaDTO(categoria.getId(), categoria.getNome(), categoria.getDescricao());
  }
}
