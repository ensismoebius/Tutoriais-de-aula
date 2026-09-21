<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Configuracao extends Model
{
    protected $table = 'configuracoes';

    protected $fillable = ['tarefas_por_pagina'];

    /**
     * Garante que sempre existe exatamente uma linha de configuração —
     * a migration já insere a primeira, mas um Controller Singleton não
     * recebe um id vindo da URL, então ele precisa de um jeito de sempre
     * achar "a" configuração sem depender de nenhum parâmetro.
     */
    public static function atual(): self
    {
        return static::firstOrCreate([], ['tarefas_por_pagina' => 10]);
    }
}
