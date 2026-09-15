<!DOCTYPE html>
<html lang="pt-BR">
<head><meta charset="UTF-8"><title>Comentários</title></head>
<body>
    @if(session('sucesso'))
        <p style="color:green">{{ session('sucesso') }}</p>
    @endif

    <form method="POST" action="/comentarios">
        @csrf
        <textarea name="texto" rows="3"></textarea>
        <button type="submit">Enviar</button>
    </form>

    @foreach($comentarios as $comentario)
        <div class="comentario">{{ $comentario->texto }}</div>
    @endforeach
</body>
</html>
