package biblioteca.model;

public class ItemInvalidoException extends RuntimeException {
    public ItemInvalidoException(String mensagem) {
        super(mensagem);
    }
}
