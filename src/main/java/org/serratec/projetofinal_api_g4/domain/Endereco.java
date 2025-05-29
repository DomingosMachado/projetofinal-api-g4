package org.serratec.projetofinal_api_g4.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Embeddable
public class Endereco {

    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_endereco")
	private Long id;

	private String cep;

	private String logradouro;

	private String complemento;

	private String bairro;

	private String numero;

	private String uf;

	private Long ibge;

    public String getCidade() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getCidade'");
    }

	public void setCidade(String localidade) {
		/
		throw new UnsupportedOperationException("Unimplemented method 'setCidade'");
	}

	
}
