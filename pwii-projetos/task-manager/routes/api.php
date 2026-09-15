<?php

use App\Http\Controllers\Api\TarefaController;
use Illuminate\Support\Facades\Route;

Route::apiResource('tarefas', TarefaController::class);
