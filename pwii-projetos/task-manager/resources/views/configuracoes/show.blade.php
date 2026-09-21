@extends('layouts.app')

@section('titulo', 'Configurações')

@section('conteudo')
    <h1>Configurações</h1>

    @if(session('sucesso'))
        <p class="sucesso">{{ session('sucesso') }}</p>
    @endif

    <p>Tarefas por página: {{ $configuracao->tarefas_por_pagina }}</p>

    <a href="{{ route('configuracoes.edit') }}">Editar</a>
@endsection
