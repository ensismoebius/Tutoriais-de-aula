<!DOCTYPE html>
<html lang="pt-BR">
<head><meta charset="UTF-8"><title>Newsletter</title></head>
<body>
    <form method="POST" action="/newsletter">
        @csrf
        <input type="email" name="email" placeholder="seu@email.com" required>
        <button type="submit">Inscrever</button>
    </form>
</body>
</html>
