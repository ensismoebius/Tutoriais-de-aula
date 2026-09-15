package jogo.model;

public class EntidadeInvalidaException extends RuntimeException {
    public EntidadeInvalidaException(String mensagem) {
        super(mensagem);
    }
}
