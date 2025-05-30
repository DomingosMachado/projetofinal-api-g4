package org.serratec.projetofinal_api_g4.repository;

import java.util.List;
import org.serratec.projetofinal_api_g4.domain.Pedido;
import org.serratec.projetofinal_api_g4.enums.PedidoStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    
    List<Pedido> findByClienteId(Long clienteId);
    
    List<Pedido> findByStatus(PedidoStatus status);
    
    @Query("SELECT p FROM Pedido p WHERE p.cliente.id = :clienteId AND p.status = :status")
    List<Pedido> findByClienteIdAndStatus(@Param("clienteId") Long clienteId, @Param("status") PedidoStatus status);
}
