package org.serratec.projetofinal_api_g4.domain;

<<<<<<< HEAD
import java.util.List;
=======
import java.util.Optional;
>>>>>>> origin/Teste

import org.hibernate.validator.constraints.br.CPF;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.Data;


@Data
@Entity
@Table(name = "cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @NotBlank(message = "O nome do cliente é obrigatório")
    @Size(min = 2, max = 100, message = "O nome do cliente deve ter entre 2 e 100 caracteres")
    @Column(nullable = false)
    private String nome;

    @Email(message = "O email do cliente é obrigatório")
    @NotBlank(message = "O email do cliente é obrigatório")
    @Column(unique = true)
    private String email;

    @NotBlank(message = "O telefone do cliente é obrigatório")
    @Pattern(regexp = "\\(\\d{2}\\)\\s\\d{4,5}-\\d{4}", message = "Telefone deve estar no formato (XX) XXXXX-XXXX")
    private String telefone; 
    

    @CPF(message = "O CPF do cliente é obrigatório")
    @NotBlank(message = "O CPF do cliente é obrigatório")
    @Pattern(regexp = "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}", message = "CPF deve estar no formato XXX.XXX.XXX-XX")
    @Column(unique = true)
    private String cpf;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private List<Pedido> pedidos;
    
    @Embedded
    private Endereco endereco;

    public Optional<Cliente> getPedidos() {
        return null; // MEUS DEUS TEM QUE MUDAR ISSO!!!
    }

}
