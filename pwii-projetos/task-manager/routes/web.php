<?php

use App\Http\Controllers\TarefaController;
use Illuminate\Support\Facades\Route;

Route::get('/', fn () => redirect()->route('tarefas.index'));

Route::resource('tarefas', TarefaController::class);
