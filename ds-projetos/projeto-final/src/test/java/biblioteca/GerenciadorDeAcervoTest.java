package biblioteca;

import biblioteca.model.Filme;
import biblioteca.model.GerenciadorDeAcervo;
import biblioteca.model.ItemInvalidoException;
import biblioteca.model.Livro;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GerenciadorDeAcervoTest {

    private GerenciadorDeAcervo gerenciador;

    @BeforeEach
    void configurar() {
        gerenciador = new GerenciadorDeAcervo();
    }

    @Test
    void comecaVazio() {
        assertEquals(0, gerenciador.getItens().size());
        assertEquals(0, gerenciador.mediaDeAvaliacao());
    }

    @Test
    void adicionarItemNuloLancaExcecao() {
        assertThrows(ItemInvalidoException.class, () -> gerenciador.adicionar(null));
    }

    @Test
    void adicionarItemSemTituloLancaExcecao() {
        assertThrows(ItemInvalidoException.class,
                () -> gerenciador.adicionar(new Livro("", 2020, 4, "Autor", 200)));
    }

    @Test
    void contaPorCategoriaCorretamente() {
        gerenciador.adicionar(new Livro("Livro 1", 2020, 5, "Autor A", 300));
        gerenciador.adicionar(new Livro("Livro 2", 2021, 4, "Autor B", 250));
        gerenciador.adicionar(new Filme("Filme 1", 2019, 3, "Diretor A", 120));

        assertEquals(2L, gerenciador.contarPorCategoria().get("Livro"));
        assertEquals(1L, gerenciador.contarPorCategoria().get("Filme"));
    }

    @Test
    void mediaDeAvaliacaoCalculaCorretamente() {
        gerenciador.adicionar(new Livro("Livro 1", 2020, 4, "Autor A", 300));
        gerenciador.adicionar(new Filme("Filme 1", 2019, 2, "Diretor A", 120));

        assertEquals(3.0, gerenciador.mediaDeAvaliacao(), 0.001);
    }
}
