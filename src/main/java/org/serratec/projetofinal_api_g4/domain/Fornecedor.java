package org.serratec.projetofinal_api_g4.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;

@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "fornecedor")
public class Fornecedor {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @EqualsAndHashCode.Include
  private Long id;


  @NotBlank(message = "O nome do fornecedor é obrigatório")
  @Size(max = 200, message = "O nome do fornecedor deve ter no máximo 200 caracteres")
  @Column(nullable = false, length = 200)
  private String nome;

  @NotBlank(message = "O CNPJ do fornecedor é obrigatório")
  @Size(min = 14, max = 18, message = "O CNPJ deve ter entre 14 e 18 caracteres")
  @Column(nullable = false, length = 18, unique = true)
  private String cnpj;

  @OneToMany(mappedBy = "fornecedor", cascade = CascadeType.ALL,orphanRemoval = true, fetch = FetchType.LAZY)
  private List<Produto> produtos = new ArrayList<>();

  @OneToMany(mappedBy = "fornecedor", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  private List<CompraFornecedor> compras = new ArrayList<>();
  
  public Fornecedor(Long id, String nome, String cnpj) {
    this.id = id;
    this.nome = nome;
    this.cnpj = cnpj;
}
public Fornecedor(){}
 
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

  public List<Produto> getProdutos() {
    return produtos;
  }


  //Metodos

  public void adicionarProduto(Produto produto) {
    produtos.add(produto);
    produto.setFornecedor(this); 
  }

  public void removerProduto(Produto produto) { 
    produtos.remove(produto);
    produto.setFornecedor(null);
  }


  @Override
  public String toString() {
    return "Fornecedor" +"id:" + id + ", nome:" + nome + ", cnpj:" + cnpj ;
  }

}
