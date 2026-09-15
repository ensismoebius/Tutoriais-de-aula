<!DOCTYPE html>
<html lang="pt-BR">
<head><meta charset="UTF-8"><title>Posts</title></head>
<body>
    <h1>Posts</h1>

    @auth
        <p>Logado como {{ auth()->user()->nome }}.
            <a href="/posts/novo">Novo post</a>
            <form method="POST" action="/logout" style="display:inline">
                @csrf
                <button type="submit">Sair</button>
            </form>
        </p>
    @else
        <p><a href="/login">Entrar</a> para publicar.</p>
    @endauth

    @foreach($posts as $post)
        <article>
            <h2>{{ $post->titulo }}</h2>
            <p>Por: {{ $post->usuario->nome ?? 'sem autor' }}</p>
            <p>{{ $post->conteudo }}</p>

            @auth
                <form method="POST" action="/posts/{{ $post->id }}">
                    @csrf
                    @method('DELETE')
                    <button type="submit">Excluir</button>
                </form>
            @endauth
        </article>
    @endforeach
</body>
</html>
