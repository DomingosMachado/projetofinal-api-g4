package org.serratec.projetofinal_api_g4.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.serratec.projetofinal_api_g4.service.ClienteDetailsService;
import org.serratec.projetofinal_api_g4.service.FuncionarioDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Lazy
    @Autowired
    private FuncionarioDetailsService funcionarioDetailsService;

    @Lazy
    @Autowired
    private ClienteDetailsService clienteDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt) && jwtUtil.validateToken(jwt)) {
                String usernameWithDetails = jwtUtil.getUsernameFromToken(jwt);
                String[] parts = usernameWithDetails.split(":");
                String email = parts[parts.length - 1];

                // Tenta carregar o usuário (funcionário ou cliente)
                UserDetails userDetails = null;
                try {
                    userDetails = funcionarioDetailsService.loadUserByUsername(email);
                } catch (UsernameNotFoundException exFuncionario) {
                    userDetails = clienteDetailsService.loadUserByUsername(email);
                }

                // Extrai as authorities do JWT
                Map<String, Object> claims = jwtUtil.getAllClaimsFromToken(jwt);
                List<String> authorities = (List<String>) claims.get("authorities");
                List<GrantedAuthority> grantedAuthorities = authorities.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                // Se a autenticação ainda não foi definida, cria o token
                if (SecurityContextHolder.getContext().getAuthentication() == null && userDetails != null) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, grantedAuthorities);
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception e) {
            logger.error("Não foi possível definir a autenticação do usuário: " + e.getMessage(), e);
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}