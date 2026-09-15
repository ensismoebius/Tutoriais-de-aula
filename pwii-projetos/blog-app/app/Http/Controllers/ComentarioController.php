<?php

namespace App\Http\Controllers;

use App\Models\Comentario;
use Illuminate\Http\Request;

class ComentarioController extends Controller
{
    public function index()
    {
        return view('comentarios', ['comentarios' => Comentario::latest()->get()]);
    }

    public function criar(Request $request)
    {
        $dados = $request->validate(['texto' => 'required|string|min:3|max:500']);
        Comentario::create($dados);

        return back()->with('sucesso', 'Comentário enviado!');
    }
}
