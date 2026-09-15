package jogo.model;

public class Quadrado extends Entidade {

    public Quadrado(double x, double y, double tamanho, double vx, double vy) {
        super(x, y, tamanho, vx, vy);
    }

    public Quadrado(double x, double y, double tamanho) {
        this(x, y, tamanho, 0, 0);
    }

    public Quadrado() {
        this(400, 300, 30);
    }
}
