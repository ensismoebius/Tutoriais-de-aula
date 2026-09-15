<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title>@yield('titulo', 'Gerenciador de Tarefas')</title>
    @livewireStyles
</head>
<body>
    <p>Urgentes no sistema: <strong>{{ $totalUrgentes }}</strong></p>

    @if(session('sucesso'))
        <div class="alerta-sucesso">{{ session('sucesso') }}</div>
    @endif

    @yield('conteudo')

    @livewireScripts
</body>
</html>
