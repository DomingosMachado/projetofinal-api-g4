package org.serratec.projetofinal_api_g4.exception;

/**
 * Exceção lançada quando um produto não tem estoque suficiente para atender a um pedido
 */
public class EstoqueInsuficienteException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;

    public EstoqueInsuficienteException(String message) {
        super(message);
    }
    
    public EstoqueInsuficienteException(String message, Throwable cause) {
        super(message, cause);
    }
}
