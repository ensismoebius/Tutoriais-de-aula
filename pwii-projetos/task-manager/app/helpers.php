<?php

if (! function_exists('formatar_prazo')) {
    function formatar_prazo($data): string
    {
        if (! $data) {
            return 'sem prazo';
        }

        return $data->isToday() ? 'Hoje' : $data->format('d/m/Y');
    }
}

if (! function_exists('tempo_restante')) {
    function tempo_restante($prazo): string
    {
        if (! $prazo) {
            return 'sem prazo';
        }

        $dias = now()->diffInDays($prazo, false);

        if ($dias < 0) {
            return 'atrasada há '.abs($dias).' dia(s)';
        }

        return "faltam {$dias} dia(s)";
    }
}
