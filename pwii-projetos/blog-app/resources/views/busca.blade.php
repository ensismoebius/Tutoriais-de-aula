<!DOCTYPE html>
<html lang="pt-BR">
<head><meta charset="UTF-8"><title>Busca de usuários</title></head>
<body>
    <h1>Busca de usuários</h1>

    <form method="GET" action="/usuarios/busca">
        <input type="text" name="termo" value="{{ $termo }}" placeholder="nome">
        <button type="submit">Buscar</button>
    </form>

    <p>Termo buscado: <strong>{{ $termo }}</strong></p>
    <p>Resultados: <strong>{{ $usuarios->count() }}</strong></p>

    <ul>
        @foreach($usuarios as $usuario)
            <li>{{ $usuario->nome }} — {{ $usuario->email }}</li>
        @endforeach
    </ul>

    @if(session('sucesso'))
        <p style="color:green">{{ session('sucesso') }}</p>
    @endif

    <h2>Cadastrar usuário</h2>
    @if ($errors->any())
        <ul style="color:#c00">
            @foreach ($errors->all() as $erro)
                <li>{{ $erro }}</li>
            @endforeach
        </ul>
    @endif
    <form method="POST" action="/usuarios">
        @csrf
        <input type="text" name="nome" placeholder="nome" value="{{ old('nome') }}">
        <input type="email" name="email" placeholder="email" value="{{ old('email') }}">
        <input type="password" name="password" placeholder="senha">
        <button type="submit">Cadastrar</button>
    </form>
</body>
</html>
