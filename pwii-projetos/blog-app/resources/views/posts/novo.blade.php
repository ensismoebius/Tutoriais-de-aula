<!DOCTYPE html>
<html lang="pt-BR">
<head><meta charset="UTF-8"><title>Novo post</title></head>
<body>
    <h1>Novo post</h1>

    @if ($errors->any())
        <ul style="color:#c00">
            @foreach ($errors->all() as $erro)
                <li>{{ $erro }}</li>
            @endforeach
        </ul>
    @endif

    <form method="POST" action="/posts">
        @csrf
        <input type="text" name="titulo" placeholder="Título" value="{{ old('titulo') }}">
        <textarea name="conteudo" placeholder="Conteúdo">{{ old('conteudo') }}</textarea>
        <button type="submit">Publicar</button>
    </form>
</body>
</html>
