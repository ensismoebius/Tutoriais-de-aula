@extends('layouts.app')

@section('titulo', $tarefa->titulo)

@section('conteudo')
    <h1>{{ $tarefa->titulo }}</h1>
    <p>Prazo: {{ formatar_prazo($tarefa->prazo) }}</p>
    <p>Concluída: {{ $tarefa->concluida ? 'sim' : 'não' }}</p>
    <p>Prioridade calculada: {{ $tarefa->calcularPrioridade() }}</p>

    @urgente($tarefa)
        <p class="badge">URGENTE</p>
    @endurgente

    <a href="{{ route('tarefas.edit', $tarefa) }}">Editar</a>

    <form method="POST" action="{{ route('tarefas.destroy', $tarefa) }}">
        @csrf
        @method('DELETE')
        <button type="submit">Excluir</button>
    </form>

    <a href="{{ route('tarefas.index') }}">Voltar</a>
@endsection
