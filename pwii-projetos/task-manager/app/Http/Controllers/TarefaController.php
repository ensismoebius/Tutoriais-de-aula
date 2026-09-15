<?php

namespace App\Http\Controllers;

use App\DAO\TarefaDAOInterface;
use App\Models\Tarefa;
use Illuminate\Http\Request;

class TarefaController extends Controller
{
    public function __construct(private TarefaDAOInterface $dao) {}

    public function index()
    {
        $tarefas = Tarefa::orderByDesc('prazo')->paginate(10);
        $urgentes = $tarefas->getCollection()->filter->isUrgente();

        return view('tarefas.index', compact('tarefas', 'urgentes'));
    }

    public function create()
    {
        return view('tarefas.create');
    }

    public function store(Request $request)
    {
        $dados = $request->validate([
            'titulo' => 'required|string|max:150',
            'prazo' => 'required|date|after:today',
            'tipo' => 'required|in:urgente,rotina',
        ]);

        $this->dao->criar($dados);

        return redirect()->route('tarefas.index')->with('sucesso', 'Tarefa criada!');
    }

    public function show(Tarefa $tarefa)
    {
        return view('tarefas.show', compact('tarefa'));
    }

    public function edit(Tarefa $tarefa)
    {
        return view('tarefas.edit', compact('tarefa'));
    }

    public function update(Request $request, Tarefa $tarefa)
    {
        $dados = $request->validate([
            'titulo' => 'required|string|max:150',
            'prazo' => 'required|date',
            'tipo' => 'required|in:urgente,rotina',
        ]);

        $this->dao->atualizar($tarefa, $dados);

        return redirect()->route('tarefas.show', $tarefa);
    }

    public function destroy(Tarefa $tarefa)
    {
        $this->dao->remover($tarefa);

        return redirect()->route('tarefas.index')->with('sucesso', 'Tarefa removida.');
    }
}
