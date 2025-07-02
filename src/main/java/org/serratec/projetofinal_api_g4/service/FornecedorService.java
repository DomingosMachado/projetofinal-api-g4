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

    public List<FornecedorDTO> listarTodos() {
        return fornecedorRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public FornecedorDTO buscarPorId(Long id) {
        Fornecedor fornecedor = fornecedorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fornecedor não encontrado"));
        return toDTO(fornecedor);
    }

    public FornecedorDTO inserir(FornecedorDTO dto) {
        if (fornecedorRepository.existsByCnpj(dto.getCnpj())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CNPJ já cadastrado");
        }

        Fornecedor fornecedor = toEntity(dto);
        fornecedor = fornecedorRepository.save(fornecedor);
        return toDTO(fornecedor);
    }

    public FornecedorDTO atualizar(Long id, FornecedorDTO dto) {
        Fornecedor fornecedor = fornecedorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fornecedor não encontrado"));

        fornecedor.setNome(dto.getNome());
        fornecedor.setCnpj(dto.getCnpj());

        return toDTO(fornecedorRepository.save(fornecedor));
    }

    public void deletar(Long id) {
        if (!fornecedorRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Fornecedor não encontrado");
        }
        fornecedorRepository.deleteById(id);
    }

    private FornecedorDTO toDTO(Fornecedor fornecedor) {
        return new FornecedorDTO(fornecedor.getId(), fornecedor.getNome(), fornecedor.getCnpj());
    }

    private Fornecedor toEntity(FornecedorDTO dto) {
        return new Fornecedor(dto.getId(), dto.getNome(), dto.getCnpj());
    }
}
