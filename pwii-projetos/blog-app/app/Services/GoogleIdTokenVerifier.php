<?php

namespace App\Services;

interface GoogleIdTokenVerifier
{
    /**
     * Verifica um ID token do Google e devolve o payload decodificado,
     * ou null se o token for inválido (assinatura, `aud` ou `exp` incorretos).
     *
     * @return array<string, mixed>|null
     */
    public function verificar(string $credential): ?array;
}
