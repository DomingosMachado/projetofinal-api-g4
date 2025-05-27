package org.serratec.projetofinal_api_g4.dto;

import org.serratec.projetofinal_api_g4.domain.Endereco;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
public class ClienteDTO {

  @Id
  private Long id;

  private String nome;

  private String email;

  private String telefone;

  private String cpf;

  private Endereco endereco;
}
