package jogo.model;

import java.util.HashMap;
import java.util.Map;

/** Singleton: uma única instância global, acessível de qualquer parte do jogo via getInstance(). */
public class GerenciadorDeConfiguracoes {
    private static GerenciadorDeConfiguracoes instancia;
    private final Map<String, String> valores = new HashMap<>();

    private GerenciadorDeConfiguracoes() {
        valores.put("dificuldade", "normal");
        valores.put("volume", "80");
    }

    public static synchronized GerenciadorDeConfiguracoes getInstance() {
        if (instancia == null) {
            instancia = new GerenciadorDeConfiguracoes();
        }
        return instancia;
    }

    public String get(String chave) {
        return valores.get(chave);
    }

    public void set(String chave, String valor) {
        valores.put(chave, valor);
    }
}
