package jogo.dao;

import jogo.model.Circulo;
import jogo.model.Entidade;
import jogo.model.Quadrado;

/** Padrão Factory: único lugar que sabe mapear "tipo" (string do banco) para classe concreta. */
public final class EntidadeFactory {

    private EntidadeFactory() {
    }

    public static Entidade criar(String tipo, double x, double y, double tamanho) {
        return switch (tipo) {
            case "Quadrado" -> new Quadrado(x, y, tamanho);
            case "Circulo" -> new Circulo(x, y, tamanho);
            default -> throw new IllegalArgumentException("Tipo desconhecido: " + tipo);
        };
    }
}
