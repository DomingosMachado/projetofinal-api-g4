package org.serratec.projetofinal_api_g4.config;

import org.serratec.projetofinal_api_g4.service.ClienteDetailsService;
import org.serratec.projetofinal_api_g4.service.FuncionarioDetailsService;
import org.serratec.projetofinal_api_g4.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)  // Habilita @PreAuthorize
@Component
public class SecurityConfig {

    @Lazy
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private ClienteDetailsService clienteDetailsService;

    @Autowired
    private FuncionarioDetailsService funcionarioDetailsService;

    @Autowired
    private PedidoService pedidoService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider clienteProvider = new DaoAuthenticationProvider(clienteDetailsService);
        clienteProvider.setPasswordEncoder(passwordEncoder());
    
        DaoAuthenticationProvider funcionarioProvider = new DaoAuthenticationProvider(funcionarioDetailsService);
        funcionarioProvider.setPasswordEncoder(passwordEncoder());
    
        return new ProviderManager(List.of(clienteProvider, funcionarioProvider));
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Adiciona CORS aqui
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/api-docs/**",
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/swagger-ui/index.html",
                    "/swagger-resources/**",
                    "/webjars/**",
                    "/configuration/**",
                    "/h2-console/**",
                    "/auth/**"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.disable())
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:8080", "https://seusite.com"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*")); // Todos os headers permitidos
        configuration.setAllowCredentials(true); // Permitir cookies e autenticação
        configuration.setMaxAge(3600L); // Cache de configuração CORS (em segundos)

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    public boolean isOwner(Long id) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
    
        String username;
        Object principal = authentication.getPrincipal();
        
        if (principal instanceof User) {
            username = ((User) principal).getUsername();
        } else if (principal instanceof String) {
            username = (String) principal;
        } else {
            return false;
        }
    

        String[] parts = username.split(":");
        if (parts.length < 1) {
            return false;
        }
    
        try {
            Long userId = Long.parseLong(parts[0]);
            return userId.equals(id);
        } catch (NumberFormatException e) {
            return false;
        }
    }

     public boolean isPedidoOwner(Long pedidoId) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        // Buscar pedido pelo id
        var pedidoOpt = pedidoService.buscarPorId(pedidoId);
        if (pedidoOpt.isEmpty()) {
            return false;
        }

        var pedido = pedidoOpt.get();

        // Extrair id do usuário autenticado do username (igual ao isOwner)
        String username;
        Object principal = authentication.getPrincipal();

        if (principal instanceof User) {
            username = ((User) principal).getUsername();
        } else if (principal instanceof String) {
            username = (String) principal;
        } else {
            return false;
        }

        String[] parts = username.split(":");
        if (parts.length < 1) {
            return false;
        }

        Long userId;
        try {
            userId = Long.parseLong(parts[0]);
        } catch (NumberFormatException e) {
            return false;
        }

        // Verifica se o pedido pertence ao cliente autenticado
        return pedido.getCliente() != null && userId.equals(pedido.getCliente().getId());
    }

}