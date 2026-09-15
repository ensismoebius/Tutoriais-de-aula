<?php

namespace App\Http\Controllers\Api;

use App\DAO\TarefaDAOInterface;
use App\Http\Controllers\Controller;
use App\Http\Resources\TarefaResource;
use App\Models\Tarefa;
use Illuminate\Http\Request;

class TarefaController extends Controller
{
    public function __construct(private TarefaDAOInterface $dao) {}

    public function index()
    {
        return TarefaResource::collection($this->dao->todas());
    }

    public function show(Tarefa $tarefa)
    {
        return new TarefaResource($tarefa);
    }

    public function store(Request $request)
    {
        $dados = $request->validate([
            'titulo' => 'required|string|max:150',
            'prazo' => 'required|date|after:today',
            'tipo' => 'required|in:urgente,rotina',
        ]);

        $tarefa = $this->dao->criar($dados);

        return new TarefaResource($tarefa);
    }

    public function update(Request $request, Tarefa $tarefa)
    {
        $dados = $request->validate([
            'titulo' => 'sometimes|string|max:150',
            'prazo' => 'sometimes|date',
            'tipo' => 'sometimes|in:urgente,rotina',
        ]);

        $this->dao->atualizar($tarefa, $dados);

        return new TarefaResource($tarefa);
    }

    public function destroy(Tarefa $tarefa)
    {
        $this->dao->remover($tarefa);

        return response()->noContent();
    }
}
