package org.serratec.projetofinal_api_g4.repository;

import org.serratec.projetofinal_api_g4.domain.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

}
