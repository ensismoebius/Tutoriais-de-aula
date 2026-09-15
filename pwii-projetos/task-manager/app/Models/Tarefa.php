<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Tarefa extends Model
{
    protected $table = 'tarefas';

    protected $fillable = ['titulo', 'prazo', 'concluida', 'tipo'];

    protected $casts = [
        'prazo' => 'date',
        'concluida' => 'boolean',
    ];

    /**
     * Regra de negócio pertence ao Model, não ao Controller nem à View
     * (ver tópico "Padrão MVC").
     */
    public function isUrgente(): bool
    {
        if ($this->concluida || ! $this->prazo) {
            return false;
        }

        return now()->diffInDays($this->prazo, false) <= 2;
    }

    public function isAtrasada(): bool
    {
        return ! $this->concluida && $this->prazo && $this->prazo->isPast();
    }

    /**
     * Alternativa via match() ao polimorfismo de herança real, aplicada
     * dentro de um único Model Eloquent (ver tópico "POO Aplicada").
     */
    public function calcularPrioridade(): int
    {
        return match ($this->tipo) {
            'urgente' => $this->isUrgente() ? 10 : 5,
            'rotina' => 1,
            default => 0,
        };
    }
}
