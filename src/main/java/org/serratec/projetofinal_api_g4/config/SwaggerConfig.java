package org.serratec.projetofinal_api_g4.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Api-Ecommerce Serratec")
                .version("1.0")
                .description("Api desenvolvida para gerenciamento do sistema de um e-commerce do projeto final")
                .termsOfService("http://swagger.io/terms/")
                .contact(new Contact()
                    .name("Leticia Teles")
                    .email("lelezinhateles@gmail.com")
                )
            );
    }
}


