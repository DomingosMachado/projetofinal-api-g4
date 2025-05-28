package org.serratec.projetofinal_api_g4.dto;

import org.serratec.projetofinal_api_g4.domain.Cliente;
import org.serratec.projetofinal_api_g4.domain.Endereco;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import jakarta.persistence.Id;
import lombok.Data;

@NoArgsConstructor
@AllArgsConstructor 
@Data 
public class ClienteDTO {


  public ClienteDTO(Cliente cliente) {
      
    }

  @Id
  private Long id;

  private String nome;

  private String email;

  private String telefone;

  private String cpf;

  private Endereco endereco;
}
