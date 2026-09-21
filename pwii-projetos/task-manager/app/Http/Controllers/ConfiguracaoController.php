<?php

namespace App\Http\Controllers;

use App\Models\Configuracao;
use Illuminate\Http\Request;

class ConfiguracaoController extends Controller
{
    public function show()
    {
        return view('configuracoes.show', ['configuracao' => Configuracao::atual()]);
    }

    public function edit()
    {
        return view('configuracoes.edit', ['configuracao' => Configuracao::atual()]);
    }

    public function update(Request $request)
    {
        $dados = $request->validate([
            'tarefas_por_pagina' => 'required|integer|min:1|max:100',
        ]);

        Configuracao::atual()->update($dados);

        return redirect()->route('configuracoes.show')->with('sucesso', 'Configurações salvas!');
    }
}
