package biblioteca.model;

public class Filme extends ItemDeAcervo {
    private String diretor;
    private int duracaoMinutos;

    public Filme(String titulo, int ano, int avaliacao, String diretor, int duracaoMinutos) {
        super(titulo, ano, avaliacao);
        this.diretor = diretor;
        this.duracaoMinutos = duracaoMinutos;
    }

    @Override
    public String getCategoria() {
        return "Filme";
    }

    public String getDiretor() {
        return diretor;
    }

    public void setDiretor(String diretor) {
        this.diretor = diretor;
    }

    public int getDuracaoMinutos() {
        return duracaoMinutos;
    }

    public void setDuracaoMinutos(int duracaoMinutos) {
        this.duracaoMinutos = duracaoMinutos;
    }
}
