package biblioteca.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Model puro (sem javafx.*): regras de negócio sobre o acervo, testável isoladamente. */
public class GerenciadorDeAcervo {

    private List<ItemDeAcervo> itens;

    public GerenciadorDeAcervo(List<ItemDeAcervo> itensIniciais) {
        this.itens = new ArrayList<>(itensIniciais);
    }

    public GerenciadorDeAcervo() {
        this(new ArrayList<>());
    }

    public void adicionar(ItemDeAcervo item) {
        if (item == null) {
            throw new ItemInvalidoException("Item não pode ser nulo");
        }
        if (item.getTitulo() == null || item.getTitulo().isBlank()) {
            throw new ItemInvalidoException("Item precisa de um título");
        }
        itens.add(item);
    }

    public List<ItemDeAcervo> getItens() {
        return itens;
    }

    /** Uso não trivial de Map: quantos itens existem por categoria (requisito de Coleção). */
    public Map<String, Long> contarPorCategoria() {
        Map<String, Long> contagem = new HashMap<>();
        for (ItemDeAcervo item : itens) {
            contagem.merge(item.getCategoria(), 1L, Long::sum);
        }
        return contagem;
    }

    public double mediaDeAvaliacao() {
        if (itens.isEmpty()) return 0;
        int soma = 0;
        for (ItemDeAcervo item : itens) {
            soma += item.getAvaliacao();
        }
        return (double) soma / itens.size();
    }

    public List<ItemDeAcervo> filtrarPorCategoria(String categoria) {
        List<ItemDeAcervo> resultado = new ArrayList<>();
        for (ItemDeAcervo item : itens) {
            if (item.getCategoria().equals(categoria)) {
                resultado.add(item);
            }
        }
        return resultado;
    }
}
