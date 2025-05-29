package org.serratec.projetofinal_api_g4.service;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.serratec.projetofinal_api_g4.controller.CategoriaController;
import org.serratec.projetofinal_api_g4.domain.Funcionario;
import org.serratec.projetofinal_api_g4.domain.Produto;
import org.serratec.projetofinal_api_g4.dto.FuncionarioDTO;
import org.serratec.projetofinal_api_g4.enums.TipoFuncionario;
import org.serratec.projetofinal_api_g4.repository.FuncionarioRepository;
import org.serratec.projetofinal_api_g4.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import jakarta.transaction.Transactional;

@Service
public class FuncionarioService {

    private final CategoriaController categoriaController;

    @Autowired  
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    FuncionarioService(CategoriaController categoriaController) {
        this.categoriaController = categoriaController;
    }

     @Transactional
    public FuncionarioDTO inserir(FuncionarioDTO dto) {
        if (funcionarioRepository.existsByEmail(dto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email já cadastrado");
        }

        Funcionario funcionario = new Funcionario();
        funcionario.setNome(dto.getNome());
        funcionario.setEmail(dto.getEmail());
        funcionario.setSenha(passwordEncoder.encode(dto.getSenha())); // Criptografar senha
        funcionario.setTipoFuncionario(dto.getTipoFuncionario());

        funcionario = funcionarioRepository.save(funcionario);
        dto.setId(funcionario.getId());
        dto.setSenha(null); // Não retornar a senha
        return dto;
    }

    @Transactional
    public FuncionarioDTO atualizar(Long id, FuncionarioDTO dto) {
        Funcionario funcionario = funcionarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Funcionário não encontrado"));

        funcionario.setNome(dto.getNome());
        funcionario.setEmail(dto.getEmail());
        if (dto.getSenha() != null && !dto.getSenha().isEmpty()) {
            funcionario.setSenha(passwordEncoder.encode(dto.getSenha())); // Atualiza senha se fornecida
        }
        funcionario.setTipoFuncionario(dto.getTipoFuncionario());

        funcionario = funcionarioRepository.save(funcionario);
        dto.setSenha(null); // Não retornar a senha
        return dto;
    }

    @Transactional
    public FuncionarioDTO buscarPorId(Long id) {
        Funcionario funcionario = funcionarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Funcionário não encontrado"));

        return new FuncionarioDTO(funcionario.getId(), funcionario.getNome(), funcionario.getEmail(), null, funcionario.getTipoFuncionario());
    }

    @Transactional
    public List<FuncionarioDTO> listarTodos() {
        List<Funcionario> funcionarios = funcionarioRepository.findAll();
        return funcionarios.stream()
                .map(f -> new FuncionarioDTO(f.getId(), f.getNome(), f.getEmail(), null, f.getTipoFuncionario()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deletar(Long id) {
        if (!funcionarioRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Funcionário não encontrado");
        }
        funcionarioRepository.deleteById(id);
    }

    public Funcionario salvarFuncionario(Funcionario funcionario) {
        
        return funcionarioRepository.save(funcionario);
    }

     @Transactional
    public Produto cadastrarProduto(Long funcionarioId, Produto produto) {
        Funcionario funcionario = funcionarioRepository.findById(funcionarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Funcionário não encontrado"));

        if (funcionario.getTipoFuncionario() != TipoFuncionario.ADMIN &&
            funcionario.getTipoFuncionario() != TipoFuncionario.ESTOQUISTA) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas Estoquista ou Admin podem cadastrar produtos");
        }

        return produtoRepository.save(produto);
    }
}