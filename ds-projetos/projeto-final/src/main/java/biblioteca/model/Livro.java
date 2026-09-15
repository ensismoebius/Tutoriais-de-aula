package biblioteca.model;

public class Livro extends ItemDeAcervo {
    private String autor;
    private int paginas;

    public Livro(String titulo, int ano, int avaliacao, String autor, int paginas) {
        super(titulo, ano, avaliacao);
        this.autor = autor;
        this.paginas = paginas;
    }

    @Override
    public String getCategoria() {
        return "Livro";
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public int getPaginas() {
        return paginas;
    }

    public void setPaginas(int paginas) {
        this.paginas = paginas;
    }
}
