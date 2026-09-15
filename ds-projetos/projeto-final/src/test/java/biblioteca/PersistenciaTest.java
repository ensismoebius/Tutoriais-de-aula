package biblioteca;

import biblioteca.dao.Conexao;
import biblioteca.dao.ItemDAO;
import biblioteca.dao.ItemDAOSQLite;
import biblioteca.model.Filme;
import biblioteca.model.ItemDeAcervo;
import biblioteca.model.Livro;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PersistenciaTest {

    @Test
    void salvarEListarPreservaCategoriaECampos() throws Exception {
        Conexao.inicializar();
        ItemDAO dao = new ItemDAOSQLite();

        Livro livro = new Livro("Dom Casmurro", 1899, 5, "Machado de Assis", 256);
        Filme filme = new Filme("Cidade de Deus", 2002, 5, "Fernando Meirelles", 130);
        dao.salvar(livro);
        dao.salvar(filme);

        List<ItemDeAcervo> todos = dao.listarTodos();
        assertTrue(todos.stream().anyMatch(i -> i instanceof Livro l && l.getAutor().equals("Machado de Assis")));
        assertTrue(todos.stream().anyMatch(i -> i instanceof Filme f && f.getDiretor().equals("Fernando Meirelles")));

        dao.remover(livro.getId());
        assertTrue(dao.listarTodos().stream().noneMatch(i -> i.getId().equals(livro.getId())));
    }
}
