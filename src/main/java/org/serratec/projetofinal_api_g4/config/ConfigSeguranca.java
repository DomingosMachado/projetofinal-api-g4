package org.serratec.projetofinal_api_g4.config;

import java.util.Arrays;

import org.serratec.projetofinal_api_g4.security.JwtAuthenticationFilter;
import org.serratec.projetofinal_api_g4.security.JwtAuthorizationFilter;
import org.serratec.projetofinal_api_g4.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class ConfigSeguranca {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {// Configurações de segurança HTTP
        http.csrf(csrf -> csrf.disable())//Desativa a proteção CSRF, pois estamos usando JWT
            .cors(cors -> cors.configurationSource(corsConfigurationsource()))//Permite que o sistema aceite requisições de origens diferentes
            .httpBasic(Customizer.withDefaults())
            .authorizeHttpRequests(authorize ->//Tudo que está abaixo são regras de acesso 
               authorize
                    .requestMatchers(HttpMethod.GET, "/funcionarios").permitAll()
                    .requestMatchers(HttpMethod.GET, "/enderecos/**").permitAll()
                    .requestMatchers(HttpMethod.POST, "/clientes").permitAll()
                    .requestMatchers(HttpMethod.GET, "/clientes/{id}").hasAuthority("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/funcionarios/nome").hasAnyAuthority("ADMIN", "USER")
                    .anyRequest().authenticated()//Qualquer outra requisição deve ser autenticada
)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));//Isso significa que não vamos usar sessões HTTP, pois estamos usando JWT para autenticação.

        // Criação dos filtros de autenticação e autorização JWT
        JwtAuthenticationFilter jwtAuthenticationFilter =
            new JwtAuthenticationFilter(authenticationManager(http.getSharedObject(AuthenticationConfiguration.class)), jwtUtil);
        jwtAuthenticationFilter.setFilterProcessesUrl("/login");// Define a URL de login para o filtro de autenticação JWT
    
        // Criação do filtro de autorização JWT
        JwtAuthorizationFilter jwtAuthorizationFilter =
            new JwtAuthorizationFilter(authenticationManager(http.getSharedObject(AuthenticationConfiguration.class)), jwtUtil, userDetailsService);

        //E adiciona os filtros de autenticação e autorização JWT à cadeia de filtros do Spring Security    
        http.addFilter(jwtAuthenticationFilter);
        http.addFilter(jwtAuthorizationFilter);

        return http.build();//E aqui retornamos a configuração de segurança construída
    }

    // Configuração do AuthenticationManager para autenticação
      @Bean
      public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
          return authenticationConfiguration.getAuthenticationManager();
      }

      //Este método configura as regras de CORS (Cross-Origin Resource Sharing) para permitir que o frontend(localhost:3000) acesse a API(localhost:8080). 
    @Bean
     public CorsConfigurationSource corsConfigurationsource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration.applyPermitDefaultValues());
        return source;
    }

    //E este método é usado para codificar senhas de forma segura.
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
