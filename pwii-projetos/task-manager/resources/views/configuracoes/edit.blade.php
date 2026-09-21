@extends('layouts.app')

@section('titulo', 'Editar configurações')

@section('conteudo')
    <h1>Editar configurações</h1>

    <form method="POST" action="{{ route('configuracoes.update') }}">
        @csrf
        @method('PUT')

        <label>
            Tarefas por página:
            <input type="number" name="tarefas_por_pagina" min="1" max="100"
                value="{{ old('tarefas_por_pagina', $configuracao->tarefas_por_pagina) }}">
        </label>
        @error('tarefas_por_pagina') <span class="erro">{{ $message }}</span> @enderror

        <button type="submit">Salvar</button>
    </form>

    <a href="{{ route('configuracoes.show') }}">Voltar</a>
@endsection
