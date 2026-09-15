@extends('layouts.app')

@section('titulo', 'Nova tarefa')

@section('conteudo')
    <h1>Nova tarefa</h1>

    <form method="POST" action="{{ route('tarefas.store') }}">
        @csrf
        <input type="text" name="titulo" value="{{ old('titulo') }}">
        @error('titulo') <span class="erro">{{ $message }}</span> @enderror

        <input type="date" name="prazo" value="{{ old('prazo') }}">
        @error('prazo') <span class="erro">{{ $message }}</span> @enderror

        <select name="tipo">
            <option value="rotina" @selected(old('tipo') === 'rotina')>Rotina</option>
            <option value="urgente" @selected(old('tipo') === 'urgente')>Urgente</option>
        </select>
        @error('tipo') <span class="erro">{{ $message }}</span> @enderror

        <button type="submit">Criar</button>
    </form>
@endsection
