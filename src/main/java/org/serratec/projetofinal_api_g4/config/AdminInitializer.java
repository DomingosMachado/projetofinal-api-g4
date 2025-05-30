package org.serratec.projetofinal_api_g4.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import org.serratec.projetofinal_api_g4.domain.Funcionario;
import org.serratec.projetofinal_api_g4.enums.TipoFuncionario;
import org.serratec.projetofinal_api_g4.repository.FuncionarioRepository;

@Component
public class AdminInitializer implements CommandLineRunner {
    
    @Autowired
    private FuncionarioRepository funcionarioRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Verifica se já existe um admin
        boolean adminExists = funcionarioRepository.existsByEmail("admin@email.com");
        
        if (!adminExists) {
            Funcionario admin = new Funcionario();
            admin.setNome("Administrador");
            admin.setEmail("admin@email.com");
            admin.setSenha(passwordEncoder.encode("admin123"));
            admin.setTipoFuncionario(TipoFuncionario.ADMIN);
            
            funcionarioRepository.save(admin);
            System.out.println("Admin criado com sucesso!");
            System.out.println("Email: admin@email.com");
            System.out.println("Senha: admin123");
        } else {
            System.out.println("Admin já existe no sistema.");
        }
    }
}