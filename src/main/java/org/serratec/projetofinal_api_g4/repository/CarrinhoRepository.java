package org.serratec.projetofinal_api_g4.repository;

import org.serratec.projetofinal_api_g4.domain.Carrinho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CarrinhoRepository extends JpaRepository<Carrinho, Long> {

    @Query("SELECT c FROM Carrinho c WHERE c.cliente.id = :clienteId")
    Optional<Carrinho> findByClienteId(@Param("clienteId") Long clienteId);
}