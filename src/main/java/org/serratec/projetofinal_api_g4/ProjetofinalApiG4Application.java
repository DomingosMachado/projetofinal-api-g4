package org.serratec.projetofinal_api_g4;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;


@SpringBootApplication
public class ProjetofinalApiG4Application {

	public static void main(String[] args) {
		SpringApplication.run(ProjetofinalApiG4Application.class, args);
	}

	 @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
