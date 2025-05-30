package org.serratec.projetofinal_api_g4;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "API do Projeto Final G4", version = "1.0", description = "Documentação da API do Projeto Final"))
public class ProjetofinalApiG4Application {

	public static void main(String[] args) {
		SpringApplication.run(ProjetofinalApiG4Application.class, args);
	}

}
