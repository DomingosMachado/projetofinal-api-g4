package org.serratec.projetofinal_api_g4.repository;

import java.math.BigDecimal;
import java.util.List;
import org.serratec.projetofinal_api_g4.domain.PedidoProduto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PedidoProdutoRepository extends JpaRepository<PedidoProduto, Long> {
    
    List<PedidoProduto> findByPedidoId(Long pedidoId);
    
    List<PedidoProduto> findByProdutoId(Long produtoId);
    
    @Modifying
    @Query("DELETE FROM PedidoProduto pp WHERE pp.pedido.id = :pedidoId")
    void deleteByPedidoId(@Param("pedidoId") Long pedidoId);
    
    @Query("SELECT COUNT(pp) FROM PedidoProduto pp WHERE pp.pedido.id = :pedidoId")
    long countByPedidoId(@Param("pedidoId") Long pedidoId);
    
    @Query("SELECT SUM(pp.subtotal) FROM PedidoProduto pp WHERE pp.pedido.id = :pedidoId")
    BigDecimal sumSubtotalByPedidoId(@Param("pedidoId") Long pedidoId);
}