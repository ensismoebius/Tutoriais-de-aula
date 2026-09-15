package jogo.model;

import java.util.UUID;

/**
 * Base para tudo que existe fisicamente no jogo: tem posição, velocidade e
 * tamanho, sofre gravidade e pode ser persistido (por isso implementa ComId).
 */
public abstract class Entidade implements ComId {

    private String id = UUID.randomUUID().toString();

    protected double x, y, vx, vy, tamanho;

    // GRAVIDADE é static: uma única cópia, compartilhada por todas as entidades.
    public static double GRAVIDADE = 0.5;

    private static int totalEntidades = 0;

    protected Entidade(double x, double y, double tamanho) {
        this(x, y, tamanho, 0, 0);
    }

    protected Entidade(double x, double y, double tamanho, double vx, double vy) {
        this.x = x;
        this.y = y;
        this.tamanho = tamanho;
        this.vx = vx;
        this.vy = vy;
        totalEntidades++;
    }

    /** Física comum a toda entidade: gravidade acelera, velocidade move. */
    public void atualizar() {
        vy += GRAVIDADE;
        y += vy;
        x += vx;
    }

    public static int getTotalEntidades() {
        return totalEntidades;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getVx() {
        return vx;
    }

    public void setVx(double vx) {
        this.vx = vx;
    }

    public double getVy() {
        return vy;
    }

    public void setVy(double vy) {
        this.vy = vy;
    }

    public double getTamanho() {
        return tamanho;
    }
}
