package org.serratec.projetofinal_api_g4.dto;

import org.serratec.projetofinal_api_g4.domain.Funcionario;
import org.serratec.projetofinal_api_g4.enums.TipoFuncionario;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FuncionarioDTO {

    private Long id;
    private String nome;
    private String email;
    private String senha;
    private TipoFuncionario tipoFuncionario;

    // Construtor para converter entidade em DTO
    public FuncionarioDTO(Funcionario funcionario) {
        this.id = funcionario.getId();
        this.nome = funcionario.getNome();
        this.email = funcionario.getEmail();
        this.senha = null; // segurança: não expõe senha
        this.tipoFuncionario = funcionario.getTipoFuncionario();
    }

    // Método para converter DTO para entidade
    public Funcionario toEntity() {
        Funcionario funcionario = new Funcionario();
        funcionario.setId(this.id);
        funcionario.setNome(this.nome);
        funcionario.setEmail(this.email);
        funcionario.setSenha(this.senha);
        funcionario.setTipoFuncionario(this.tipoFuncionario);
        return funcionario;
    }
}