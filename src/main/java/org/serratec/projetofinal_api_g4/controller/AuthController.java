package org.serratec.projetofinal_api_g4.controller;


import org.serratec.projetofinal_api_g4.config.JwtUtil;
import org.serratec.projetofinal_api_g4.dto.LoginDTO;
import org.serratec.projetofinal_api_g4.dto.LoginResponseDTO;
import org.serratec.projetofinal_api_g4.domain.Cliente;
import org.serratec.projetofinal_api_g4.domain.Funcionario;
import org.serratec.projetofinal_api_g4.repository.ClienteRepository;
import org.serratec.projetofinal_api_g4.repository.FuncionarioRepository;
import org.serratec.projetofinal_api_g4.service.ClienteDetailsService;
import org.serratec.projetofinal_api_g4.service.FuncionarioDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
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
    private ClienteDetailsService clienteDetailsService;

    @Autowired
    private FuncionarioDetailsService funcionarioDetailsService;

    @Autowired
    private ClienteRepository clienteRepository;   // Injetado para busca do Cliente

    @Autowired
    private FuncionarioRepository funcionarioRepository;  // Injetado para busca do Funcionario

    @PostMapping("/login-cliente")
    public ResponseEntity<LoginResponseDTO> loginCliente(@Valid @RequestBody LoginDTO loginDTO) {
        try {
            // Autentica as credenciais
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getSenha())
            );

            UserDetails userDetails = clienteDetailsService.loadUserByUsername(loginDTO.getEmail());
            String token = jwtUtil.generateToken(authentication);

            // Busca cliente pelo e-mail
            Cliente cliente = clienteRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

            // Preenche o DTO de resposta
            LoginResponseDTO response = new LoginResponseDTO();
            response.setId(cliente.getId().toString());
            response.setNome(cliente.getNome());
            response.setEmail(cliente.getEmail());
            response.setRole("CLIENTE");
            response.setToken(token);
            response.setSucesso(true);
            response.setMensagem("Login realizado com sucesso!");

            return ResponseEntity.ok().header("Authorization", "Bearer " + token).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(new LoginResponseDTO(
                null, null, loginDTO.getEmail(), null, null, false, "Credenciais inválidas para cliente."
            ));
        }
    }

    @PostMapping("/login-funcionario")
    public ResponseEntity<LoginResponseDTO> loginFuncionario(@Valid @RequestBody LoginDTO loginDTO) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getSenha())
            );

            UserDetails userDetails = funcionarioDetailsService.loadUserByUsername(loginDTO.getEmail());
            String token = jwtUtil.generateToken(authentication);

            // Busca funcionario pelo e-mail
            Funcionario funcionario = funcionarioRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado"));

            // Preenche o DTO de resposta
            LoginResponseDTO response = new LoginResponseDTO();
            response.setId(funcionario.getId().toString());
            response.setNome(funcionario.getNome());
            response.setEmail(funcionario.getEmail());
            response.setRole("FUNCIONARIO");
            response.setToken(token);
            response.setSucesso(true);
            response.setMensagem("Login realizado com sucesso!");

            return ResponseEntity.ok().header("Authorization", "Bearer " + token).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(new LoginResponseDTO(
                null, null, loginDTO.getEmail(), null, null, false, "Credenciais inválidas para funcionário."
            ));
        }
    }
}