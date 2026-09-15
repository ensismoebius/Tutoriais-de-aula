package jogo;

import jogo.model.Circulo;
import jogo.model.Entidade;
import jogo.model.Quadrado;
import jogo.model.Repositorio;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RepositorioTest {

    @Test
    void salvarEBuscarPorId() {
        Repositorio<Entidade> repo = new Repositorio<>();
        Entidade q = new Quadrado(100, 50, 30);
        repo.salvar(q);

        assertTrue(repo.buscarPorId(q.getId()).isPresent());
        assertEquals(1, repo.total());
    }

    @Test
    void removerTiraDoRepositorio() {
        Repositorio<Entidade> repo = new Repositorio<>();
        Entidade c = new Circulo(0, 0, 10);
        repo.salvar(c);
        repo.remover(c.getId());

        assertFalse(repo.buscarPorId(c.getId()).isPresent());
        assertEquals(0, repo.total());
    }
}
