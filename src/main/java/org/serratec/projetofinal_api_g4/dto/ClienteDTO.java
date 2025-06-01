package org.serratec.projetofinal_api_g4.dto;


import org.hibernate.validator.constraints.br.CPF;
import org.serratec.projetofinal_api_g4.domain.Cliente;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDTO {

    private Long id;

    @NotBlank(message = "O nome do cliente é obrigatório")
    @Size(min = 2, max = 100, message = "O nome do cliente deve ter entre 2 e 100 caracteres")
    private String nome;

    @Email(message = "Email deve ter um formato válido")
    @NotBlank(message = "O email do cliente é obrigatório")
    private String email;

    @NotBlank(message = "O telefone do cliente é obrigatório")
    @Pattern(regexp = "\\(\\d{2}\\)\\s\\d{4,5}-\\d{4}", message = "Telefone deve estar no formato (XX) XXXXX-XXXX")
    private String telefone;

    @CPF(message = "CPF deve ser válido")
    @NotBlank(message = "O CPF do cliente é obrigatório")
    @Pattern(regexp = "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}|\\d{11}", message = "CPF deve estar no formato XXX.XXX.XXX-XX ou apenas números")
    private String cpf;

    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 6, message = "A senha deve ter pelo menos 6 caracteres")
    private String senha;

    @Valid
    private EnderecoDTO endereco;

    public ClienteDTO(Cliente cliente) {
        this.id = cliente.getId();
        this.nome = cliente.getNome();
        this.email = cliente.getEmail();
        this.telefone = cliente.getTelefone();
        this.cpf = cliente.getCpf();
        this.senha = null; // Evita trazer a senha no retorno
        this.endereco = cliente.getEndereco() != null ? EnderecoDTO.fromEntity(cliente.getEndereco()) : null;
    }

    // Método opcional: cria um Cliente "desanexado" para inserir
    public Cliente toNewEntity() {
        Cliente cliente = new Cliente();
        cliente.setNome(this.nome);
        cliente.setEmail(this.email);
        cliente.setTelefone(this.telefone);
        cliente.setCpf(this.cpf);
        cliente.setSenha(this.senha);
        cliente.setEndereco(this.endereco != null ? this.endereco.toEntity() : null);
        return cliente;
    }
}

