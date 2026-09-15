<!DOCTYPE html>
<html lang="pt-BR">
<head><meta charset="UTF-8"><title>Login</title></head>
<body>
    <h1>Login</h1>

    @if ($errors->any())
        <p style="color:#c00">{{ $errors->first() }}</p>
    @endif

    <form method="POST" action="/login">
        @csrf
        <input type="email" name="email" placeholder="E-mail" value="{{ old('email') }}">
        <input type="password" name="password" placeholder="Senha">
        <label><input type="checkbox" name="lembrar"> Lembrar-me</label>
        <button type="submit">Entrar</button>
    </form>
</body>
</html>
