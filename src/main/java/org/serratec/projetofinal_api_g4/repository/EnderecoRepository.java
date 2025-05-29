package org.serratec.projetofinal_api_g4.repository;

import java.util.List;
import java.util.Optional;

import org.serratec.projetofinal_api_g4.domain.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EnderecoRepository extends JpaRepository<Endereco, Long> {
    
    // Buscar endereço por CEP
    Optional<Endereco> findByCep(String cep);
    
    // Verificar se existe endereço com CEP
    boolean existsByCep(String cep);
    
    // Buscar endereços por cidade
    List<Endereco> findByCidadeIgnoreCase(String cidade);
    
    // Buscar endereços por UF
    List<Endereco> findByUf(String uf);
    
    // Buscar endereços por bairro
    List<Endereco> findByBairroContainingIgnoreCase(String bairro);
    
    // Buscar endereços por logradouro
    List<Endereco> findByLogradouroContainingIgnoreCase(String logradouro);
    
    // Buscar endereços por cidade e UF
    List<Endereco> findByCidadeIgnoreCaseAndUf(String cidade, String uf);
    
    // // Query personalizada para buscar endereços por região
    // @Query("SELECT e FROM Endereco e WHERE e.cidade IN :cidades")
    // List<Endereco> findByRegiao(@Param("cidades") List<String> cidades);
    
    // // Contar endereços por UF
    // Long countByUf(String uf);
    
    // // Buscar CEPs únicos por cidade
    // @Query("SELECT DISTINCT e.cep FROM Endereco e WHERE e.cidade = :cidade")
    // List<String> findDistinctCepByCidade(@Param("cidade") String cidade);
}


