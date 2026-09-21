<?php

use App\Http\Controllers\ConfiguracaoController;
use App\Http\Controllers\MarcarTarefaConcluidaController;
use App\Http\Controllers\TarefaController;
use Illuminate\Support\Facades\Route;

Route::get('/', fn () => redirect()->route('tarefas.index'));

Route::resource('tarefas', TarefaController::class);
Route::patch('tarefas/{tarefa}/concluir', MarcarTarefaConcluidaController::class)->name('tarefas.concluir');

Route::singleton('configuracoes', ConfiguracaoController::class);
