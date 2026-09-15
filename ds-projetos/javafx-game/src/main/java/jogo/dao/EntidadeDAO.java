package jogo.dao;

import jogo.model.Entidade;

import java.util.List;

public interface EntidadeDAO {
    void salvar(Entidade e);

    List<Entidade> listarTodas();

    void remover(String id);
}
