package org.serratec.projetofinal_api_g4.security;

import java.io.IOException;



import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

//O que essa classe faz?
//Ela recebe um token JWT, verifica se eé verdadeiro,descobre de quem é o token, e só libera o acesso se o token se der tudo certo. 
public class JwtAuthorizationFilter extends BasicAuthenticationFilter{

  private JwtUtil jwtUtil;

  private UserDetailsService userDetailsService;

  // Construtor que recebe o AuthenticationManager(é como um chefe de segurança), 
  //JwtUtil(é como se fosse uma caixinha de ferramentas , que sabe como verificar se a chave é válida)
  // e UserDetailsService(é como se fosse um cadastro de moradores).
  public JwtAuthorizationFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil, 
  UserDetailsService userDetailsService) {
    super(authenticationManager);
    this.jwtUtil = jwtUtil;
    this.userDetailsService = userDetailsService;
  }


  //Este método é chamado sempre que uma requisição chega ao servidor.
  @Override
  protected void doFilterInternal(HttpServletRequest request,HttpServletResponse response,FilterChain chain)
    throws IOException, ServletException {
      String header = request.getHeader(HttpHeaders.AUTHORIZATION);//Recebe o cabeçalho de autorização da requisição.
      if(header != null && header.startsWith("Bearer")){//Verifica se o cabeçalho não é nulo e começa com "Bearer" (que é como os tokens JWT são enviados).
        UsernamePasswordAuthenticationToken auth = getAuthentication(header.substring(7));
        if(auth != null){//Se a autenticação for bem-sucedida, cria um token de autenticação.
          SecurityContextHolder.getContext().setAuthentication(auth);//Armazena o token de autenticação no contexto de segurança do Spring.
        }
      }
      super.doFilterInternal(request, response, chain);//Chama o método pai para continuar o processamento da requisição.
    }

    // Este método verifica se o token é válido e retorna um token de autenticação, obtém o nome de usuário associado a ele e carrega os detalhes do usuário.
    private UsernamePasswordAuthenticationToken getAuthentication(String token) {
      if(jwtUtil.isValidToken(token)){// Verifica se o token é válido usando o JwtUtil.
        String username = jwtUtil.getUserName(token);// Obtém o nome de usuário do token.
        UserDetails user = userDetailsService.loadUserByUsername(username);// Carrega os detalhes do usuário usando o UserDetailsService.
        return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());// Cria um token de autenticação com os detalhes do usuário e suas autoridades (permissões).
      }  
      return null;
    }
  }




    
  
