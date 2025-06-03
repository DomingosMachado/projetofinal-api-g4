// EXTRA DOMINGOS MACHADO

package org.serratec.projetofinal_api_g4.repository;

import java.util.List;

import org.serratec.projetofinal_api_g4.domain.DevolucaoPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DevolucaoPedidoRepository extends JpaRepository<DevolucaoPedido, Long> {
    
    List<DevolucaoPedido> findByPedidoId(Long pedidoId);
}
