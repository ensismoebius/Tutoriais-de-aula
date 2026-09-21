<?php

use App\Http\Controllers\BuscaController;
use App\Http\Controllers\ComentarioController;
use App\Http\Controllers\NewsletterController;
use App\Http\Controllers\PostController;
use App\Http\Controllers\SessaoController;
use Illuminate\Support\Facades\Route;

Route::get('/', fn () => redirect('/posts'));

// Tópico 1 — Formulários/HTTP
Route::get('/newsletter', [NewsletterController::class, 'mostrarFormulario']);
Route::post('/newsletter', [NewsletterController::class, 'inscrever']);
Route::get('/artigos', [NewsletterController::class, 'buscarArtigos']);

// Tópico 3 — Segurança (SQL Injection, XSS, mass assignment, CSRF)
Route::get('/usuarios/busca', [BuscaController::class, 'buscar']);
Route::post('/usuarios', [BuscaController::class, 'cadastrar']);
Route::get('/comentarios', [ComentarioController::class, 'index']);
Route::post('/comentarios', [ComentarioController::class, 'criar']);

// Tópico 2 — Sessões / login
Route::get('/login', [SessaoController::class, 'mostrarLogin'])->name('login');
Route::post('/login', [SessaoController::class, 'entrar']);
Route::post('/login/google', [SessaoController::class, 'entrarComGoogle']);
Route::post('/logout', [SessaoController::class, 'sair'])->middleware('auth');

// Tópicos 4 e 5 — Padrão DAO / Projeto Integrador
Route::get('/posts', [PostController::class, 'index']);
Route::get('/posts/novo', [PostController::class, 'mostrarFormulario'])->middleware('auth');
Route::post('/posts', [PostController::class, 'criar'])->middleware('auth');
Route::delete('/posts/{id}', [PostController::class, 'excluir'])->middleware('auth');
