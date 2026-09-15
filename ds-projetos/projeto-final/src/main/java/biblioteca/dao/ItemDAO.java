package biblioteca.dao;

import biblioteca.model.ItemDeAcervo;

import java.util.List;

public interface ItemDAO {
    void salvar(ItemDeAcervo item);

    List<ItemDeAcervo> listarTodos();

    void remover(String id);
}
