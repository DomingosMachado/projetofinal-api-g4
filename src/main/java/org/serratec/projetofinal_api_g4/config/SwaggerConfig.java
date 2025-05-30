package org.serratec.projetofinal_api_g4.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API E-Commerce - Grupo 4")
                        .description("API para gerenciamento de e-commerce com produtos, clientes e pedidos")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Grupo 4")
                                .email("lelezinhateles@gmail.com")
                                .url("https://github.com/DomingosMachado/projetofinal-api-g4"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}
