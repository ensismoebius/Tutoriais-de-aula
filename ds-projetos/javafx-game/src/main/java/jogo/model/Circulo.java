package jogo.model;

public class Circulo extends Entidade {

    public Circulo(double x, double y, double tamanho, double vx, double vy) {
        super(x, y, tamanho, vx, vy);
    }

    public Circulo(double x, double y, double tamanho) {
        this(x, y, tamanho, 0, 0);
    }

    public Circulo() {
        this(400, 300, 30);
    }
}
