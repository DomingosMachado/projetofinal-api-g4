package org.serratec.projetofinal_api_g4.exception;

public class EnderecoNotFoundException extends RuntimeException {
    
    public EnderecoNotFoundException(String message) {
        super(message);
    }
    
    public EnderecoNotFoundException(Long id) {
        super("Endereço não encontrado com ID: " + id);
    }
    
    public EnderecoNotFoundException(String cep, String message) {
        super("Endereço não encontrado para CEP " + cep + ": " + message);
    }
}
