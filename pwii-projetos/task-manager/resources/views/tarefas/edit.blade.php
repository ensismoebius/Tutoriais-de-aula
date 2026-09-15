@extends('layouts.app')

@section('titulo', 'Editar tarefa')

@section('conteudo')
    <h1>Editar tarefa</h1>

    <form method="POST" action="{{ route('tarefas.update', $tarefa) }}">
        @csrf
        @method('PUT')
        <input type="text" name="titulo" value="{{ old('titulo', $tarefa->titulo) }}">
        @error('titulo') <span class="erro">{{ $message }}</span> @enderror

        <input type="date" name="prazo" value="{{ old('prazo', $tarefa->prazo?->format('Y-m-d')) }}">
        @error('prazo') <span class="erro">{{ $message }}</span> @enderror

        <select name="tipo">
            <option value="rotina" @selected(old('tipo', $tarefa->tipo) === 'rotina')>Rotina</option>
            <option value="urgente" @selected(old('tipo', $tarefa->tipo) === 'urgente')>Urgente</option>
        </select>

        <button type="submit">Salvar</button>
    </form>
@endsection
