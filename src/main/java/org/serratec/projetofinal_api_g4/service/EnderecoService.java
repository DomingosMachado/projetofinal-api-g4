package org.serratec.projetofinal_api_g4.service;
import java.util.list;
import java.util.stream.Collectors;
import org.serratec.projetofinal_api_g4.domin.endereço;
import org.serratec.projetofinal_api_g4.dto.EnderecoDTO;
import org.serratec.projetofinal_api_g4.repository.EnderecoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.serratec.projetofinal_api_g4.service.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

<<<<<<< HEAD
import org.serratec.projetofinal_api_g4.dto.EnderecoDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
=======
@service
>>>>>>> 47591f464b8fcc9b39e8b93f69ab249e78e2d712
public class EnderecoService {
    @Autowired
    private EnderecoRepository enderecoRepository;

    @Transactional
    public EnderecoDTO buscarPorId(Long id) {
        Endereco endereco = enderecoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Endereço não encontrado. Id: " + id));
        return new EnderecoDTO(endereco);
    }

    @Transactional
    public EnderecoDTO inserir(EnderecoDTO enderecoDTO) {
        Endereco endereco = new Endereco();
        endereco.setLogradouro(enderecoDTO.getLogradouro());
        endereco.setNumero(enderecoDTO.getNumero());
        endereco.setBairro(enderecoDTO.getBairro());
        endereco.setCidade(enderecoDTO.getCidade());
        endereco.setEstado(enderecoDTO.getEstado());
        endereco.setCep(enderecoDTO.getCep());
        endereco = enderecoRepository.save(endereco);
        return new EnderecoDTO(endereco);
    }

    @Transactional
    public EnderecoDTO atualizar(EnderecoDTO enderecoDTO, Long id) {
        Endereco endereco = enderecoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Endereço não encontrado. Id: " + id));
        endereco.setLogradouro(enderecoDTO.getLogradouro());
        endereco.setNumero(enderecoDTO.getNumero());
        endereco.setBairro(enderecoDTO.getBairro());
        endereco.setCidade(enderecoDTO.getCidade());
        endereco.setEstado(enderecoDTO.getEstado());
        endereco.setCep(enderecoDTO.getCep());
        endereco = enderecoRepository.save(endereco);
        return new EnderecoDTO(endereco);
    }

    @Transactional
    public void deletar(Long id) {
        if (!enderecoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Endereço não encontrado. Id: " + id);
        }
        enderecoRepository.deleteById(id);
    }

    @Transactional
    public List<EnderecoDTO> listarTodos() {
        List<Endereco> enderecos = enderecoRepository.findAll();
        return enderecos.stream()
            .map(EnderecoDTO::new)
            .collect(Collectors.toList());
    }


<<<<<<< HEAD
    private static final String VIACEP_URL = "https://viacep.com.br/ws/{cep}/json/";
   
    public EnderecoDTO buscarEnderecoPorCep(String cep) {
       RestTemplate restTemplate = new RestTemplate();
        EnderecoDTO endereco = restTemplate.getForObject(VIACEP_URL, EnderecoDTO.class, cep);
        
        if (endereco == null || endereco.getCep() == null) {
            throw new RuntimeException("CEP não encontrado: " + cep);
        }
        
        return endereco;    
    }

}
=======
>>>>>>> 47591f464b8fcc9b39e8b93f69ab249e78e2d712
