package jogo;

import jogo.model.Circulo;
import jogo.model.Entidade;
import jogo.model.EntidadeInvalidaException;
import jogo.model.GerenciadorDeJogo;
import jogo.model.Quadrado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GerenciadorDeJogoTest {

    private GerenciadorDeJogo gerenciador;

    @BeforeEach
    void configurar() {
        gerenciador = new GerenciadorDeJogo(new ArrayList<>());
    }

    @Test
    void comecaComPontuacaoZero() {
        assertEquals(0, gerenciador.calcularPontuacaoTotal());
    }

    @Test
    void calculaPontuacaoCorretamenteComEntidadesMistas() {
        List<Entidade> entidades = List.of(new Quadrado(0, 0, 10), new Circulo(0, 0, 10));
        GerenciadorDeJogo g = new GerenciadorDeJogo(entidades);

        assertEquals(15, g.calcularPontuacaoTotal());
    }

    @Test
    void pontuacaoDeListaVaziaEhZero() {
        assertEquals(0, gerenciador.calcularPontuacaoTotal());
    }

    @Test
    void adicionarQuadradoAumentaPontuacao() {
        gerenciador.adicionarEntidade(new Quadrado(0, 0, 10));
        assertEquals(10, gerenciador.calcularPontuacaoTotal());
    }

    @Test
    void adicionarEntidadeNulaLancaExcecao() {
        assertThrows(EntidadeInvalidaException.class, () -> gerenciador.adicionarEntidade(null));
    }

    @Test
    void marcarPontoDuasVezesSoma() {
        gerenciador.marcarPonto("Ana", 10);
        gerenciador.marcarPonto("Ana", 10);
        assertEquals(20, gerenciador.getPontuacaoPorJogador().get("Ana"));
    }

    @Test
    void contarPorTipoAgrupaCorretamente() {
        gerenciador.adicionarEntidade(new Quadrado(0, 0, 10));
        gerenciador.adicionarEntidade(new Quadrado(0, 0, 10));
        gerenciador.adicionarEntidade(new Circulo(0, 0, 10));

        assertEquals(2L, gerenciador.contarPorTipo().get("Quadrado"));
        assertEquals(1L, gerenciador.contarPorTipo().get("Circulo"));
    }
}
