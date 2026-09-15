<?php

namespace App\Http\Controllers;

use App\Models\Usuario;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Hash;

class BuscaController extends Controller
{
    public function buscar(Request $request)
    {
        $termo = $request->query('termo', '');

        $usuarios = Usuario::query()
            ->where('nome', 'like', "%{$termo}%")
            ->get();

        return view('busca', compact('usuarios', 'termo'));
    }

    public function cadastrar(Request $request)
    {
        $dados = $request->validate([
            'nome' => 'required|string|max:100',
            'email' => 'required|email|max:100|unique:usuarios,email',
            'password' => 'required|string|min:8',
        ]);

        $dados['password'] = Hash::make($dados['password']);

        Usuario::create($dados);

        return redirect('/usuarios/busca')->with('sucesso', 'Cadastrado!');
    }
}
