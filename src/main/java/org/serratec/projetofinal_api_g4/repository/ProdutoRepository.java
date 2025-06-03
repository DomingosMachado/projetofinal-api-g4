package org.serratec.projetofinal_api_g4.repository;

import java.util.List;

import org.serratec.projetofinal_api_g4.domain.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    // Buscar produtos por categoria
    List<Produto> findByCategoriaId(Long categoriaId);
    
    // Buscar produtos por nome (case insensitive)
    @Query("SELECT p FROM Produto p WHERE LOWER(p.nome) LIKE LOWER(CONCAT('%', :nome, '%'))")
    List<Produto> findByNomeContainingIgnoreCase(@Param("nome") String nome);
    
    // Buscar produtos por categoria e que tenham estoque
    @Query("SELECT p FROM Produto p WHERE p.categoria.id = :categoriaId AND p.estoque > 0")
    List<Produto> findByCategoriaIdAndEstoqueGreaterThanZero(@Param("categoriaId") Long categoriaId);

}
