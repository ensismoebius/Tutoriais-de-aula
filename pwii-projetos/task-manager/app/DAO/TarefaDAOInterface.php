<?php

namespace App\DAO;

use App\Models\Tarefa;
use Illuminate\Database\Eloquent\Collection;

interface TarefaDAOInterface
{
    public function todas(): Collection;

    public function porId(int $id): ?Tarefa;

    public function criar(array $dados): Tarefa;

    public function atualizar(Tarefa $tarefa, array $dados): Tarefa;

    public function remover(Tarefa $tarefa): void;
}
