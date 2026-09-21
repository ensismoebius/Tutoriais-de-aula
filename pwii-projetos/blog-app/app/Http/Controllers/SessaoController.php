<?php

namespace App\Http\Controllers;

use App\Models\Usuario;
use App\Services\GoogleIdTokenVerifier;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;

class SessaoController extends Controller
{
    public function __construct(private readonly GoogleIdTokenVerifier $googleIdTokenVerifier) {}

    public function mostrarLogin()
    {
        return view('sessao.login');
    }

    public function entrar(Request $request)
    {
        $credenciais = $request->validate([
            'email' => 'required|email',
            'password' => 'required|string',
        ]);

        $lembrar = $request->boolean('lembrar');

        if (! Auth::attempt($credenciais, $lembrar)) {
            return back()->withErrors(['email' => 'Credenciais inválidas.']);
        }

        $request->session()->regenerate();

        return redirect('/posts');
    }

    public function entrarComGoogle(Request $request)
    {
        $dados = $request->validate([
            'credential' => 'required|string',
        ]);

        $payload = $this->googleIdTokenVerifier->verificar($dados['credential']);

        if ($payload === null) {
            return back()->withErrors(['email' => 'Não foi possível confirmar sua identidade com o Google.']);
        }

        $usuario = Usuario::firstOrCreate(
            ['google_id' => $payload['sub']],
            ['nome' => $payload['name'], 'email' => $payload['email'], 'password' => null]
        );

        $lembrar = $request->boolean('lembrar');

        Auth::login($usuario, $lembrar);
        $request->session()->regenerate();

        return redirect('/posts');
    }

    public function sair(Request $request)
    {
        Auth::logout();
        $request->session()->invalidate();
        $request->session()->regenerateToken();

        return redirect('/login');
    }
}
