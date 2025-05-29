package org.serratec.projetofinal_api_g4.repository;

import java.util.List;
import org.serratec.projetofinal_api_g4.domain.PedidoProduto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PedidoProdutoRepository extends JpaRepository<PedidoProduto, Long> {
    
    List<PedidoProduto> findByPedidoId(Long pedidoId);
    
    List<PedidoProduto> findByProdutoId(Long produtoId);
}

    
