package org.serratec.projetofinal_api_g4.service;

import org.serratec.projetofinal_api_g4.domain.Cliente;
import org.serratec.projetofinal_api_g4.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class ClienteDetailsService implements UserDetailsService {

        @Autowired
        private ClienteRepository clienteRepository;
@Override
public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    Cliente cliente = clienteRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Email não encontrado: " + email));

    String usernameWithDetails = cliente.getId() + ":" + cliente.getNome() + ":" + cliente.getEmail();

    return new User(
            usernameWithDetails,
            cliente.getSenha(),
            Collections.singleton(new SimpleGrantedAuthority("ROLE_CLIENTE"))
    );
}
}