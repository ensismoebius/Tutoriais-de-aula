package biblioteca.model;

import java.util.UUID;

/** Hierarquia (requisito 1 do Projeto Final): base para tudo que entra no acervo pessoal. */
public abstract class ItemDeAcervo {
    private String id = UUID.randomUUID().toString();
    private String titulo;
    private int ano;
    private int avaliacao; // 0 a 5

    protected ItemDeAcervo(String titulo, int ano, int avaliacao) {
        this.titulo = titulo;
        this.ano = ano;
        this.avaliacao = avaliacao;
    }

    /** Cada subtipo descreve a si mesmo de um jeito específico — polimorfismo em ação. */
    public abstract String getCategoria();

    public String getId() {
        return id;
    }

    /** Usado só pelo DAO ao reconstruir um item já existente vindo do banco. */
    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public int getAvaliacao() {
        return avaliacao;
    }

    public void setAvaliacao(int avaliacao) {
        this.avaliacao = avaliacao;
    }
}
