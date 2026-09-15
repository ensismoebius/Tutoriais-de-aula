<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;

class NewsletterController extends Controller
{
    public function mostrarFormulario()
    {
        return view('newsletter');
    }

    public function inscrever(Request $request)
    {
        $dados = $request->validate([
            'email' => 'required|email|max:255',
        ]);

        return "Inscrito validado: {$dados['email']}";
    }

    public function buscarArtigos(Request $request)
    {
        $tag = $request->query('tag');

        if (! $tag) {
            return 'Informe uma tag: /artigos?tag=laravel';
        }

        return "Artigos marcados com: {$tag}";
    }

    public function inspecionar(Request $request)
    {
        return [
            'metodo' => $request->method(),
            'url_completa' => $request->fullUrl(),
            'so_email' => $request->only('email'),
            'tudo_menos_csrf' => $request->except('_token'),
            'e_ajax' => $request->ajax(),
            'e_json' => $request->expectsJson(),
        ];
    }
}
