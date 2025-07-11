package org.serratec.projetofinal_api_g4.service;

import java.util.Collections;
import org.serratec.projetofinal_api_g4.domain.Funcionario;
import org.serratec.projetofinal_api_g4.repository.FuncionarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class FuncionarioDetailsService implements UserDetailsService {

    @Autowired
        private FuncionarioRepository funcionarioRepository;

@Override
public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    Funcionario funcionario = funcionarioRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Email não encontrado: " + email));

    // Codifica no username: id:nome:email
    String usernameWithDetails = funcionario.getId() + ":" + funcionario.getNome() + ":" + funcionario.getEmail();

    return new User(
            usernameWithDetails,
            funcionario.getSenha(),
            Collections.singleton(new SimpleGrantedAuthority("ROLE_" + funcionario.getTipoFuncionario().name()))
    );
}
}
