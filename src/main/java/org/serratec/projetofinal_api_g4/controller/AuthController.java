package org.serratec.projetofinal_api_g4.controller;

import org.serratec.projetofinal_api_g4.config.JwtUtil;
import org.serratec.projetofinal_api_g4.dto.LoginDTO;
import org.serratec.projetofinal_api_g4.dto.LoginResponseDTO;
import org.serratec.projetofinal_api_g4.domain.Cliente;
import org.serratec.projetofinal_api_g4.domain.Funcionario;
import org.serratec.projetofinal_api_g4.repository.ClienteRepository;
import org.serratec.projetofinal_api_g4.repository.FuncionarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @PostMapping("/login-cliente")
    public ResponseEntity<LoginResponseDTO> loginCliente(@Valid @RequestBody LoginDTO loginDTO) {
        return autenticarUsuario(loginDTO, "CLIENTE");
    }

    @PostMapping("/login-funcionario")
    public ResponseEntity<LoginResponseDTO> loginFuncionario(@Valid @RequestBody LoginDTO loginDTO) {
        return autenticarUsuario(loginDTO, "FUNCIONARIO");
    }

    private ResponseEntity<LoginResponseDTO> autenticarUsuario(LoginDTO loginDTO, String tipo) {
    try {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getSenha()));

        String token = jwtUtil.generateToken(authentication);

        String id, nome, email, role;

        if ("CLIENTE".equalsIgnoreCase(tipo)) {
            Cliente cliente = clienteRepository.findByEmail(loginDTO.getEmail())
                    .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
            id = cliente.getId().toString();
            nome = cliente.getNome();
            email = cliente.getEmail();
            role = "CLIENTE";
        } else if ("FUNCIONARIO".equalsIgnoreCase(tipo)) {
            Funcionario funcionario = funcionarioRepository.findByEmail(loginDTO.getEmail())
                    .orElseThrow(() -> new RuntimeException("Funcionário não encontrado"));
            id = funcionario.getId().toString();
            nome = funcionario.getNome();
            email = funcionario.getEmail();
            role = funcionario.getTipoFuncionario().name();
        } else {
            throw new RuntimeException("Tipo de usuário inválido");
        }

        LoginResponseDTO response = new LoginResponseDTO(
                id, nome, email, role, token, true, "Login realizado com sucesso!");

        return ResponseEntity.ok().header("Authorization", "Bearer " + token).body(response);

    } catch (BadCredentialsException e) {
        return ResponseEntity.status(401).body(new LoginResponseDTO(
                null, null, loginDTO.getEmail(), tipo, null, false, "Credenciais inválidas."));
    } catch (RuntimeException e) {
        return ResponseEntity.status(404).body(new LoginResponseDTO(
                null, null, loginDTO.getEmail(), tipo, null, false, e.getMessage()));
    } catch (Exception e) {
        return ResponseEntity.status(500).body(new LoginResponseDTO(
                null, null, loginDTO.getEmail(), tipo, null, false, "Erro inesperado durante o login."));
    }
}
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    public String getEmailUsuarioLogado() {
        if (auth != null && auth.isAuthenticated()) {
            return auth.getName();
        }
        return null;
    }
}