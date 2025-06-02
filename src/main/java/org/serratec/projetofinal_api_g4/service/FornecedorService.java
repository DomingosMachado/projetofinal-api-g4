package org.serratec.projetofinal_api_g4.service;

import java.util.List;
import java.util.stream.Collectors;

import org.serratec.projetofinal_api_g4.domain.Fornecedor;
import org.serratec.projetofinal_api_g4.dto.FornecedorDTO;
import org.serratec.projetofinal_api_g4.repository.FornecedorRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class FornecedorService {

    private final FornecedorRepository fornecedorRepository;

    public FornecedorService(FornecedorRepository fornecedorRepository) {
        this.fornecedorRepository = fornecedorRepository;
    }

    // Esse método retorna uma lista com todos os fornecedores cadastrados
    public List<FornecedorDTO> listarTodos() {
        return fornecedorRepository.findAll().stream()
                .map(this::toDTO) // Converte cada Fornecedor para FornecedorDTO
                .collect(Collectors.toList());// Junta tudo em uma lista
    }

    // Busca um fornecedor pelo ID. Se não encontrar, lança erro 404
    public FornecedorDTO buscarPorId(Long id) {
        Fornecedor fornecedor = fornecedorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fornecedor não encontrado"));
        return toDTO(fornecedor);
    }

    // Insere um novo fornecedor. Verifica se o CNPJ já existe antes de salvar
    public FornecedorDTO inserir(FornecedorDTO dto) {
        if (fornecedorRepository.existsByCnpj(dto.getCnpj())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CNPJ já cadastrado");
        }

        Fornecedor fornecedor = toEntity(dto);// Converte o DTO para entidade
        fornecedor = fornecedorRepository.save(fornecedor);// Salva no banco
        return toDTO(fornecedor);// Retorna o objeto salvo, já convertido
    }

    // Atualiza os dados de um fornecedor existente
    public FornecedorDTO atualizar(Long id, FornecedorDTO dto) {
        Fornecedor fornecedor = fornecedorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fornecedor não encontrado"));

        // Atualiza os campos        
        fornecedor.setNome(dto.getNome());
        fornecedor.setCnpj(dto.getCnpj());

        //Retorna o fornecedor atualizado
        return toDTO(fornecedorRepository.save(fornecedor));
    }

    public void deletar(Long id) {
        if (!fornecedorRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Fornecedor não encontrado");
        }
        fornecedorRepository.deleteById(id);
    }

    // Converte um Fornecedor (entidade) para FornecedorDTO
    private FornecedorDTO toDTO(Fornecedor fornecedor) {
        return new FornecedorDTO(fornecedor.getId(), fornecedor.getNome(), fornecedor.getCnpj());
    }

     // Converte um FornecedorDTO para Fornecedor (entidade)
    private Fornecedor toEntity(FornecedorDTO dto) {
        return new Fornecedor(dto.getId(), dto.getNome(), dto.getCnpj());
    }
}
