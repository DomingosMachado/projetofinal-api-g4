package org.serratec.projetofinal_api_g4.repository;

import java.util.Optional;

import org.serratec.projetofinal_api_g4.domain.Fornecedor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FornecedorRepository extends JpaRepository<Fornecedor, Long> {
    boolean existsByCnpj(String cnpj);
    Optional<Fornecedor> findByCnpj(String cnpj);
}
