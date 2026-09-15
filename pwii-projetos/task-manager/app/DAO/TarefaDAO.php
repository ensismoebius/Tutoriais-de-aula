<?php

namespace App\DAO;

use App\Models\Tarefa;
use Illuminate\Database\Eloquent\Collection;

class TarefaDAO implements TarefaDAOInterface
{
    public function todas(): Collection
    {
        return Tarefa::orderByDesc('prazo')->get();
    }

    public function porId(int $id): ?Tarefa
    {
        return Tarefa::find($id);
    }

    public function criar(array $dados): Tarefa
    {
        return Tarefa::create($dados);
    }

    public function atualizar(Tarefa $tarefa, array $dados): Tarefa
    {
        $tarefa->update($dados);

        return $tarefa;
    }

    public function remover(Tarefa $tarefa): void
    {
        $tarefa->delete();
    }
}
