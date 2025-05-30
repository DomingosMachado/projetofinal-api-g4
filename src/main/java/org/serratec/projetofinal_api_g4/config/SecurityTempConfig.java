package org.serratec.projetofinal_api_g4.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityTempConfig {

    @SuppressWarnings("removal")
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/api/**").permitAll() // Libera suas APIs
                .requestMatchers("/swagger-ui/**").permitAll() // Libera Swagger
                .requestMatchers("/api-docs/**").permitAll() // Libera API Docs
                .anyRequest().authenticated()
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/h2-console/**")
            )
            .headers(headers -> headers
                .frameOptions().disable() // Versão atualizada para permitir H2 Console
            );
        
        return http.build();
    }
}