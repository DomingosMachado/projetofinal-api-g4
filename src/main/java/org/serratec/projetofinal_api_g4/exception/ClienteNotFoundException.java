package org.serratec.projetofinal_api_g4.exception;

public class ClienteNotFoundException extends RuntimeException {
    
    public ClienteNotFoundException(String message) {
        super(message);
    }
    
    public ClienteNotFoundException(Long id) {
        super("Cliente não encontrado com ID: " + id);
    }
}