<?php

namespace App\Http\Controllers;

use App\Models\Tarefa;

class MarcarTarefaConcluidaController extends Controller
{
    public function __invoke(Tarefa $tarefa)
    {
        $tarefa->update(['concluida' => ! $tarefa->concluida]);

        return redirect()->route('tarefas.show', $tarefa);
    }
}
