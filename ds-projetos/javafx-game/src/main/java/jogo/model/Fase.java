package jogo.model;

public class Fase {
    private int id;
    private String nome;
    private int dificuldade; // 1 a 5
    private double gravidade;

    public Fase(String nome, int dificuldade, double gravidade) {
        this.nome = nome;
        this.dificuldade = dificuldade;
        this.gravidade = gravidade;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getDificuldade() {
        return dificuldade;
    }

    public void setDificuldade(int dificuldade) {
        this.dificuldade = dificuldade;
    }

    public double getGravidade() {
        return gravidade;
    }

    public void setGravidade(double gravidade) {
        this.gravidade = gravidade;
    }
}
