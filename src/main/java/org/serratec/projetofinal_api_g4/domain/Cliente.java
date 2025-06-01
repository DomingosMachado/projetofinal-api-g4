package org.serratec.projetofinal_api_g4.domain;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.constraints.br.CPF;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank(message = "O nome do cliente é obrigatório")
    @Size(min = 2, max = 100, message = "O nome do cliente deve ter entre 2 e 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nome;

    @Email(message = "Email deve ter um formato válido")
    @NotBlank(message = "O email do cliente é obrigatório")
    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @NotBlank(message = "O telefone do cliente é obrigatório")
    @Pattern(regexp = "\\(\\d{2}\\)\\s\\d{4,5}-\\d{4}", message = "Telefone deve estar no formato (XX) XXXXX-XXXX")
    @Column(nullable = false, length = 15)
    private String telefone;

    @CPF(message = "CPF deve ser válido")
    @NotBlank(message = "O CPF do cliente é obrigatório")
    @Pattern(regexp = "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}|\\d{11}", message = "CPF deve estar no formato XXX.XXX.XXX-XX ou apenas números")
    @Column(unique = true, nullable = false, length = 14)
    private String cpf;

    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 6, message = "A senha deve ter pelo menos 6 caracteres")
    private String senha;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Pedido> pedidos = new ArrayList<>();

    @Embedded
    private Endereco endereco;



    @Override
    public String toString() {
        return "Cliente{id=" + id + ", nome='" + nome + "', email='" + email + "', cpf='" + cpf + "'}";
    }

}