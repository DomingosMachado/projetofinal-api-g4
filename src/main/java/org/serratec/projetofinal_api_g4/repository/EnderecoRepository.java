package org.serratec.projetofinal_api_g4.repository;

import java.util.Optional;

import org.serratec.projetofinal_api_g4.domain.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnderecoRepository extends JpaRepository<Endereco, Long> {

    Optional<Endereco> findByCep(String cep);
  

  

}
