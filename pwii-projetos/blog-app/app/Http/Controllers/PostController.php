<?php

namespace App\Http\Controllers;

use App\DAO\PostDAOInterface;
use App\Models\Post;
use Illuminate\Http\Request;

class PostController extends Controller
{
    public function __construct(private PostDAOInterface $dao) {}

    public function index()
    {
        return view('posts.index', ['posts' => $this->dao->todos()]);
    }

    public function mostrarFormulario()
    {
        return view('posts.novo');
    }

    public function criar(Request $request)
    {
        $dados = $request->validate([
            'titulo' => 'required|string|max:150',
            'conteudo' => 'required|string|min:10',
        ]);

        $dados['usuario_id'] = auth()->id();

        $this->dao->criar($dados);

        return redirect('/posts');
    }

    public function excluir(Request $request, int $id)
    {
        $post = Post::findOrFail($id);

        if ($post->usuario_id !== auth()->id()) {
            abort(403, 'Você não pode excluir este post');
        }

        $post->delete();

        return redirect('/posts');
    }
}
