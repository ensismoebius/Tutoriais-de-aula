package jogo.dao;

import jogo.model.Fase;

import java.util.List;

public interface FaseDAO {
    void salvar(Fase fase);

    List<Fase> listarTodas();

    void atualizar(Fase fase);

    void excluir(int id);
}
