<?php

namespace App\Livewire;

use App\DAO\TarefaDAOInterface;
use Livewire\Component;

class TarefaLista extends Component
{
    public function concluir(int $tarefaId): void
    {
        $tarefa = app(TarefaDAOInterface::class)->porId($tarefaId);
        $tarefa->update(['concluida' => true]);
    }

    public function remover(int $tarefaId): void
    {
        $tarefa = app(TarefaDAOInterface::class)->porId($tarefaId);
        app(TarefaDAOInterface::class)->remover($tarefa);
    }

    public function render()
    {
        $dao = app(TarefaDAOInterface::class);

        return view('livewire.tarefa-lista', [
            'tarefas' => $dao->todas(),
        ]);
    }
}
