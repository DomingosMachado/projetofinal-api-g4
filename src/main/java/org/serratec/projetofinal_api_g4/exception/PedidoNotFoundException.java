package org.serratec.projetofinal_api_g4.exception;

public class PedidoNotFoundException extends RuntimeException {

    public PedidoNotFoundException(String message) {
        super(message);
    }

    public PedidoNotFoundException(Long id) {
        super("Pedido não encontrado. Id: " + id);
    }
}
