package jogo.model;

public class LimiteDeFaseExcedidoException extends RuntimeException {
    private final int limite;

    public LimiteDeFaseExcedidoException(int limite) {
        super("Limite de " + limite + " entidades por fase excedido");
        this.limite = limite;
    }

    public int getLimite() {
        return limite;
    }
}
