package org.serratec.projetofinal_api_g4.service;

import java.util.List;
import java.util.stream.Collectors;

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

  public List<CategoriaDTO> listarTodas() {
    return categoriaRepository.findAll().stream()
        .map(categoria -> new CategoriaDTO(categoria))
        .collect(Collectors.toList());
  }

  @Transactional
  public CategoriaDTO inserir(CategoriaDTO categoriaDTO) {
    Categoria categoria = new Categoria();
    categoria.setId(categoriaDTO.getId());
    categoria.setNome(categoriaDTO.getNome());
    categoria.setDescricao(categoriaDTO.getDescricao());
    categoria = categoriaRepository.save(categoria);
    
    return new CategoriaDTO(categoria.getId(), categoria.getNome(), categoria.getDescricao());
  }


  @Transactional
  public CategoriaDTO atualizar(CategoriaDTO categoriaDTO, Long id){
    Categoria categoria = categoriaRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Categoria não encontrada.id:" + id));
    categoria.setNome(categoriaDTO.getNome());
    categoria = categoriaRepository.save(categoria);
    return new CategoriaDTO(categoria);
      }

   @Transactional
   public CategoriaDTO buscarPorId(Long id){
    Categoria categoria = categoriaRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Categoria não encontrada.id:" + id));
    return new CategoriaDTO(categoria);
   }   

   public void deletar(Long id) {
    if(!categoriaRepository.existsById(id)){
      throw new RuntimeException("Categoria não encontrada.id:" + id);
    }
   categoriaRepository.deleteById(id);     
}
}
