package org.serratec.projetofinal_api_g4.repository;

import java.util.List;

import org.serratec.projetofinal_api_g4.domain.Avaliacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {

    List<Avaliacao> findByProdutoId(Long produtoId);

    List<Avaliacao> findByProdutoIdAndNotaGreaterThanEqual(Long produtoId, int nota);

    // Buscar avaliações por cliente
    List<Avaliacao> findByClienteId(Long clienteId);

    // Buscar avaliações por cliente e produto
    List<Avaliacao> findByClienteIdAndProdutoId(Long clienteId, Long produtoId);

    // Buscar avaliações com comentário não vazio
    @Query("SELECT a FROM Avaliacao a WHERE a.produto.id = :produtoId AND a.comentario IS NOT NULL AND a.comentario <> ''")
    List<Avaliacao> findWithComentarioByProdutoId(@Param("produtoId") Long produtoId);

}
