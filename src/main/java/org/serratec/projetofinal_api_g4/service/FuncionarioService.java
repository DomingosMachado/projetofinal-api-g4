package org.serratec.projetofinal_api_g4.service;

import org.serratec.projetofinal_api_g4.domain.Funcionario;
import org.serratec.projetofinal_api_g4.domain.Produto;
import org.serratec.projetofinal_api_g4.enums.TipoFuncionario;
import org.serratec.projetofinal_api_g4.repository.FuncionarioRepository;
import org.serratec.projetofinal_api_g4.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FuncionarioService {

    @Autowired  
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    public Funcionario salvarFuncionario(Funcionario funcionario) {
        
        return funcionarioRepository.save(funcionario);
    }

    public Produto cadastrarProduto(Long funcionarioId, Produto produto) {
        Funcionario funcionario = funcionarioRepository.findById(funcionarioId)
        .orElseThrow(() -> new RuntimeException("Funcionário não encontrado"));

        if(funcionario.getTipoFuncionario() != TipoFuncionario.ADMIN && funcionario.getTipoFuncionario() != TipoFuncionario.ESTOQUISTA) {
            throw new RuntimeException("Apenas funcionários do tipo Estoquista ou Admin podem cadastrar produtos");
        }

        produtoRepository.save(produto);
        return produto;
              
    }
    
}