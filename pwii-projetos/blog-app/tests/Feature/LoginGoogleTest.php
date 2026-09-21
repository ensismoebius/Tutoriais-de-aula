<?php

namespace Tests\Feature;

use App\Models\Usuario;
use App\Services\GoogleIdTokenVerifier;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Illuminate\Support\Facades\Auth;
use Tests\TestCase;

class LoginGoogleTest extends TestCase
{
    use RefreshDatabase;

    public function test_credential_valido_cria_usuario_e_autentica_via_sessao(): void
    {
        // Substitui só a fronteira de rede (verifyIdToken do Google\Client) por
        // um fake, sem tocar no restante do fluxo do controller.
        $this->app->bind(GoogleIdTokenVerifier::class, fn () => new class implements GoogleIdTokenVerifier
        {
            public function verificar(string $credential): ?array
            {
                return [
                    'sub' => 'google-id-123',
                    'email' => 'nova@exemplo.com',
                    'name' => 'Nova Pessoa',
                ];
            }
        });

        $response = $this->post('/login/google', ['credential' => 'qualquer-coisa']);

        $response->assertRedirect('/posts');

        $usuario = Usuario::where('google_id', 'google-id-123')->first();

        $this->assertNotNull($usuario, 'esperava uma linha nova em usuarios com google_id preenchido');
        $this->assertSame('nova@exemplo.com', $usuario->email);
        $this->assertNull($usuario->password, 'usuário criado via Google não deve ter senha');
        $this->assertTrue(Auth::check(), 'esperava uma sessão autenticada de verdade após o login');
        $this->assertTrue(Auth::id() === $usuario->id);
    }

    public function test_credential_invalido_nao_cria_sessao_e_redireciona_com_erro(): void
    {
        $this->app->bind(GoogleIdTokenVerifier::class, fn () => new class implements GoogleIdTokenVerifier
        {
            public function verificar(string $credential): ?array
            {
                return null;
            }
        });

        $response = $this->from('/login')->post('/login/google', ['credential' => 'token-invalido']);

        $response->assertRedirect('/login');
        $response->assertSessionHasErrors('email');
        $this->assertFalse(Auth::check());
    }
}
