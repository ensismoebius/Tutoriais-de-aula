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

    <p>ou</p>

    <form id="form-google" method="POST" action="/login/google">
        @csrf
        <input type="hidden" name="credential" id="google-credential">
        <input type="hidden" name="lembrar" id="google-lembrar" value="0">
    </form>

    <div id="google-button"
         data-client-id="{{ config('services.google.client_id') }}"></div>

    <script src="https://accounts.google.com/gsi/client" async defer></script>
    <script>
        function aoReceberCredencialGoogle(resposta) {
            document.getElementById('google-credential').value = resposta.credential;
            document.getElementById('google-lembrar').value =
                document.querySelector('input[name="lembrar"]').checked ? '1' : '0';
            document.getElementById('form-google').submit();
        }

        window.onload = function () {
            const clientId = document.getElementById('google-button').dataset.clientId;

            google.accounts.id.initialize({
                client_id: clientId,
                callback: aoReceberCredencialGoogle,
            });

            google.accounts.id.renderButton(
                document.getElementById('google-button'),
                { theme: 'outline', size: 'large' }
            );
        };
    </script>
</body>
</html>
