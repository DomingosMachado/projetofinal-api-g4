package org.serratec.projetofinal_api_g4.security;

import java.io.IOException;

import org.serratec.projetofinal_api_g4.config.JwtUtil;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;



public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

  private JwtUtil jwtUtil;
  private UserDetailsService userDetailsService;


	public JwtAuthorizationFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil,
			UserDetailsService userDetailsService) {
		super(authenticationManager);
		this.jwtUtil = jwtUtil;
		this.userDetailsService = userDetailsService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		String header = request.getHeader(HttpHeaders.AUTHORIZATION);
		if(header!=null && header.startsWith("Bearer")) {
			UsernamePasswordAuthenticationToken auth = getAthentication(header.substring(7));
			if(auth != null ) {
				SecurityContextHolder.getContext().setAuthentication(auth);
			}
		}
		super.doFilterInternal(request, response, chain);
	}
	
	private UsernamePasswordAuthenticationToken getAthentication(String token) {
		if(jwtUtil.validateToken(token)) {
			String username = jwtUtil.getUsernameFromToken(token);
			UserDetails user = userDetailsService.loadUserByUsername(username);
			return new UsernamePasswordAuthenticationToken( user,null,user.getAuthorities());			
		}
		return null;
	}
}
