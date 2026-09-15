<?php

namespace App\DAO;

use App\Models\Post;
use Illuminate\Database\Eloquent\Collection;

interface PostDAOInterface
{
    public function todos(): Collection;

    public function doUsuario(int $usuarioId): Collection;

    public function criar(array $dados): Post;
}
