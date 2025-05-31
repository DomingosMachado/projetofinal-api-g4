package org.serratec.projetofinal_api_g4.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

import java.util.List;

@Configuration
public class OpenAPIConfig {
    
    @org.springframework.beans.factory.annotation.Value("${springdoc.api.dev.url}")
    private String devUrl;
    
    @org.springframework.beans.factory.annotation.Value("${springdoc.api.prod.url}")
    private String prodUrl;
    
    @Bean
    public OpenAPI myOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl(devUrl);
        devServer.setDescription("URL do servidor de desenvolvimento");
        
        Server prodServer = new Server();
        prodServer.setUrl(prodUrl);
        prodServer.setDescription("URL do servidor de produção");
        
        Contact contact = new Contact();
        contact.setEmail("lelezinhateles@gmail.com");
        contact.setName("Grupo 4");
        contact.setUrl("https://github.com/DomingosMachado/projetofinal-api-g4");
        
        License apacheLicense = new License();
        apacheLicense.name("Apache 2.0");
        apacheLicense.url("http://www.apache.org/licenses/LICENSE-2.0.html");
        
        Info info = new Info()
            .title("API E-Commerce - Grupo 4")
            .version("1.0.0")
            .contact(contact)
            .description("API para gerenciamento de e-commerce com produtos, clientes e pedidos")
            .termsOfService("https://github.com/DomingosMachado/projetofinal-api-g4")
            .license(apacheLicense);
        
        return new OpenAPI()
            .info(info)
            .servers(List.of(devServer, prodServer));
    }
}