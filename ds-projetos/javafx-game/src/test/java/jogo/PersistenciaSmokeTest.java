package jogo;

import jogo.dao.Conexao;
import jogo.dao.EntidadeDAO;
import jogo.dao.EntidadeDAOSQLite;
import jogo.dao.FaseDAO;
import jogo.dao.FaseDAOImpl;
import jogo.model.Circulo;
import jogo.model.Entidade;
import jogo.model.Fase;
import jogo.model.Quadrado;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Verifica persistência real em SQLite (não mockada) — arquivos .db de verdade. */
class PersistenciaSmokeTest {

    @Test
    void entidadeDaoSalvaEListaDeVoltaComTipoCorreto() throws Exception {
        Conexao.inicializar();
        EntidadeDAO dao = new EntidadeDAOSQLite();
        Entidade q = new Quadrado(10, 20, 30);
        Entidade c = new Circulo(40, 50, 15);
        dao.salvar(q);
        dao.salvar(c);

        List<Entidade> todas = dao.listarTodas();
        assertTrue(todas.stream().anyMatch(e -> e.getId().equals(q.getId()) && e instanceof Quadrado));
        assertTrue(todas.stream().anyMatch(e -> e.getId().equals(c.getId()) && e instanceof Circulo));

        dao.remover(q.getId());
        assertTrue(dao.listarTodas().stream().noneMatch(e -> e.getId().equals(q.getId())));
    }

    @Test
    void faseDaoCrudCompleto() {
        FaseDAO dao = new FaseDAOImpl();
        Fase f = new Fase("Fase de teste", 3, 0.8);
        dao.salvar(f);

        Fase salva = dao.listarTodas().stream()
                .filter(x -> x.getNome().equals("Fase de teste"))
                .findFirst().orElseThrow();
        assertEquals(3, salva.getDificuldade());

        salva.setDificuldade(5);
        dao.atualizar(salva);
        Fase atualizada = dao.listarTodas().stream()
                .filter(x -> x.getId() == salva.getId()).findFirst().orElseThrow();
        assertEquals(5, atualizada.getDificuldade());

        dao.excluir(salva.getId());
        assertTrue(dao.listarTodas().stream().noneMatch(x -> x.getId() == salva.getId()));
    }
}
