package org.serratec.projetofinal_api_g4.exception;

public class CategoriaNotFoundException extends RuntimeException {
    
    public CategoriaNotFoundException(Long id) {
        super("Categoria não encontrada com ID: " + id);
    }
}