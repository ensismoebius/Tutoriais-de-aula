<?php

namespace App\Services;

use Google\Client;

class GoogleClientIdTokenVerifier implements GoogleIdTokenVerifier
{
    public function __construct(private readonly string $googleClientId) {}

    public function verificar(string $credential): ?array
    {
        $client = new Client(['client_id' => $this->googleClientId]);

        try {
            $payload = $client->verifyIdToken($credential);
        } catch (\UnexpectedValueException) {
            // Um credential que nem chega a ter o formato de um JWT (três
            // segmentos separados por ".") faz o firebase/php-jwt lançar essa
            // exceção antes mesmo de checar assinatura — o verifyIdToken() do
            // google/apiclient só captura ExpiredException, SignatureInvalidException
            // e DomainException internamente, então esta aqui escapa e precisa
            // ser tratada por fora.
            return null;
        }

        return $payload === false ? null : $payload;
    }
}
