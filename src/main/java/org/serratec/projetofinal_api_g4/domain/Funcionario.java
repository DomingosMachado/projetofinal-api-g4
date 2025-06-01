package org.serratec.projetofinal_api_g4.domain;

import org.serratec.projetofinal_api_g4.enums.TipoFuncionario;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "funcionario")
public class Funcionario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank(message = "O nome do funcionário é obrigatório.")
    @Column(nullable = false)
    private String nome;

    @NotBlank(message = "O e-mail do funcionário é obrigatório.")
    @Email(message = "O e-mail deve ser válido.")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "A senha é obrigatória.")
    @Column(nullable = false)
    private String senha;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "O tipo de funcionário é obrigatório.")
    @Column(nullable = false)
    private TipoFuncionario tipoFuncionario;

}