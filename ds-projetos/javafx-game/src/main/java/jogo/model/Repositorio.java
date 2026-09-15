package jogo.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/** Repositório genérico em memória para qualquer T que tenha um id. */
public class Repositorio<T extends ComId> {

    private final Map<String, T> itens = new HashMap<>();

    public void salvar(T item) {
        itens.put(item.getId(), item);
    }

    public Optional<T> buscarPorId(String id) {
        return Optional.ofNullable(itens.get(id));
    }

    public void remover(String id) {
        itens.remove(id);
    }

    public int total() {
        return itens.size();
    }

    public java.util.List<T> listarTodos() {
        return new java.util.ArrayList<>(itens.values());
    }
}
