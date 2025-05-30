package org.serratec.projetofinal_api_g4.domain;

import org.serratec.projetofinal_api_g4.enums.TipoFuncionario;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "funcionario")
public class Funcionario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome do funcionário é obrigatório.")
    private String nome;

    @NotBlank(message = "O e-mail do funcionário é obrigatório.")
    @Email(message = "O e-mail deve ser válido.")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "A senha é obrigatória.")
    private String senha;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "O tipo de funcionário é obrigatório.")
    private TipoFuncionario tipoFuncionario;

    
}
