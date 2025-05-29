package org.serratec.projetofinal_api_g4.exception;

public class EnderecoNotFoundException extends RuntimeException {
    
    public EnderecoNotFoundException(String message) {
        super(message);
    }
    
    public EnderecoNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
