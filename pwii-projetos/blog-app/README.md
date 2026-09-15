# blog-app

Implementação de referência dos Tópicos 1 a 5 de `Tutorial_PWII.md` (Formulários/HTTP, Sessões, Segurança, Padrão DAO, Projeto Integrador). Laravel 13.26, PHP 8.4, MySQL/MariaDB.

## Pré-requisitos

Um servidor MySQL ou MariaDB rodando localmente, com um banco chamado `blog_app` já criado:

```bash
mysql -u seu_usuario -p -e "CREATE DATABASE IF NOT EXISTS blog_app CHARACTER SET utf8mb4;"
```

Se o MariaDB acusar `Cannot load from mysql.proc` ao rodar as migrations, rode `sudo mariadb-upgrade` uma vez.

## Rodando o projeto

```bash
composer install
cp .env.example .env       # depois edite DB_USERNAME/DB_PASSWORD com suas credenciais
php artisan key:generate
php artisan migrate
php artisan serve
```

Sobe em `http://localhost:8000`. Crie usuários de teste pelo tinker:

```bash
php artisan tinker --execute="
App\Models\Usuario::firstOrCreate(['email' => 'bruno@email.com'], ['nome' => 'Bruno Lima', 'password' => bcrypt('senha456')]);
App\Models\Usuario::firstOrCreate(['email' => 'ana@email.com'], ['nome' => 'Ana Souza', 'password' => bcrypt('senha123')]);
"
```

## O que já está funcionando, de ponta a ponta — e como foi verificado

Todo item abaixo foi confirmado rodando o servidor de verdade (`php artisan serve`) e testando com `curl`/tinker contra um MySQL local real — não apenas revisado como código.

- **Formulário de newsletter e busca por querystring** (`GET/POST /newsletter`, `GET /artigos?tag=...`), com inspeção completa da requisição em `POST /api/inspecionar` — Tópico 1.
- **Login/logout com sessão** (`GET/POST /login`, `POST /logout`), incluindo checkbox de lembrar-me e rota protegida por `middleware('auth')` — Tópico 2.
- **Resistência a SQL Injection via Eloquent**: busca com o payload `' OR '1'='1` testada de verdade contra `/usuarios/busca` devolveu 0 resultados (não a tabela inteira), e o log de queries do tinker confirmou a query parametrizada (`... like ?`, payload no array de bindings) — Tópico 3.
- **XSS neutralizado por escape na saída**: um comentário com `<script>alert('XSS!')</script>` foi de fato gravado cru no banco (confirmado via `SELECT` direto) e veio escapado (`&lt;script&gt;...`) na página `/comentarios` — Tópico 3. A rota de demonstração insegura (`/comentarios-inseguro`) descrita no tutorial **não está incluída aqui** — ela existe só para fins didáticos de comparação lado a lado; mantê-la num projeto de referência reintroduziria a vulnerabilidade de propósito, o que não serve ao objetivo deste diretório.
- **Mass assignment bloqueado**: `Comentario::create(['texto' => ..., 'aprovado' => true])` com `aprovado` fora do `$fillable` grava `aprovado` como `null`, nunca `true` — confirmado no tinker.
- **CSRF automático**: `POST /comentarios` sem o token `_token` devolveu `419`; com o token extraído da página, `302` (sucesso) — Tópico 3.
- **Padrão DAO**: `PostController` injeta `PostDAOInterface`, nunca `Post::` diretamente. A troca por uma implementação em memória via `app()->instance(...)` foi executada no tinker, mudando o resultado de `get_class(app(PostDAOInterface::class))` sem tocar em `PostController.php` — Tópico 4.
- **Projeto Integrador**: post autenticado criado com `auth()->id()` como autor; exclusão por não-dono (`Ana` tentando excluir post do `Bruno`) devolveu `403`; `curl -X DELETE` sem CSRF devolveu `419` (não `403` — a distinção que o tutorial enfatiza); exclusão pelo dono devolveu `302` — Tópico 5.

## Estrutura, por tópico

Veja a seção "Projeto completo" em `Tutorial_PWII.md` para a árvore de arquivos completa, comentada arquivo a arquivo. Resumo rápido:

| Camada | Arquivo | Tópico |
|---|---|---|
| Model | `app/Models/Usuario.php` | 2 |
| Model | `app/Models/Post.php` | 4/5 |
| Model | `app/Models/Comentario.php` | 3 |
| DAO | `app/DAO/PostDAOInterface.php`, `PostDAO.php` | 4 |
| Controller | `NewsletterController` | 1 |
| Controller | `BuscaController`, `ComentarioController` | 3 |
| Controller | `SessaoController` | 2 |
| Controller | `PostController` | 4/5 |

## Banco de dados usado nas lições

`blog_app`, usuário `andre`/senha `1234` no ambiente de desenvolvimento em que este projeto foi verificado (ajuste `.env` para suas próprias credenciais).
