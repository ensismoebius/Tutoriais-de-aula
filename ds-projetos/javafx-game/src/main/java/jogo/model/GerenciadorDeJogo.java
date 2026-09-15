package jogo.model;

import jogo.dao.EntidadeDAO;
import jogo.dao.EntidadeDAOSQLite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Model central do jogo: nenhum import de javafx.* aqui de propósito — esta
 * classe existe e é testável mesmo sem nenhuma janela aberta (padrão MVC).
 */
public class GerenciadorDeJogo {

    private static final Logger logger = LoggerFactory.getLogger(GerenciadorDeJogo.class);
    private static final int LIMITE_ENTIDADES = 50;

    private final EntidadeDAO dao;
    private List<Entidade> entidades;
    private final Set<String> conquistasDesbloqueadas = new HashSet<>();
    private final Map<String, Integer> pontuacaoPorJogador = new HashMap<>();

    public GerenciadorDeJogo(List<Entidade> entidadesIniciais) {
        this.entidades = new ArrayList<>(entidadesIniciais);
        this.dao = new EntidadeDAOSQLite();
    }

    public GerenciadorDeJogo() {
        this(new ArrayList<>());
    }

    public void adicionarEntidade(Entidade e) {
        if (e == null) {
            throw new EntidadeInvalidaException("Entidade não pode ser nula");
        }
        if (entidades.size() >= LIMITE_ENTIDADES) {
            throw new LimiteDeFaseExcedidoException(LIMITE_ENTIDADES);
        }
        logger.info("Entidade adicionada: {}", e.getClass().getSimpleName());
        entidades.add(e);
        if (entidades.size() == 10) {
            conquistasDesbloqueadas.add("DEZ_ENTIDADES");
        }
    }

    public void marcarPonto(String jogador, int pontos) {
        pontuacaoPorJogador.merge(jogador, pontos, Integer::sum);
    }

    public int calcularPontuacaoTotal() {
        int total = 0;
        for (Entidade e : entidades) {
            if (e instanceof Quadrado) total += 10;
            if (e instanceof Circulo) total += 5;
        }
        return total;
    }

    public void reiniciar() {
        entidades.clear();
        conquistasDesbloqueadas.clear();
        pontuacaoPorJogador.clear();
    }

    public void carregarJogoSalvo() {
        entidades = new ArrayList<>(dao.listarTodas());
        logger.info("Jogo carregado: {} entidades", entidades.size());
    }

    public void salvarProgresso() {
        for (Entidade e : entidades) {
            dao.salvar(e);
        }
        logger.info("Progresso salvo: {} entidades", entidades.size());
    }

    public List<Entidade> getEntidades() {
        return entidades;
    }

    public Set<String> getConquistasDesbloqueadas() {
        return conquistasDesbloqueadas;
    }

    public Map<String, Integer> getPontuacaoPorJogador() {
        return pontuacaoPorJogador;
    }

    public void exportarRelatorio(String caminho) {
        try (PrintWriter escritor = new PrintWriter(new FileWriter(caminho))) {
            escritor.println("Relatório de Fase");
            escritor.println("Total de entidades: " + entidades.size());
            for (Entidade e : entidades) {
                escritor.printf("%s em (%.1f, %.1f)%n", e.getClass().getSimpleName(), e.getX(), e.getY());
            }
        } catch (IOException ex) {
            throw new RuntimeException("Falha ao exportar relatório", ex);
        }
    }

    public void exportarComNio(String caminho) throws IOException {
        List<String> linhas = entidades.stream()
                .map(e -> e.getClass().getSimpleName() + " em (" + e.getX() + ", " + e.getY() + ")")
                .toList();
        Files.write(Path.of(caminho), linhas);
    }

    public long contarQuadrados(String caminho) throws IOException {
        try (Stream<String> linhas = Files.lines(Path.of(caminho))) {
            return linhas.filter(l -> l.startsWith("Quadrado")).count();
        }
    }

    public Map<String, Long> contarPorTipo() {
        return entidades.stream()
                .collect(Collectors.groupingBy(e -> e.getClass().getSimpleName(), Collectors.counting()));
    }
}
