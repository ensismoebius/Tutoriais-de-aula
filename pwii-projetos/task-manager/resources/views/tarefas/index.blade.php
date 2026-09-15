@extends('layouts.app')

@section('titulo', 'Minhas Tarefas')

@section('conteudo')
    <h1>Tarefas</h1>
    <p>Urgentes nesta página: {{ $urgentes->count() }}</p>

    <a href="{{ route('tarefas.create') }}">Nova tarefa (formulário tradicional)</a>

    @foreach($tarefas as $tarefa)
        <x-tarefa-card :tarefa="$tarefa" />
        <a href="{{ route('tarefas.show', $tarefa) }}">Ver</a>
    @endforeach

    {{ $tarefas->links() }}

    <hr>

    <h2>Interatividade sem reload (Livewire)</h2>
    @livewire('tarefa-form')
    @livewire('tarefa-lista')
@endsection
