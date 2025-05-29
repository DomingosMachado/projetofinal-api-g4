package org.serratec.projetofinal_api_g4.repository;

import java.util.List;
import java.util.Optional;

import org.serratec.projetofinal_api_g4.domain.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    
    // Buscar cliente por CPF
    Optional<Cliente> findByCpf(String cpf);
    
    
    boolean existsByCpf(String cpf);
    
    // Buscar cliente por email
    Optional<Cliente> findByEmail(String email);
    
    // Verificar se existe cliente com email
    boolean existsByEmail(String email);
    
    // Buscar clientes por nome (contendo)
    List<Cliente> findByNomeContainingIgnoreCase(String nome);
    
    // // Buscar clientes por cidade
    // @Query("SELECT c FROM Cliente c WHERE c.endereco.cidade = :cidade")
    // List<Cliente> findByCidade(@Param("cidade") String cidade);
    
    // // Buscar clientes por CEP
    // @Query("SELECT c FROM Cliente c WHERE c.endereco.cep = :cep")
    // List<Cliente> findByCep(@Param("cep") String cep);
    
    // // Buscar clientes por UF
    // @Query("SELECT c FROM Cliente c WHERE c.endereco.uf = :uf")
    // List<Cliente> findByUf(@Param("uf") String uf);
    
    // // Contar clientes por cidade
    // @Query("SELECT COUNT(c) FROM Cliente c WHERE c.endereco.cidade = :cidade")
    // Long countByCidade(@Param("cidade") String cidade);
}
