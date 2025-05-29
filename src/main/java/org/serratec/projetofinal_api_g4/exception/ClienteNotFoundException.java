package org.serratec.projetofinal_api_g4.exception;

public class ClienteNotFoundException extends RuntimeException {
    public ClienteNotFoundException() {
        super();
    }

    public ClienteNotFoundException(String message) {
        super(message);
    }
}