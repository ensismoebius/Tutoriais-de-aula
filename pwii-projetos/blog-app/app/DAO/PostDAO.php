<?php

namespace App\DAO;

use App\Models\Post;
use Illuminate\Database\Eloquent\Collection;

class PostDAO implements PostDAOInterface
{
    public function todos(): Collection
    {
        return Post::with('usuario')->latest()->get();
    }

    public function doUsuario(int $usuarioId): Collection
    {
        return Post::where('usuario_id', $usuarioId)->latest()->get();
    }

    public function criar(array $dados): Post
    {
        return Post::create($dados);
    }
}
