package org.serratec.projetofinal_api_g4.domain;

import org.hibernate.validator.constraints.br.CPF;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Entity
@Table(name = "cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @NotBlank(message = "O nome do cliente é obrigatório")
    private String nome;

    @Email(message = "O email do cliente é obrigatório")
    @NotBlank(message = "O email do cliente é obrigatório")
    @Column(unique = true)
    private String email;

    @NotBlank(message = "O telefone do cliente é obrigatório")
    @Pattern(regexp = "\\d{11}", message = "O telefone do cliente deve conter 11 dígitos")
    private String telefone; 
    

    @CPF(message = "O CPF do cliente é obrigatório")
    @NotBlank(message = "O CPF do cliente é obrigatório")
    @Column(unique = true)
    private String cpf;

    @Embedded
    private Endereco endereco;

}
