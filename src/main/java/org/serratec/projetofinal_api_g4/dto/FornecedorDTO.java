package org.serratec.projetofinal_api_g4.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class FornecedorDTO {

    private Long id;

    @NotBlank(message = "O nome do fornecedor é obrigatório")
    @Size(max = 200, message = "O nome deve ter no máximo 200 caracteres")
    private String nome;

    @NotBlank(message = "O CNPJ é obrigatório")
    @Size(min = 14, max = 18, message = "O CNPJ deve ter entre 14 e 18 caracteres")
    private String cnpj;

    public FornecedorDTO() {}

    public FornecedorDTO(Long id, String nome, String cnpj) {
        this.id = id;
        this.nome = nome;
        this.cnpj = cnpj;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }
}
