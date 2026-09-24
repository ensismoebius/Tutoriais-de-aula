# Tutorial — Programação para Web II (PWII)

Framework de referência: PHP com Laravel 13 (PHP 8.3+), banco MySQL/MariaDB. Dois projetos cumulativos são construídos ao longo do material: o `blog-app`, que cobre formulários, sessões, segurança e o padrão DAO; e o `task-manager` (Gerenciador de Tarefas), que cobre orientação a objetos, o padrão MVC completo, integração DAO+MVC, interatividade com Livewire e uma API JSON.

Convenção de nomes: tudo que é criado neste tutorial (classes, variáveis, nomes de tabela e de arquivo) é nomeado em português — `Usuario`, `PostController`, `tarefas`. Tudo que pertence ao framework ou a uma biblioteca externa mantém o nome original em inglês — `Request`, `Eloquent`, `Blade::directive()`. Essa distinção ajuda a enxergar de relance o que é seu para editar e o que é contrato do framework para usar exatamente como está.

## Sumário

- [O básico, para quem está começando agora](#o-básico-para-quem-está-começando-agora)
- [Conceitos fundamentais antes de começar](#conceitos-fundamentais-antes-de-começar)
- [1. Formulários/HTTP](#1-formulárioshttp)
- [2. Sessões](#2-sessões)
- [3. Segurança (SQL Injection, XSS, CSRF)](#3-segurança-sql-injection-xss-csrf)
- [4. Padrão DAO](#4-padrão-dao)
- [5. Projeto Integrador](#5-projeto-integrador)
- [6. Introdução a POO](#6-introdução-a-poo)
- [Factories — gerando dados de teste realistas](#factories--gerando-dados-de-teste-realistas)
- [7. POO Aplicada (herança, polimorfismo)](#7-poo-aplicada-herança-polimorfismo)
- [8. Padrão MVC](#8-padrão-mvc)
- [9. Implementação de Controller](#9-implementação-de-controller)
- [10. Views e interação com Controller](#10-views-e-interação-com-controller)
- [11. Revisão de MVC + helpers](#11-revisão-de-mvc--helpers)
- [12. Integração DAO + MVC](#12-integração-dao--mvc)
- [13. Interatividade sem reload — Livewire](#13-interatividade-sem-reload--livewire)
- [14. APIs + Web Services](#14-apis--web-services)
- [15. Projeto Final + Apresentações](#15-projeto-final--apresentações)
- [Revisão geral / Exercícios finais](#revisão-geral--exercícios-finais)
- [Projeto completo — todos os arquivos juntos](#projeto-completo--todos-os-arquivos-juntos)

Cada tópico é um tutorial *build-along*: execute cada passo no seu computador e confira o resultado antes de prosseguir. Cada seção termina com um **✅ Checkpoint** — pare e confirme que o resultado bate antes de continuar.

## O básico, para quem está começando agora

Esta seção existe para quem nunca programou para a web antes. Se os termos abaixo já são familiares, pode pular direto para "Conceitos fundamentais antes de começar".

**O modelo cliente-servidor.** Quando você abre um site, duas máquinas (ou dois programas) conversam com papéis diferentes. O **cliente** inicia a conversa pedindo alguma coisa — o navegador, o Postman, o `curl`. O **servidor** fica esperando por pedidos e responde a cada um. Uma aplicação Laravel como a que você vai construir aqui é, tecnicamente, um servidor: um programa que fica escutando requisições numa porta de rede e devolvendo uma resposta a cada uma.

**O que é HTTP.** HTTP é o conjunto de regras que cliente e servidor seguem para conversar pela web. O cliente manda uma **requisição** (*request*) com um método (`GET` para ler, `POST` para criar, entre outros), uma URL e, às vezes, um corpo. O servidor devolve uma **resposta** (*response*) com um **código de status** (`200` sucesso, `404` não encontrado, `419` token CSRF ausente/expirado, `403` proibido, `422` erro de validação — todos vão aparecer ao longo deste tutorial) e geralmente um corpo. HTTP é **sem estado** (*stateless*): por padrão, o servidor não lembra da requisição anterior do mesmo cliente — é justamente esse problema que o Tópico 2 (Sessões) resolve.

**O que é PHP, e uma diferença crucial em relação ao Node.js.** PHP é uma linguagem interpretada, desenhada desde o início para rodar **por trás de um servidor web**, respondendo a requisições HTTP. Se você já fez PWIII com Node.js/Express, há uma diferença que costuma pegar gente de surpresa: um processo Node fica **vivo** entre requisições — uma variável declarada fora de uma rota continua com o mesmo valor na requisição seguinte, a menos que você reinicie o servidor. Em PHP, cada requisição roda o script **do zero**: as variáveis do PHP não sobrevivem de uma requisição para a próxima. Um contador incrementado numa requisição volta ao valor original na próxima, porque é literalmente uma nova execução do interpretador. É por isso que qualquer coisa que precise "durar" entre requisições — o carrinho de compras, o usuário logado — precisa ser guardada em algum lugar que sobreviva sozinho: um banco de dados, um arquivo, ou a sessão (Tópico 2), que por baixo dos panos também é um arquivo (ou linha de banco) gravado no servidor.

**O que é um servidor web e o servidor embutido do Laravel.** Em produção, PHP normalmente roda atrás de um servidor web dedicado (Nginx ou Apache), que recebe a requisição HTTP e a repassa a um interpretador PHP. Para desenvolvimento, o Laravel inclui um atalho: `php artisan serve` sobe um servidor HTTP mínimo, embutido no próprio PHP, suficiente para testar localmente sem instalar Nginx. É o que você vai usar o tempo todo neste tutorial.

**O terminal, e o que significa "rodar um comando".** O terminal é uma forma de interagir com o computador digitando comandos de texto. Praticamente todo passo deste tutorial dentro de uma caixa de código (como `composer create-project laravel/laravel blog-app`, ou `php artisan serve`) é um comando para você digitar literalmente e confirmar com Enter. Vários passos pedem para manter um comando rodando (como `php artisan serve`) enquanto você usa um **segundo terminal** para testar — isso é normal.

**✅ Checkpoint:** você sabe explicar, com suas próprias palavras, por que uma variável PHP não "lembra" seu valor entre duas requisições, e o que isso implica para guardar o usuário logado.

## Conceitos fundamentais antes de começar

Com o básico alinhado, vale aprofundar seis ideias específicas deste material. Elas são intencionalmente curtas aqui — cada uma é retomada em profundidade no tópico onde passa a importar de verdade.

**O que é o Composer.** Assim como o npm resolve, em PWIII, o problema de "não reescrever do zero código que outras pessoas já publicaram", o **Composer** é o gerenciador de pacotes do PHP — mesmo papel, ecossistema diferente. Ele lê um arquivo `composer.json` (o equivalente do `package.json`), baixa as bibliotecas listadas para uma pasta `vendor/` (o equivalente do `node_modules/`) e gera um autoloader que permite usar `use Alguma\Classe;` sem `require` manual em cada arquivo. Você vai usá-lo já no Tópico 1, com `composer create-project laravel/laravel blog-app`.

**O que é o artisan.** `artisan` é a ferramenta de linha de comando que vem embutida em todo projeto Laravel — não é instalada à parte, é um arquivo PHP (`artisan`, na raiz do projeto) que você chama com `php artisan <comando>`. Ele gera código repetitivo (`make:model`, `make:controller`), roda migrations, abre um console interativo (`tinker`) e sobe o servidor de desenvolvimento. É comparável a um gerador de scaffolding, mas mora dentro do próprio projeto.

**O que um framework MVC como o Laravel resolve, que o Express (PWIII) deixa em aberto.** O Express é deliberadamente minimalista: ele resolve "receber uma requisição HTTP e chamar uma função", e o resto (ORM, sistema de views, autenticação, validação, estrutura de pastas) é escolha sua, montada peça por peça. O Laravel é um framework **opinativo**: ele já vem com um ORM (Eloquent), um sistema de templates (Blade), autenticação, validação, um container de injeção de dependências e uma estrutura de pastas padronizada (`app/Models`, `app/Http/Controllers`, `resources/views`). A vantagem é produtividade — menos decisões de infraestrutura a tomar; a contrapartida é que você aprende as convenções do framework, não escolhe cada peça isoladamente.

**O que é o Eloquent, e o paralelo com o Prisma.** Eloquent é o ORM (*Object-Relational Mapper*) que vem com o Laravel — ocupa, na sua aplicação, o mesmo lugar que o Prisma ocupa em PWIII: traduzir entre classes PHP e linhas de tabela, sem você escrever SQL manualmente na maioria dos casos. A diferença de estilo mais visível é que o Prisma gera um cliente a partir de um arquivo `schema.prisma` central, enquanto o Eloquent usa **Active Record**: cada Model (`App\Models\Post`) é, ele mesmo, a classe que sabe se salvar, se buscar e se relacionar (`Post::create(...)`, `Post::find(1)`, `$post->usuario`) — sem uma etapa de geração de código separada.

**O que é o Blade.** Blade é o motor de templates do Laravel: arquivos `.blade.php` que misturam HTML com uma sintaxe enxuta para lógica de apresentação — `{{ $variavel }}` para exibir um valor (já escapado contra XSS, como você vai ver em detalhe no Tópico 3), `@foreach`, `@if`, entre outras diretivas. Ele ocupa, no backend, o papel que os componentes React ocupam no frontend de PWIII: decidir como os dados viram HTML — só que aqui o HTML é renderizado no servidor, antes de chegar ao navegador.

**O que é injeção de dependências / o Service Container.** Em vez de uma classe criar (`new`) as próprias dependências, ela **declara** o que precisa (geralmente no construtor) e deixa o framework entregar essa dependência pronta. O Laravel faz isso através do **Service Container**: um registro central de "quando alguém pedir X, entregue Y". Isso é o que permite, por exemplo, que um Controller peça uma interface no construtor e nunca precise saber qual classe concreta a está implementando — o assunto central do Tópico 4.

**O que é uma migration.** Uma migration é um arquivo PHP versionado que descreve uma mudança na estrutura do banco (criar uma tabela, adicionar uma coluna) — o equivalente direto das migrations do Prisma em PWIII. Rodar `php artisan migrate` aplica, em ordem, todas as migrations ainda não aplicadas; qualquer colega que clone o projeto e rode o mesmo comando chega ao mesmo esquema de banco, sem precisar trocar um dump SQL manualmente.

**✅ Checkpoint:** você consegue explicar, numa frase cada, o que Composer, artisan, Eloquent, Blade, Service Container e migration fazem — mesmo sem ainda ter escrito código com eles.

## 1. Formulários/HTTP

**Objetivo:** criar o projeto `blog-app` do zero e entender como o Laravel lê dados enviados por formulário (`POST`) e por querystring (`GET`), em contraste com as superglobais do PHP puro.

### Pré-requisitos

```bash
php --version     # Deve mostrar 8.3+
composer --version
```

**✅ Checkpoint:** os comandos retornam versões sem erro.

### Passo 1 — relembre o PHP puro (superglobais)

Antes de qualquer framework, vale lembrar como PHP puro lê dados de uma requisição:

```php
<?php
// newsletter.html envia via POST
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $email = $_POST['email'] ?? '';
    echo "Inscrito: $email";
}

// ?tag=laravel na URL de busca de artigos
$tag = $_GET['tag'] ?? '';
```

`$_POST` traz dados do **corpo** da requisição; `$_GET` traz dados da **URL** (querystring). Ambos devolvem sempre `string` — qualquer conversão de tipo é manual em PHP puro. O Laravel, como você verá a seguir, embrulha os dois num único objeto `Request`, com métodos que já resolvem essa distinção para você.

**✅ Checkpoint:** você sabe dizer, sem olhar o código, se um dado veio de POST ou de GET.

### Passo 2 — crie o projeto Laravel

```bash
composer create-project laravel/laravel blog-app
cd blog-app
php artisan serve
```

**✅ Checkpoint:** `http://localhost:8000` mostra a tela padrão do Laravel.

> 💡 Se você for usar um assistente de IA (Claude Code, Cursor, e outros) para ajudar a construir este projeto, considere instalar o **Laravel Boost** — um pacote oficial do próprio time do Laravel, feito exatamente para isso. Ele expõe, via MCP, ferramentas específicas do seu projeto (rodar `tinker`, consultar o schema do banco, buscar na documentação da versão exata do Laravel instalada, entre outras) e gera um `AGENTS.md`/`CLAUDE.md` com boas práticas específicas do ecossistema Laravel — reduzindo o risco de o assistente sugerir uma API de uma versão diferente da que está instalada aqui. É opcional, não faz parte do currículo, e não muda nada no código do projeto em si:
>
> ```bash
> composer require laravel/boost --dev
> php artisan boost:install
> ```
>
> O instalador pergunta quais assistentes de IA você usa e configura cada um automaticamente.

### Passo 3 — formulário de inscrição na newsletter

Gere o controller:

```bash
php artisan make:controller NewsletterController
```

Em `routes/web.php`, adicione:

```php
use App\Http\Controllers\NewsletterController;

Route::get('/newsletter', [NewsletterController::class, 'mostrarFormulario']);
Route::post('/newsletter', [NewsletterController::class, 'inscrever']);
```

Em `app/Http/Controllers/NewsletterController.php`:

```php
<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;

class NewsletterController extends Controller
{
    public function mostrarFormulario()
    {
        return view('newsletter');
    }

    public function inscrever(Request $request)
    {
        $email = $request->input('email');
        return "Inscrito com sucesso: {$email}";
    }
}
```

Crie `resources/views/newsletter.blade.php`:

```blade
<!DOCTYPE html>
<html>
<body>
    <form method="POST" action="/newsletter">
        @csrf
        <input type="email" name="email" placeholder="seu@email.com" required>
        <button type="submit">Inscrever</button>
    </form>
</body>
</html>
```

`@csrf` gera o token que protege contra *Cross-Site Request Forgery* — sem ele, o Laravel rejeita o POST com erro 419. O Tópico 3 explica esse mecanismo em detalhe; por ora, memorize a regra: todo formulário `POST`/`PUT`/`PATCH`/`DELETE` precisa de `@csrf`.

**✅ Checkpoint:** ao inscrever um email, aparece "Inscrito com sucesso: seu@email.com".

### Passo 4 — busca de artigos por tag (GET)

Adicione a rota:

```php
Route::get('/artigos', [NewsletterController::class, 'buscarArtigos']);
```

No controller:

```php
public function buscarArtigos(Request $request)
{
    $tag = $request->query('tag');

    if (!$tag) {
        return "Informe uma tag: /artigos?tag=laravel";
    }

    return "Artigos marcados com: {$tag}";
}
```

Teste: `http://127.0.0.1:8000/artigos?tag=laravel`.

**✅ Checkpoint:** a página mostra "Artigos marcados com: laravel".

### Passo 5 — todas as formas de ler a requisição

Crie `routes/api.php`:

```php
use App\Http\Controllers\NewsletterController;
use Illuminate\Support\Facades\Route;

Route::post('/inspecionar', [NewsletterController::class, 'inspecionar']);
```

E em `NewsletterController`:

```php
public function inspecionar(Request $request)
{
    return [
        'metodo'          => $request->method(), // GET, POST...
        'url_completa'    => $request->fullUrl(),
        'so_email'        => $request->only('email'),
        'tudo_menos_csrf' => $request->except('_token'),
        'e_ajax'          => $request->ajax(),
        'e_json'          => $request->expectsJson(),
    ];
}
```

Registre o grupo de rotas de API em `bootstrap/app.php` — nas versões atuais do Laravel (11+) não existe mais `Kernel.php`; rotas e middlewares são registrados diretamente aqui:

```php
use Illuminate\Foundation\Application;
use Illuminate\Foundation\Configuration\Exceptions;
use Illuminate\Foundation\Configuration\Middleware;
use Illuminate\Http\Request;

return Application::configure(basePath: dirname(__DIR__))
    ->withRouting(
        web: __DIR__.'/../routes/web.php',
        api: __DIR__.'/../routes/api.php', // <-- adicionado
        commands: __DIR__.'/../routes/console.php',
        health: '/up',
    )
    ->withMiddleware(function (Middleware $middleware): void {
        //
    })
    ->withExceptions(function (Exceptions $exceptions): void {
        $exceptions->shouldRenderJsonWhen(
            fn (Request $request) => $request->is('api/*') || $request->expectsJson(),
        );
    })->create();
```

Teste com curl:

```bash
curl -s -X POST http://localhost:8000/api/inspecionar \
  -H "Accept: application/json" \
  -d "email=teste@email.com"
```

**✅ Checkpoint:** o JSON retornado mostra método, URL, e-mail isolado e os outros campos:

```json
{"metodo":"POST","url_completa":"http:\/\/localhost:8000\/api\/inspecionar","so_email":{"email":"teste@email.com"},"tudo_menos_csrf":{"email":"teste@email.com"},"e_ajax":false,"e_json":true}
```

Este exemplo foi verificado de verdade na implementação de referência (`pwii-projetos/blog-app`), rodando `php artisan serve` numa porta local e fazendo exatamente esse `curl` — a saída acima é a saída real.

### Passo 6 — valide o email antes de aceitar

```php
public function inscrever(Request $request)
{
    $dados = $request->validate([
        'email' => 'required|email|max:255',
    ]);

    return "Inscrito validado: {$dados['email']}";
}
```

`$request->validate()` faz três coisas de uma vez: verifica as regras, **interrompe** a execução redirecionando de volta se algo falhar, e devolve **apenas** os campos validados — mesmo que o formulário tenha enviado campos extras. Tente enviar `"nao-e-email"` no campo — o Laravel bloqueia automaticamente e devolve os erros (redirect com mensagens, ou JSON 422 se a requisição pedir JSON).

**✅ Checkpoint:** um valor sem "@" é rejeitado; um email válido passa.

### Resumo do que você construiu

```
✅ Diferença entre $_POST/$_GET (PHP puro) e Request (Laravel)
✅ Formulário de newsletter com proteção CSRF
✅ Busca por querystring (?tag=...) com $request->query()
✅ Inspeção completa da requisição (método, URL, campos filtrados)
✅ Validação automática de email com $request->validate()
```

### Exercícios

1. **Confirmação de email**: adicione um campo `email_confirmation` e valide com a regra `confirmed`.
2. **Busca combinada**: `/artigos?tag=laravel&autor=Ana` — leia os dois parâmetros juntos.
3. **Duplicidade**: rejeite emails já inscritos usando `unique` (simule com um array fixo, sem tabela ainda).
4. **Mensagem customizada**: personalize a mensagem de erro de validação com o segundo argumento de `validate()`.

### Perguntas de fixação

1. Por que `$_POST['email']` sempre devolve `string`, mesmo que o campo pareça um número?
2. O que `$request->validate()` faz que um `if` manual não faria automaticamente?
3. Por que a rota GET de `/newsletter` e a rota POST de `/newsletter` são registradas separadamente, em vez de uma única rota tratando os dois casos?

---

## 2. Sessões

**Objetivo:** entender por que HTTP sem estado exige sessão e cookie trabalhando juntos, e construir um sistema de login completo — com logout e "lembrar-me" — no `blog-app`.

### O problema que vamos resolver

HTTP é **stateless**: cada requisição chega ao servidor sem memória da anterior. Se você faz login e depois clica num link, a segunda requisição, sozinha, não tem como provar que foi você. A solução tem duas peças que trabalham juntas:

| Peça | Onde fica | O que guarda |
|---|---|---|
| **Cookie** | No navegador do usuário | Apenas um identificador |
| **Sessão** | No servidor | Os dados reais (o id do usuário), indexados por aquele identificador |

O navegador reenvia o cookie automaticamente a cada requisição; o servidor usa o identificador para reencontrar os dados. **Nunca** guardamos o ID do usuário dentro do cookie — só a "senha do cofre", não o conteúdo. O nome do cookie de sessão sai do `APP_NAME` do `.env` — com o valor padrão "Laravel", ele se chama `laravel-session`. Confira no DevTools do navegador, aba Application → Cookies.

### Pré-requisitos

```bash
php --version       # 8.2 ou superior
composer --version  # 2.x
mysql --version     # 8.x (ou MariaDB 10+)
```

**✅ Checkpoint:** os três comandos retornam versões sem erro.

### Passo 0 — crie o projeto Laravel (pule se já tem o `blog-app` do Tópico 1)

```bash
composer create-project laravel/laravel blog-app
cd blog-app
```

O `create-project` baixa o esqueleto do Laravel, roda `composer install` e gera a `APP_KEY` — a chave usada para **assinar e criptografar os cookies**. Sem ela, a sessão não funciona. O instalador deixa o projeto rodando em **SQLite** por padrão, com um arquivo `database/database.sqlite` já migrado. Como esta aula usa MySQL, é preciso trocar a configuração.

Crie o banco:

```bash
mysql -u root -p -e "CREATE DATABASE blog_app CHARACTER SET utf8mb4;"
```

Abra o `.env`. Você vai encontrar algo assim, com as linhas de MySQL **comentadas**:

```
DB_CONNECTION=sqlite
# DB_HOST=127.0.0.1
# DB_PORT=3306
# DB_DATABASE=laravel
# DB_USERNAME=root
# DB_PASSWORD=
```

Descomente e ajuste:

```
DB_CONNECTION=mysql
DB_HOST=127.0.0.1
DB_PORT=3306
DB_DATABASE=blog_app
DB_USERNAME=root
DB_PASSWORD=sua_senha
```

O `.env` guarda configurações que **mudam por máquina** (e segredos) — nunca vai para o Git; é por isso que existe o `.env.example` versionado, sem valores reais. Rode as migrations no banco novo e suba o servidor:

```bash
php artisan migrate
php artisan serve
```

**✅ Checkpoint:** o servidor sobe sem erros e o endereço local mostra a tela inicial do Laravel.

### Passo 1 — sessão vs cookie de "lembrar-me"

Existem **dois** mecanismos diferentes em jogo:

- **Sessão comum**: expira quando o navegador fecha (ou após o tempo configurado em `config/session.php`). O cookie que a identifica é um *session cookie*, sem data de validade — o navegador o descarta ao fechar.
- **Cookie de lembrar-me**: sobrevive ao fechar o navegador. O Laravel gera um token de longa duração, guarda-o **no banco** (coluna `remember_token`) e **no cookie**, além da sessão normal.

| | Sessão comum | Remember me |
|---|---|---|
| Vive até | Fechar o navegador / expirar | ~5 anos (padrão do Laravel) |
| Guardado no servidor | Sim (arquivo/banco de sessão) | Sim (coluna de token na tabela) |
| Guardado no navegador | Cookie de sessão | Cookie persistente |
| Some ao fechar o navegador | **Sim** | Não |

Guardar o token no banco também permite invalidá-lo: trocar o token do usuário derruba instantaneamente todos os dispositivos "lembrados" — é o que faz o botão "sair de todos os dispositivos".

**✅ Checkpoint:** você entende por que lembrar-me não pode depender só da sessão comum.

### Passo 2 — crie a tabela de usuários (com suporte a remember token)

```bash
php artisan make:model Usuario -m
```

`make:model Usuario` cria `app/Models/Usuario.php`; a opção `-m` cria também a *migration* — o histórico versionado do banco. Abra a migration criada em `database/migrations/` e deixe o método de subida assim:

```php
public function up(): void
{
    Schema::create('usuarios', function (Blueprint $table) {
        $table->id();
        $table->string('nome');
        $table->string('email')->unique();
        $table->string('password');
        $table->rememberToken();
        $table->timestamps();
    });
}
```

- `$table->id()` cria a chave primária BIGINT auto-incremento.
- `$table->string('email')->unique()` cria um índice único — o banco passa a **impedir** dois cadastros com o mesmo e-mail.
- `$table->string('password')` guarda o **hash** da senha, nunca a senha em texto puro.
- `$table->rememberToken()` cria a coluna do lembrar-me, um VARCHAR(100) que aceita nulo — o PASSO 1 explicou por quê.
- `$table->timestamps()` cria `created_at`/`updated_at`, preenchidas automaticamente pelo Eloquent.

Aplique a migration:

```bash
php artisan migrate
```

Ajuste o **model**, em `app/Models/Usuario.php`:

```php
<?php

namespace App\Models;

use Illuminate\Foundation\Auth\User as Authenticatable;

class Usuario extends Authenticatable
{
    protected $table = 'usuarios';

    protected $fillable = ['nome', 'email', 'password'];

    protected $hidden = ['password', 'remember_token'];
}
```

`extends Authenticatable` é **essencial** — é essa classe base que fornece os métodos que o sistema de login usa, inclusive os do lembrar-me; herdar do `Model` comum faria `Auth::attempt()` falhar. `$table = 'usuarios'` evita a pluralização automática em inglês que o Eloquent tentaria. `$fillable` protege contra *mass assignment* (o Tópico 3 aprofunda isso). `$hidden` esconde esses campos quando o model vira JSON.

Diga ao Laravel para usar este model. Em `config/auth.php`, dentro do bloco de providers:

```php
'providers' => [
    'users' => [
        'driver' => 'eloquent',
        'model' => App\Models\Usuario::class,
    ],
],
```

Depois, limpe o cache de configuração:

```bash
php artisan config:clear
```

**✅ Checkpoint:** `DESCRIBE usuarios` no MySQL mostra a coluna de remember token.

### Passo 3 — crie um usuário de teste

```bash
php artisan tinker
```

O *tinker* é um console interativo com o Laravel inteiro carregado — útil para testar sem escrever uma rota.

```php
App\Models\Usuario::create([
    'nome' => 'Bruno Lima',
    'email' => 'bruno@email.com',
    'password' => bcrypt('senha456'),
]);
```

O ponto crítico é `bcrypt('senha456')`: transforma a senha num **hash**, uma função de mão única, impossível de reverter — o banco nunca vê a senha real. Na hora de entrar, o Laravel aplica a mesma função à senha digitada e **compara os hashes**, nunca "descriptografa" nada.

**⚠️ Armadilha — salvar a senha sem hash.** Se você gravar a senha direto como texto, o cadastro funciona, mas `Auth::attempt()` (Passo 5) sempre falhará: ele compara o hash da senha digitada com o texto puro do banco, e nunca batem. O sintoma é "email ou senha inválidos" mesmo com a senha certa.

**✅ Checkpoint:** consultando a coluna de senha no banco, o valor começa com `$2y$` — é um hash, não a senha.

### Passo 4 — formulário de login com checkbox de lembrar-me

Em `routes/web.php`:

```php
use App\Http\Controllers\SessaoController;

Route::get('/login', [SessaoController::class, 'mostrarLogin'])->name('login');
Route::post('/login', [SessaoController::class, 'entrar']);
Route::post('/logout', [SessaoController::class, 'sair'])->middleware('auth');
```

`/login` aparece duas vezes, com verbos diferentes — o `GET` **exibe** o formulário; o `POST` **processa** o envio. Essa separação segue a semântica do HTTP: GET lê, POST altera estado. O logout é POST pelo mesmo motivo — um GET de logout poderia ser disparado por qualquer link em outro site, deslogando o usuário sem que ele quisesse. E leva `middleware('auth')`: só faz sentido derrubar uma sessão que já existe, então a mesma proteção que o Passo 6 aplica a rotas autenticadas já se aplica aqui.

**Guarde o `->name('login')`** — ele volta a importar no Passo 6. Quando o middleware de autenticação barra um visitante, ele redireciona procurando uma rota **chamada** `login`. Sem esse nome, o Laravel não redireciona: lança um erro 500 com "Route [login] not defined".

```bash
php artisan make:controller SessaoController
```

Crie também a subpasta `resources/views/sessao/` — agrupar as views de login (e as que vierem depois, ligadas à sessão) numa pasta própria evita que `resources/views/` vire uma lista plana conforme o projeto cresce. `resources/views/sessao/login.blade.php`:

```blade
<!DOCTYPE html>
<html>
<body>
    @if ($errors->any())
        <p style="color:red">{{ $errors->first() }}</p>
    @endif
    <form method="POST" action="/login">
        @csrf
        <input type="email" name="email" placeholder="email">
        <input type="password" name="password" placeholder="senha">
        <label><input type="checkbox" name="lembrar"> Lembrar-me</label>
        <button type="submit">Entrar</button>
    </form>
</body>
</html>
```

Três pontos importantes: `$errors` é uma variável que o Laravel injeta **automaticamente** em toda view — mesmo quando nada deu errado, ela existe, só que vazia — desde que o controller devolva os erros com `withErrors()` (Passo 5), nunca `session('erro')` manual; é esse mecanismo padrão, e não uma chave inventada, que faz a mensagem aparecer sem risco de as duas pontas divergirem em silêncio. `@csrf` é obrigatório — sem ele, 419. Os atributos `name` (não os `id`) viram as chaves recebidas no servidor. O checkbox `lembrar`, quando desmarcado, **não é enviado** — a chave simplesmente não existe na requisição, o que exige tratamento especial no Passo 5.

**✅ Checkpoint:** a rota de login mostra o formulário com o checkbox.

### Passo 5 — implemente login com suporte a lembrar-me

Em `app/Http/Controllers/SessaoController.php`, montado por partes. O cabeçalho e a exibição do formulário:

```php
<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;

class SessaoController extends Controller
{
    public function mostrarLogin()
    {
        return view('login');
    }
```

`Auth` é a fachada que dá acesso ao sistema de autenticação. A validação dos dados recebidos:

```php
    public function entrar(Request $request)
    {
        $credenciais = $request->validate([
            'email' => 'required|email',
            'password' => 'required|string',
        ]);
```

Repare que não há `try/catch` ao redor de `validate()`: quando uma regra falha, `validate()` lança `ValidationException`, e o Laravel já intercepta essa exceção sozinho — redireciona de volta com os erros, sem que o controller precise fazer nada especial. Um `try/catch` genérico ali só esconderia esse comportamento padrão sem ganhar nada. A leitura do checkbox:

```php
        $lembrar = $request->boolean('lembrar');
```

`boolean()` resolve os dois casos com segurança: converte "on" em `true` e a ausência da chave em `false`. Ler o campo direto daria `null` e um aviso de índice indefinido. A tentativa de autenticação:

```php
        if (! Auth::attempt($credenciais, $lembrar)) {
            return back()->withErrors(['email' => 'Credenciais inválidas.']);
        }

        $request->session()->regenerate();

        return redirect('/posts');
    }
```

`Auth::attempt($credenciais, $lembrar)` busca o usuário pelo e-mail, aplica o hash na senha digitada e compara com o banco. Se bater, cria a sessão e devolve `true` — o **segundo argumento** é o lembrar-me: quando verdadeiro, o Laravel gera o token de longa duração e envia o cookie persistente. `$request->session()->regenerate()` gera um **novo ID de sessão**, prevenindo *session fixation*: um atacante que tivesse forçado um ID conhecido antes do login perde o acesso, porque o ID muda no instante da autenticação. **Nunca omita esta linha.** A mensagem de erro genérica ("Credenciais inválidas.") é intencional — não distingue "e-mail não existe" de "senha errada", evitando que alguém descubra quais e-mails estão cadastrados. E ela chega à view por `withErrors()`, a mesma variável `$errors` que o Passo 4 já lê — sem inventar uma chave de sessão paralela.

O destino do redirect, depois de logado, é `/posts` — a listagem de posts que o Tópico 4/5 constrói sobre este mesmo projeto; é ali, e não numa rota de "painel" separada, que a sessão autenticada passa a fazer diferença visível (o Passo 6 mostra exatamente o que muda na tela). O logout:

```php
    public function sair(Request $request)
    {
        Auth::logout();
        $request->session()->invalidate();
        $request->session()->regenerateToken();
        return redirect('/login');
    }
}
```

Sair exige as três linhas, cada uma desfazendo uma peça diferente:

| Linha | O que desfaz |
|---|---|
| `Auth::logout()` | Remove o usuário da sessão e troca o token de lembrar-me |
| `session()->invalidate()` | Destrói os dados da sessão e o seu ID |
| `session()->regenerateToken()` | Gera novo token CSRF, para a tela de login seguinte funcionar |

Omitir a segunda deixaria dados antigos acessíveis na mesma sessão; omitir a terceira faria o próximo login falhar com 419.

**✅ Checkpoint:** com senha errada, a mensagem vermelha aparece na tela; com a senha certa, o navegador é redirecionado para `/posts`.

### Passo 6 — proteja rotas e mostre dados da sessão

`/posts` (o destino do redirect do Passo 5) é a listagem de posts que o Tópico 5 constrói neste mesmo `blog-app` — o projeto é cumulativo, e o `routes/web.php` final já tem essas rotas ao lado das de sessão:

```php
Route::get('/posts/novo', [PostController::class, 'mostrarFormulario'])->middleware('auth');
Route::post('/posts', [PostController::class, 'criar'])->middleware('auth');
Route::delete('/posts/{id}', [PostController::class, 'excluir'])->middleware('auth');
```

`middleware('auth')` é um filtro que roda *antes* da rota: verifica se há usuário autenticado e, se não houver, redireciona para a rota chamada `login` — a rota protegida nem chega a executar. Esse redirecionamento só funciona porque a rota de login tem nome (Passo 4); sem ele, erro 500. Você já tem, deste próprio tópico, uma rota protegida de verdade para testar isso sem esperar pelo Tópico 5: `/logout` (Passo 4) também leva `middleware('auth')` — tente `POST /logout` deslogado e confira o redirecionamento para `/login`.

A listagem pública em `/posts` já mostra, hoje, como uma view lê dados da sessão sem exigir login para a página inteira — só o conteúdo muda conforme `Auth::check()`:

```blade
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
```

`auth()->user()` (a função helper, equivalente a `Auth::user()`) devolve o model do usuário autenticado, ou `null` se ninguém estiver logado — por isso a diretiva `@auth`/`@else` do Blade, e não um `if` cego que quebraria para visitante anônimo. Dentro do bloco `@auth`, o model nunca é nulo.

Uma peça relacionada, que você vai usar de verdade no Tópico 4/5, é `Auth::viaRemember()`: informa se a sessão atual foi restaurada a partir do cookie de lembrar-me ou de um login normal — útil para exigir reautenticação em ações sensíveis, por exemplo:

```php
if (Auth::viaRemember()) {
    return redirect('/login')->with('aviso', 'Confirme sua senha para continuar.');
}
```

Este projeto de referência não chama `viaRemember()` em lugar nenhum ainda — fica como o Exercício 2 desta seção. **Como testar o lembrar-me de verdade, mesmo sem essa checagem:** faça login com o checkbox marcado; no DevTools, aba Application → Cookies, você verá dois cookies: `laravel-session` e um começando com `remember_web_`. Apague **apenas** o cookie de sessão e recarregue `/posts` — você continua logado (o nome continua aparecendo no topo da página), prova de que a sessão foi reconstruída a partir do cookie persistente, mesmo sem nenhum código checando isso explicitamente.

**✅ Checkpoint:** deslogado, `POST /logout` redireciona para `/login` em vez de executar; logado, `/posts` mostra "Logado como <nome>"; depois de apagar só o cookie de sessão (mantendo o de lembrar-me), `/posts` continua mostrando o nome do usuário.

### Passo 7 — Login com Google (Identity Services)

Até aqui, confirmar a identidade de alguém sempre significou a mesma coisa: comparar o hash de uma senha com `Auth::attempt()`. O Google oferece outra forma de fazer essa mesma confirmação, sem o `blog-app` nunca ver uma senha: a pessoa loga direto com a conta Google dela, e o Google devolve um **ID token** — um JWT assinado pelo próprio Google, provando quem ela é. O papel do backend não muda em relação ao que o Passo 5 já faz: ainda é "confirmar identidade, depois `Auth::login()`". Só a **forma de confirmar** muda — em vez de checar um hash, o backend confere a assinatura do Google. O destino, depois de confirmada a identidade, é o mesmo de sempre: uma sessão válida, criada exatamente como o Passo 5 já cria.

**7.1 — crie um Client ID no Google Cloud.** Acesse [console.cloud.google.com](https://console.cloud.google.com) → **APIs & Services** → **Credentials** → **Create Credentials** → **OAuth client ID** → tipo **Web application**. Em **Authorized JavaScript origins**, adicione `http://localhost:8000` — a URL que `php artisan serve` usa neste tutorial (não confunda com a porta `:5173` do Vite; o `blog-app` é renderizado pelo servidor, não empacotado por um bundler). Ao final, o Google mostra um **Client ID**, algo como `123456-abc.apps.googleusercontent.com`. Ele não é secreto — vai parar no HTML da página, visível para qualquer visitante — e este fluxo não usa client secret nenhum.

**7.2 — instale o `google/apiclient`.** É a biblioteca oficial do Google para PHP, equivalente ao `google-auth-library` que um projeto Node usaria:

```bash
composer require google/apiclient
```

⚠️ **Esse comando, do jeito que está, instala a versão errada.** O Laravel 13 fixa `guzzlehttp/guzzle` na versão `^8`, e o `google/apiclient` 2.x (a versão atual, com o namespace `Google\Client`) exige Guzzle `~7`. Sem conseguir satisfazer os dois ao mesmo tempo, o Composer não avisa e não falha — ele silenciosamente resolve para a `google/apiclient` **v1.1.9**, lançada em 2020, sem `composer.json` moderno, com a classe legada `Google_Client` (sem namespace, autoload baseado em arquivo). É fácil não perceber, porque o comando roda sem erro nenhum. Force a versão atual e deixe o Composer resolver o conflito de verdade, rebaixando o Guzzle:

```bash
composer require "google/apiclient:^2.9" -W
```

A flag `-W` (`--with-all-dependencies`) autoriza o Composer a também ajustar dependências já travadas — neste caso, o Guzzle do projeto inteiro volta de `8.x` para `7.15.x`, a versão mais recente que o `google/apiclient` 2.x aceita. Isso é uma mudança de verdade em todo o projeto, não só nesta feature: qualquer código que já usasse o `Http` facade do Laravel (que roda sobre Guzzle por baixo) continua funcionando — a API pública do Guzzle 7 não muda entre essas versões para o uso que o Laravel faz dela — mas vale rodar a suíte de testes depois, o que a seção de verificação deste passo já cobre.

Acrescente o Client ID do Passo 7.1 ao `.env`:

```
GOOGLE_CLIENT_ID=123456-abc.apps.googleusercontent.com
```

E o placeholder correspondente no `.env.example`, nunca o valor real:

```
GOOGLE_CLIENT_ID=seu-client-id.apps.googleusercontent.com
```

Para expor essa variável a partir de um lugar central — em vez de espalhar `env('GOOGLE_CLIENT_ID')` pelo controller e pela view — crie uma entrada em `config/services.php`, o arquivo que o próprio Laravel já reserva para credenciais de serviços de terceiros:

```php
'google' => [
    'client_id' => env('GOOGLE_CLIENT_ID'),
],
```

`env()` só deve ser chamado dentro de arquivos de `config/`; em qualquer outro lugar (controller, view), a forma correta de ler esse valor é `config('services.google.client_id')` — é esse `config()` que a view do Passo 7.6 vai usar.

**✅ Checkpoint:** `composer show google/apiclient` mostra `v2.19.4` (ou outra `v2.x`), não `v1.1.9`; `php artisan config:show services.google` mostra o Client ID do `.env`.

**7.3 — a tabela `usuarios` ganha uma conta possível sem senha.** Uma pessoa que só loga pelo Google nunca digita uma senha neste app — então `password` precisa deixar de ser obrigatório, e uma nova coluna `google_id` precisa guardar o identificador estável que o Google atribui à conta (o campo `sub` do payload, que aparece no Passo 7.4):

```bash
php artisan make:migration add_google_id_to_usuarios_table --table=usuarios
```

```php
public function up(): void
{
    Schema::table('usuarios', function (Blueprint $table) {
        $table->string('password')->nullable()->change();
        $table->string('google_id')->nullable()->unique()->after('password');
    });
}

public function down(): void
{
    Schema::table('usuarios', function (Blueprint $table) {
        $table->dropColumn('google_id');
        $table->string('password')->nullable(false)->change();
    });
}
```

```bash
php artisan migrate
```

Atualize o `$fillable` do model, em `app/Models/Usuario.php`:

```php
protected $fillable = ['nome', 'email', 'password', 'google_id'];
```

Isso levanta uma pergunta que vale testar de verdade, não supor: se uma conta existe só com Google (`password` nulo) e alguém tenta entrar por ela usando o **formulário de senha** do Passo 5, o que acontece? Em outras linguagens isso já quebrou de verdade — comparar uma senha com um hash nulo pode lançar exceção em vez de simplesmente "não bater". Testando contra este projeto, com um usuário `password = null` de propósito:

```php
Auth::attempt(['email' => 'conta-so-google@exemplo.com', 'password' => 'qualquercoisa']);
// => false, sem exceção
```

Sem crash. `Auth::attempt()` chama, por baixo, `Hash::check()` — e o hasher do Laravel (`Illuminate\Hashing\AbstractHasher::check()`) tem essa checagem logo no início:

```php
if (is_null($hashedValue) || (string) $hashedValue === '') {
    return false;
}
```

Um hash nulo ou vazio nunca chega a ser passado para `password_verify()` — o método já devolve `false` antes disso. Não é preciso nenhuma guarda extra no `SessaoController::entrar()` para esse caso: uma conta só-Google recebe, ao tentar logar por senha, a mesma mensagem genérica "Credenciais inválidas" que qualquer outra tentativa errada — comportamento correto, e já coberto pelo código do Passo 5.

**✅ Checkpoint:** `DESCRIBE usuarios` mostra `password` aceitando `NULL` e a nova coluna `google_id`; um usuário de teste com `password` nulo tentando logar pelo formulário de senha recebe a mensagem de erro normal, sem página de erro 500.

**7.4 — confira o `credential` recebido, sem confiar em exceções para isso.** A chamada central é:

```php
use Google\Client;

$client = new Client(['client_id' => $googleClientId]);
$payload = $client->verifyIdToken($credential);
```

Aqui está uma diferença real em relação a bibliotecas Node como a `google-auth-library` (que rejeita uma *promise* quando o token é inválido, exigindo `try/catch`): o `google/apiclient` devolve `false` quando a verificação falha — assinatura errada, `aud` diferente do Client ID configurado, token expirado. É preciso checar `=== false` explicitamente, um `try/catch` sozinho não cobre esse caminho.

Só que "devolve `false`" não é a história completa, e só ficou claro testando com um valor realmente malformado — não um JWT com assinatura errada, mas uma string que nem chega a ter o formato `cabeçalho.payload.assinatura`:

```bash
curl -X POST http://localhost:8000/login/google -d "credential=isto-nao-eh-um-jwt"
```

Isso derrubou a rota com um **500**, não um erro tratado. O motivo: por dentro, `verifyIdToken()` decodifica o token com a biblioteca `firebase/php-jwt`, e só captura internamente `ExpiredException`, `SignatureInvalidException` e `DomainException` — um token sem a estrutura de três segmentos faz o `firebase/php-jwt` lançar `UnexpectedValueException`, que escapa sem ser tratada. Ou seja: `verifyIdToken()` retorna `false` para *alguns* jeitos de token inválido, mas lança exceção para outros. Isolar essa chamada numa classe própria — em vez de espalhar `new Google\Client(...)` direto pelo controller — dá um lugar único para cobrir os dois casos:

```php
namespace App\Services;

use Google\Client;

class GoogleClientIdTokenVerifier implements GoogleIdTokenVerifier
{
    public function __construct(private readonly string $googleClientId) {}

    public function verificar(string $credential): ?array
    {
        $client = new Client(['client_id' => $this->googleClientId]);

        try {
            $payload = $client->verifyIdToken($credential);
        } catch (\UnexpectedValueException) {
            return null;
        }

        return $payload === false ? null : $payload;
    }
}
```

com a interface correspondente:

```php
namespace App\Services;

interface GoogleIdTokenVerifier
{
    /** @return array<string, mixed>|null */
    public function verificar(string $credential): ?array;
}
```

Por que uma interface para uma coisa tão pequena? Pelo mesmo motivo do `PostDAOInterface` que você vai construir no Tópico 4 — `verificar()` faz uma chamada de rede de verdade até o Google, então testar o controller sem depender de uma conta Google real exige poder trocar essa peça por uma versão falsa. O Tópico 4 nomeia esse padrão formalmente; aqui você já está usando a mesma ideia, um passo à frente. Registre o binding em `app/Providers/AppServiceProvider.php`, junto de qualquer outro bind que já exista ali:

```php
$this->app->bind(GoogleIdTokenVerifier::class, fn () => new GoogleClientIdTokenVerifier(
    config('services.google.client_id')
));
```

**✅ Checkpoint:** `php artisan tinker` consegue resolver `app(App\Services\GoogleIdTokenVerifier::class)` e o objeto devolvido é uma instância de `GoogleClientIdTokenVerifier`.

**7.5 — o método `entrarComGoogle` no `SessaoController`.** Injete a interface pelo construtor — nunca instancie `Google\Client` direto dentro do controller, é exatamente essa instanciação direta que tornaria o passo seguinte impossível de testar sem rede:

```php
use App\Models\Usuario;
use App\Services\GoogleIdTokenVerifier;

class SessaoController extends Controller
{
    public function __construct(private readonly GoogleIdTokenVerifier $googleIdTokenVerifier) {}

    // ...mostrarLogin(), entrar()...

    public function entrarComGoogle(Request $request)
    {
        $dados = $request->validate([
            'credential' => 'required|string',
        ]);

        $payload = $this->googleIdTokenVerifier->verificar($dados['credential']);

        if ($payload === null) {
            return back()->withErrors(['email' => 'Não foi possível confirmar sua identidade com o Google.']);
        }

        $usuario = Usuario::firstOrCreate(
            ['google_id' => $payload['sub']],
            ['nome' => $payload['name'], 'email' => $payload['email'], 'password' => null]
        );

        $lembrar = $request->boolean('lembrar');

        Auth::login($usuario, $lembrar);
        $request->session()->regenerate();

        return redirect('/posts');
    }
}
```

Repare no que **não** muda em relação ao Passo 5: `Auth::login()` seguido de `$request->session()->regenerate()` é a mesma dupla de sempre, prevenindo a mesma *session fixation* de sempre. O que muda é só como se chega a um `$usuario` autenticável. `Usuario::firstOrCreate(['google_id' => ...], [...])` resolve, numa chamada, tanto "primeira vez que essa conta Google aparece" (cria a linha, com `password` nulo) quanto "essa conta já existia" (só localiza). Isso deixa um caso de fora, de propósito: alguém que já tem conta por senha tentando entrar pelo Google com o mesmo email esbarra na constraint `unique` de `email`, porque o `create` tentaria inserir uma linha nova com um email já cadastrado — um sistema real vincularia as duas contas nesse ponto; fica como exercício.

**7.6 — a rota.** Sem `middleware('auth')`, pelo mesmo motivo do `/login` original: essa rota é quem *cria* a sessão, ninguém está autenticado antes dela.

```php
Route::post('/login/google', [SessaoController::class, 'entrarComGoogle']);
```

**7.7 — teste a rejeição de verdade, com o servidor no ar.** Isso não depende de nenhuma credencial Google real: a própria tentativa de verificar a assinatura já basta para rejeitar, e passa mesmo pela rede até as chaves públicas do Google antes de decidir que o token é inválido — mesmo o caminho de erro não é um atalho local:

```bash
php artisan serve

# 1. pegue um cookie de sessão e o token CSRF da própria página de login
curl -s -c cookies.txt http://localhost:8000/login -o login.html
TOKEN=$(grep -o 'name="_token" value="[^"]*"' login.html | head -1 | sed 's/.*value="//;s/"//')

# 2. envie um credential qualquer, claramente inválido
curl -i -b cookies.txt -c cookies.txt -X POST http://localhost:8000/login/google \
  -d "_token=$TOKEN" -d "credential=isto-nao-eh-um-jwt-valido"
```

A resposta é um `302` de volta para `/login` (não um 500), e a página de login volta mostrando "Não foi possível confirmar sua identidade com o Google." — a mesma `@if ($errors->any())` que o formulário de senha já usa, sem inventar um novo mecanismo de erro só para este caminho.

**✅ Checkpoint:** `POST /login/google` com um `credential` inválido responde com um redirecionamento limpo e a mensagem de erro na tela, sem página de erro 500 — tanto para uma string qualquer quanto para um JWT com três segmentos e assinatura falsa.

**7.8 — o botão do Google em `resources/views/sessao/login.blade.php`.** O `blog-app` é renderizado pelo servidor — não existe um `fetch` de SPA aqui, então o `callback` do botão precisa terminar num **POST de formulário HTML comum**, o mesmo jeito que o resto deste tópico já usa. A estratégia: um segundo `<form>`, escondido, com um campo `credential` que o JavaScript do Google preenche; o próprio JavaScript dispara o envio em seguida:

```blade
<form id="form-google" method="POST" action="/login/google">
    @csrf
    <input type="hidden" name="credential" id="google-credential">
    <input type="hidden" name="lembrar" id="google-lembrar" value="0">
</form>

<div id="google-button" data-client-id="{{ config('services.google.client_id') }}"></div>

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

        google.accounts.id.initialize({ client_id: clientId, callback: aoReceberCredencialGoogle });
        google.accounts.id.renderButton(document.getElementById('google-button'), { theme: 'outline', size: 'large' });
    };
</script>
```

O Client ID chega à view via `config('services.google.client_id')` — o mesmo `config()` do Passo 7.2, nunca `env()` direto numa view — e vai para um atributo `data-client-id`, de onde o script lê. `aoReceberCredencialGoogle` é o `callback` que o Google chama assim que a pessoa termina o login na janela que ele mesmo abre; o objeto recebido tem um campo `.credential`, o ID token como string. Em vez de um `fetch`, essa função só copia o `credential` (e o valor atual do checkbox "lembrar-me" do formulário de senha) para o formulário escondido e chama `form.submit()` — um POST de página inteira como qualquer outro deste tutorial, com `@csrf` cuidando do resto.

**✅ Checkpoint (honesto sobre o que dá para testar sem uma conta Google real):** com um Client ID de placeholder, `GET /login` carrega sem erro nenhum, o formulário de senha continua funcionando exatamente como antes, e `window.google` existe no console do navegador depois que a página termina de carregar — mas **nenhum botão aparece na tela**. O Google Identity Services falha assim de propósito: como o Client ID não é secreto, ele não pode dar pistas específicas sobre *por que* uma origem não está autorizada, então prefere não desenhar nada a desenhar um botão quebrado — sem erro no console, sem exceção lançada. Isso já dá para confirmar sem nenhum Client ID real. O clique no botão, a tela de escolha de conta do Google e o handshake completo do OAuth só existem com um Client ID real, criado no Passo 7.1 a partir da sua própria conta Google, e um navegador de verdade — nada disso roda num terminal. Configure seu próprio Client ID e teste o botão fim a fim antes de considerar este passo concluído.

### Armadilhas comuns — referência rápida

| Sintoma | Causa | Solução |
|---|---|---|
| **419 Page Expired** | Falta `@csrf` no formulário POST | Adicione `@csrf` em todo formulário POST |
| Login sempre falha, mesmo com senha certa | Senha gravada sem `bcrypt()` | Regrave usando `bcrypt('senha')` |
| `Auth::attempt()` não encontra o usuário | Config ainda aponta para o model `User` | Aponte o provider para `App\Models\Usuario` |
| "must return an instance of Authenticatable" | Model estende `Model` em vez de `Authenticatable` | Use `class Usuario extends Authenticatable` |
| **Route [login] not defined**, erro 500 | A rota de login não tem nome | Acrescente `->name('login')` |
| **404** logo após login | O destino do `redirect()` não existe | Crie a rota de destino |
| A mensagem de erro nunca aparece | Chave da view difere da usada no controller | Use a mesma string nos dois lados |
| Lembrar-me não persiste | Faltou o 2º argumento em `Auth::attempt()` | Passe a variável e confirme a coluna |
| Usuário continua logado após "Sair" | Faltou `session()->invalidate()` | Use as três linhas do logout |
| `composer require google/apiclient` instala a `v1.1.9`, com `Google_Client` em vez de `Google\Client` | Laravel 13 fixa `guzzlehttp/guzzle` em `^8`, e o `google/apiclient` 2.x exige `~7` — o Composer resolve em silêncio para a última versão sem esse conflito | `composer require "google/apiclient:^2.9" -W`, aceitando o rebaixamento do Guzzle no projeto inteiro |
| `POST /login/google` com um `credential` malformado derruba a rota com **500** | `verifyIdToken()` só captura `ExpiredException`, `SignatureInvalidException` e `DomainException` internamente — um token sem 3 segmentos faz o `firebase/php-jwt` lançar `UnexpectedValueException`, que escapa | Capture `\UnexpectedValueException` também, ao redor da chamada a `verifyIdToken()` |

### Resumo do que você construiu

```
✅ Compreensão de cookie (navegador) vs sessão (servidor) sobre HTTP stateless
✅ Tabela usuarios com coluna de remember token, via migration
✅ Model Usuario estendendo Authenticatable, com fillable e hidden
✅ Senhas armazenadas como hash bcrypt, nunca em texto puro
✅ Formulário de login com @csrf e checkbox de lembrar-me
✅ Rota de login nomeada, que é o que faz o middleware redirecionar
✅ Auth::attempt() com o 2º argumento ativando o cookie de longa duração
✅ session()->regenerate() prevenindo session fixation
✅ Logout completo em três etapas
✅ Rotas protegidas por middleware('auth') (/logout, /posts/novo, POST /posts, DELETE /posts/{id})
✅ Client ID do Google criado no Cloud Console, sem client secret
✅ google/apiclient 2.x instalado (com o conflito de versão do Guzzle resolvido)
✅ Coluna password opcional e google_id única, via migration, sem quebrar o login por senha existente
✅ GoogleIdTokenVerifier isolando a verificação do ID token atrás de uma interface, testável sem rede
✅ entrarComGoogle tratando tanto o false quanto a exceção que verifyIdToken() pode produzir
✅ Mesmo Auth::login() + session()->regenerate() do login por senha, reaproveitados para a sessão vinda do Google
✅ Botão "Entrar com Google" desenhado via Google Identity Services, com POST de formulário comum (sem fetch)
```

### Exercícios

1. **Tela de cadastro**: rota de registro que grava um novo usuário com `bcrypt()` e já o autentica.
2. **Confirmar senha**: exija a senha novamente antes de uma ação sensível quando o acesso veio do lembrar-me.
3. **Sair de todos os dispositivos**: troque o token de lembrar-me no banco e observe os outros navegadores caírem.
4. **Limite de tentativas**: pesquise o `RateLimiter` para bloquear após 5 tentativas de login falhas.
5. **Avatar do Google**: o payload do ID token traz um campo `picture` — grave-o numa coluna nova e exiba-o em `/posts`, ao lado do nome do usuário logado.
6. **Vincular contas**: trate o caso de um email já cadastrado por senha tentando entrar via Google — em vez de deixar o `firstOrCreate` esbarrar na constraint `unique` de `email`, localize a conta existente pelo email e vincule o `google_id` a ela.

### Perguntas de fixação

1. Por que o cookie de sessão não guarda o ID do usuário diretamente?
2. O que `session()->regenerate()` previne, e por que ele entra depois de `Auth::attempt()` e não antes?
3. Por que o logout precisa de três chamadas, e não só de `Auth::logout()`?
4. Dentro de `GoogleClientIdTokenVerifier::verificar()`, por que checar `$payload === false` sozinho não basta, sem o `try/catch` ao redor da chamada a `verifyIdToken()`?
5. Depois de confirmar a identidade pelo Google, por que o código ainda chama `Auth::login()` e `session()->regenerate()`, em vez de simplesmente marcar o usuário como logado de alguma outra forma?

---

## 3. Segurança (SQL Injection, XSS, CSRF)

Laravel 13 (testado na 13.25, PHP 8.4). Se você fez a aula de Sessões, já tem o projeto pronto e pode pular o Passo 0. Hoje comparamos, lado a lado, o que aconteceria em PHP puro sem proteção — e como o Laravel evita essas falhas por padrão, sem código extra.

### Pré-requisitos

```bash
php --version       # 8.2 ou superior
composer --version  # 2.x
mysql --version     # 8.x (ou MariaDB 10+)
```

### Passo 0 — monte o projeto base (pule se já fez a aula anterior)

Se você já tem o `blog-app` com o Model `Usuario` funcionando, vá direto ao Passo 1.

**1. Crie o projeto e aponte para o MySQL:**

```bash
composer create-project laravel/laravel blog-app
cd blog-app
mysql -u andre -p1234 -e "CREATE DATABASE IF NOT EXISTS blog_app CHARACTER SET utf8mb4;"
```

Ajuste o `.env` para MySQL (mesmas linhas do Tópico 2) e rode:

```bash
php artisan migrate
```

**2. Crie o Model e a tabela de usuários** (repetindo o Tópico 2, caso ainda não exista):

```bash
php artisan make:model Usuario -m
```

```php
public function up(): void
{
    Schema::create('usuarios', function (Blueprint $table) {
        $table->id();
        $table->string('nome');
        $table->string('email')->unique();
        $table->string('password');
        $table->rememberToken();
        $table->timestamps();
    });
}
```

```php
<?php

namespace App\Models;

use Illuminate\Foundation\Auth\User as Authenticatable;

class Usuario extends Authenticatable
{
    protected $table = 'usuarios';

    protected $fillable = ['nome', 'email', 'password'];

    protected $hidden = ['password', 'remember_token'];
}
```

Em `config/auth.php`, aponte o provider para `App\Models\Usuario::class`, então:

```bash
php artisan migrate
php artisan config:clear
```

**3. Crie três usuários de teste**, para a busca do Passo 2 ter o que devolver:

```bash
php artisan tinker
```

```php
App\Models\Usuario::create(['nome' => 'Bruno Lima', 'email' => 'bruno@email.com', 'password' => bcrypt('senha456')]);
App\Models\Usuario::create(['nome' => 'Ana Souza', 'email' => 'ana@email.com', 'password' => bcrypt('senha456')]);
App\Models\Usuario::create(['nome' => 'Carla Dias', 'email' => 'carla@email.com', 'password' => bcrypt('senha456')]);
App\Models\Usuario::count();
```

`bcrypt()` transforma a senha num hash de mão única — o banco nunca vê a senha real.

```bash
php artisan serve
```

**✅ Checkpoint:** o servidor sobe sem erros e a contagem de usuários no tinker devolve 3.

### Passo 1 — reconheça o problema (SQL Injection em PHP puro)

```php
// NUNCA FAÇA ISSO — concatenação direta de entrada do usuário
$termo = $_GET['termo'];
$sql = "SELECT * FROM usuarios WHERE nome LIKE '%$termo%'";
$conn->query($sql);
```

O problema é misturar **código SQL** com **dado do usuário** na mesma string. O banco recebe um texto só e não tem como saber onde termina sua instrução e onde começa o que o visitante digitou.

**Escolhendo uma injeção que realmente funciona.** Um detalhe prático costuma ser ensinado errado: a injeção clássica `' OR 1=1 --` **não funciona** em MySQL/MariaDB quando colada assim — o comentário de duas barras exige um **espaço depois**; sem ele, a query quebra com erro de sintaxe em vez de vazar dados. Testado no MariaDB, com 3 usuários na tabela:

| Injeção | Resultado da query concatenada |
|---|---|
| `' OR 1=1 --` | erro de sintaxe (o comentário não abre) |
| `' OR 1=1 -- ` (com espaço no fim) | **3 linhas — a tabela inteira** |
| `' OR '1'='1` | **3 linhas — a tabela inteira** |

Vamos usar `' OR '1'='1`, que não depende de comentário e funciona em qualquer banco. Com ele, a query vira:

```sql
SELECT * FROM usuarios WHERE nome LIKE '%' OR '1'='1%'
```

A condição `nome LIKE '%'` é verdadeira para todas as linhas, então o `OR` derruba o filtro inteiro e a busca devolve a tabela toda.

**✅ Checkpoint:** você identifica, na query montada acima, onde o SQL "puro" termina e o dado do usuário começa.

### Passo 2 — busque com Eloquent — SQL Injection já resolvido

```bash
php artisan make:controller BuscaController
```

Rota em `routes/web.php`:

```php
use App\Http\Controllers\BuscaController;

Route::get('/usuarios/busca', [BuscaController::class, 'buscar']);
```

`app/Http/Controllers/BuscaController.php`:

```php
<?php

namespace App\Http\Controllers;

use App\Models\Usuario;
use Illuminate\Http\Request;

class BuscaController extends Controller
{
    public function buscar(Request $request)
    {
        $termo = $request->query('termo', '');

        $usuarios = Usuario::query()
            ->where('nome', 'like', "%{$termo}%")
            ->get();

        return view('busca', compact('usuarios', 'termo'));
    }
}
```

O controller devolve a view `busca` — sem criá-la, a rota responde 500 com "View [busca] not found". Crie `resources/views/busca.blade.php`:

```blade
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
</body>
</html>
```

Teste a mesma injeção do Passo 1, digitando `' OR '1'='1` no campo de busca.

**Por que não funciona: veja o SQL de verdade.** No tinker, ligue o log de queries e repita a busca:

```php
DB::listen(fn($q) => print($q->sql . PHP_EOL . json_encode($q->bindings) . PHP_EOL));
App\Models\Usuario::where('nome', 'like', "%' OR '1'='1%")->get();
```

A saída mostra duas coisas separadas:

```
select * from `usuarios` where `nome` like ?
["%' OR '1'='1%"]
```

Essa é a ideia inteira da aula em duas linhas. O SQL vai ao banco com um **espaço reservado** — a interrogação. O dado do usuário viaja **por fora**, num pacote separado. O banco recebe a instrução já compilada e depois preenche o buraco com o valor, tratando-o sempre como **texto literal**, jamais como comando. É isso que se chama *prepared statement*, e o Eloquent faz sempre, sem você pedir.

**Verificado na implementação de referência**: rodando exatamente essa busca no `blog-app` real contra o MySQL local, o resultado foi `Resultados: 0` na página, e o log do tinker mostrou literalmente `select * from `usuarios` where `nome` like ?` com o payload inteiro no array de bindings — confirmando que a injeção não escapou do parâmetro.

**✅ Checkpoint:** a injeção retorna zero resultados (busca literal), e o log mostra a query com interrogação e o dado no array de bindings.

### Passo 3 — quando precisar de SQL cru, use bindings — nunca concatenação

```php
use Illuminate\Support\Facades\DB;

// Seguro: a interrogação é preenchida via binding, nunca concatenada
$usuarios = DB::select('SELECT * FROM usuarios WHERE nome LIKE ?', ["%{$termo}%"]);

// Continua vulnerável mesmo dentro do Laravel — NUNCA faça isso:
// DB::select("SELECT * FROM usuarios WHERE nome LIKE '%$termo%'");
```

O Query Builder e o Eloquent protegem automaticamente. Já `DB::select` com SQL cru só é seguro se você **sempre** usar a interrogação mais o array de bindings. Confirme a diferença no tinker, com o mesmo payload nos dois:

```php
$termo = "' OR '1'='1";
count(DB::select('SELECT * FROM usuarios WHERE nome LIKE ?', ["%{$termo}%"]));
count(DB::select("SELECT * FROM usuarios WHERE nome LIKE '%$termo%'"));
```

A primeira devolve 0. A segunda devolve **todas** as linhas da tabela.

**✅ Checkpoint:** a versão com binding devolve 0 e a concatenada devolve a tabela inteira — no mesmo framework, com o mesmo dado.

### Passo 4 — valide toda entrada antes de usar

A validação é anterior a tudo: rejeita o pedido antes que qualquer dado chegue perto do banco. Acrescente o import do Hash no topo do `BuscaController`:

```php
use Illuminate\Support\Facades\Hash;
```

E este método dentro da classe:

```php
public function cadastrar(Request $request)
{
    $dados = $request->validate([
        'nome' => 'required|string|max:100',
        'email' => 'required|email|max:100|unique:usuarios,email',
        'password' => 'required|string|min:8',
    ]);

    $dados['password'] = Hash::make($dados['password']);

    Usuario::create($dados);

    return redirect('/usuarios/busca')->with('sucesso', 'Cadastrado!');
}
```

E registre a rota:

```php
Route::post('/usuarios', [BuscaController::class, 'cadastrar']);
```

Cada regra tem um papel: `required` rejeita campo ausente ou vazio. `email` valida o formato antes de o valor existir no banco. `max:100` limita o tamanho. `unique:usuarios,email` consulta a tabela e recusa duplicatas **sem** tentar inserir — a mesma garantia do índice único do banco, mas com mensagem amigável em vez de erro 500. `min:8` estabelece um piso para a senha. `Hash::make()` gera o mesmo tipo de hash bcrypt do `bcrypt()` do Tópico 2.

**✅ Checkpoint:** enviar um email já cadastrado volta ao formulário com erro de validação, e o banco continua com apenas um registro daquele email.

### Passo 5 — proteja contra XSS — Blade escapa por padrão

```bash
php artisan make:model Comentario -m
```

```php
public function up(): void
{
    Schema::create('comentarios', function (Blueprint $table) {
        $table->id();
        $table->text('texto');
        $table->boolean('aprovado')->default(false);
        $table->timestamps();
    });
}
```

A coluna `aprovado` já entra aqui de propósito, não só depois: ela existe para o `$fillable` do model, logo abaixo, ficar **de fora** dela — a prova viva de que mass assignment protege até colunas que já existem na tabela, não só as que "ainda não foram pensadas".

```bash
php artisan migrate
```

**⚠️ O Model precisa de fillable, senão nada é salvo.** `make:model` cria a classe **vazia**. Tentar `Comentario::create()` assim lança `MassAssignmentException` com "Add [texto] to fillable property" — nenhum comentário chega ao banco. Isso não é um obstáculo chato: é a proteção contra *mass assignment* funcionando. O Eloquent se recusa a preencher colunas em massa a partir de um array vindo do usuário enquanto você não declarar, explicitamente, quais colunas são seguras.

```php
<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Comentario extends Model
{
    protected $table = 'comentarios';

    protected $fillable = ['texto'];
}
```

Repare que a validação do Passo 4 e o `$fillable` resolvem problemas **diferentes**: a validação diz "estes valores são aceitáveis"; o `$fillable` diz "estas colunas podem ser preenchidas de uma vez". Sem o segundo, um formulário com um campo extra chamado, digamos, `admin`, poderia gravar numa coluna que você nunca pretendeu expor.

```bash
php artisan make:controller ComentarioController
```

```php
<?php

namespace App\Http\Controllers;

use App\Models\Comentario;
use Illuminate\Http\Request;

class ComentarioController extends Controller
{
    public function index()
    {
        return view('comentarios', ['comentarios' => Comentario::latest()->get()]);
    }

    public function criar(Request $request)
    {
        $dados = $request->validate(['texto' => 'required|string|min:3|max:500']);
        Comentario::create($dados);
        return back()->with('sucesso', 'Comentário enviado!');
    }
}
```

```php
use App\Http\Controllers\ComentarioController;

Route::get('/comentarios', [ComentarioController::class, 'index']);
Route::post('/comentarios', [ComentarioController::class, 'criar']);
```

`resources/views/comentarios.blade.php`:

```blade
<form method="POST" action="/comentarios">
    @csrf
    <textarea name="texto" rows="3"></textarea>
    <button type="submit">Enviar</button>
</form>

@foreach($comentarios as $comentario)
    <div class="comentario">{{ $comentario->texto }}</div>
@endforeach
```

Tente enviar isto como comentário: `<script>alert('XSS!');</script>`.

**Detalhe que muda tudo: escape na SAÍDA, não na entrada.** Vá ver o que ficou gravado no banco, com `SELECT texto FROM comentarios`. O texto está lá **cru**, com as tags intactas — o Blade não alterou nada na hora de salvar. O que ele faz é escapar na hora de **exibir**: as chaves duplas convertem os caracteres perigosos em entidades HTML, e o navegador recebe `&lt;script&gt;alert(&#039;XSS!&#039;);&lt;/script&gt;`, que ele desenha como texto, sem executar. Guardar o dado original é intencional: o mesmo texto pode ser exibido depois em HTML, num e-mail, num PDF ou numa API — cada destino tem regras de escape diferentes. Quem escapa é quem exibe.

**Verificado na implementação de referência**: postando literalmente `<script>alert('XSS!');</script>` via `curl` contra o `blog-app` real, a consulta direta ao MySQL mostrou o texto cru salvo (`<script>alert('XSS!');</script>`), enquanto a página HTML devolvida pelo mesmo servidor mostrou `&lt;script&gt;alert(&#039;XSS!&#039;);&lt;/script&gt;` — a prova de que o escape acontece exatamente na saída.

**✅ Checkpoint:** o comentário com script aparece como texto na tela, sem executar nenhum alerta — e no banco o texto está gravado com as tags originais.

### Passo 6 — entenda a saída insegura, e quando (não) usá-la

```blade
{{-- Seguro (padrão): escapa HTML --}}
{{ $comentario->texto }}

{{-- Inseguro: renderiza HTML sem escapar --}}
{!! $comentario->texto !!}
```

Não fique só na explicação — comprove. Crie uma view de demonstração `resources/views/comentarios-inseguro.blade.php`:

```blade
@foreach($comentarios as $comentario)
    <div class="inseguro">{!! $comentario->texto !!}</div>
@endforeach
```

E uma rota temporária:

```php
Route::get('/comentarios-inseguro', function () {
    return view('comentarios-inseguro', ['comentarios' => App\Models\Comentario::latest()->get()]);
});
```

Abra as duas rotas com o mesmo comentário malicioso já gravado e compare o HTML recebido:

| Rota | HTML entregue ao navegador | Efeito |
|---|---|---|
| `/comentarios` | `&lt;script&gt;alert(...)&lt;/script&gt;` | aparece como texto |
| `/comentarios-inseguro` | `<script>alert(...)</script>` | **executa o alerta** |

A sintaxe não escapada só deve ser usada com conteúdo que você mesmo controla e já sanitizou (HTML gerado por um editor rico confiável, por exemplo) — nunca com texto vindo direto do usuário. Apague a rota de demonstração depois do teste — na implementação de referência ela **não** está presente, exatamente por esse motivo (veja a nota no README do `blog-app`).

**✅ Checkpoint:** a rota insegura executa o alerta e a segura não, com exatamente o mesmo dado no banco.

### Passo 7 — CSRF automático — reconheça a proteção que já existe

Toda rota POST, PUT, PATCH e DELETE do Laravel já é protegida por um middleware de verificação de token. Remova o `@csrf` do formulário do Passo 5 e tente enviar:

```
419 | PAGE EXPIRED
```

O Laravel rejeita a requisição antes de chegar ao Controller. Recoloque o `@csrf`.

O ataque que isso previne é o *Cross-Site Request Forgery*: um site malicioso que você visita logado monta um formulário apontando para o seu servidor e o envia em segundo plano. O navegador anexa seus cookies automaticamente, e sem o token o servidor não teria como distinguir esse envio de um seu. O token existe só na sessão e no seu HTML — o site do atacante não tem como adivinhá-lo.

**Nota sobre o nome da classe:** você vai encontrar tutoriais citando `VerifyCsrfToken` ou `ValidateCsrfToken`. No Laravel 13 as duas estão marcadas como **deprecadas**; a classe atual chama-se `PreventRequestForgery`. O comportamento é o mesmo, mas se for referenciá-la em código, use o nome novo.

**Verificado na implementação de referência**: um `curl -X POST /comentarios` sem o campo `_token` contra o servidor real devolveu, de fato, status `419` — e o mesmo POST com o token correto (extraído da própria página) devolveu `302` (redirecionamento de sucesso).

**✅ Checkpoint:** sem o token, o formulário retorna 419 em vez de salvar o comentário.

### Resumo do que você construiu

```
✅ Projeto base do zero: Laravel 13 + MySQL, Model Usuario autenticável
✅ Comparação direta: concatenação SQL (vulnerável) vs Eloquent/bindings (seguro)
✅ Payload de injeção que de fato funciona no MySQL, e por que o clássico falha
✅ Prova via log de queries: SQL com interrogação e dado separado nos bindings
✅ Busca segura com Eloquent e com SQL cru mais bindings
✅ Validação de entrada como primeira camada de defesa
✅ Hash de senha com Hash::make()
✅ Mass assignment: por que o fillable é obrigatório, e como ele difere da validação
✅ Proteção automática contra XSS com as chaves duplas do Blade
✅ Demonstração lado a lado de que a saída não escapada reabre a falha
✅ Escape acontece na saída, não na entrada — e por que isso é desejável
✅ CSRF automático, e o nome atual do middleware no Laravel 13
```

### Exercícios

1. **Rate limiting**: adicione o middleware `throttle` na rota de comentários para limitar tentativas de spam.
2. **Sanitização de exibição**: pesquise a função de remover tags do PHP como alternativa quando parte do HTML precisa ser preservada, e discuta por que ela é pior que escapar.
3. **Colunas sensíveis**: a tabela de comentários já tem a coluna `aprovado` (Passo 5), fora do `$fillable`. Comprove que um POST malicioso tentando marcá-la — `curl -d "texto=oi" -d "aprovado=1" ...` — é ignorado, e que a linha criada continua com `aprovado = 0` no banco.
4. **Log de tentativas suspeitas**: registre em log toda vez que uma validação falhar na rota de comentários.

### Perguntas de fixação

1. Por que `' OR 1=1 --` sem espaço no final não funciona no MySQL/MariaDB?
2. Se o Blade já escapa `{{ }}` por padrão, por que ainda existe `{!! !!}` na linguagem?
3. Qual é a diferença prática entre o que `$fillable` protege e o que `$request->validate()` protege?

---

## 4. Padrão DAO

**Objetivo:** isolar o acesso a dados dos posts atrás de uma interface, para que o controller nunca fale diretamente com o Eloquent.

Laravel 13 (testado na 13.25, PHP 8.4). Se você já tem o `blog-app` das aulas anteriores, o Passo 0 só acrescenta o que falta.

### Pré-requisitos

```bash
php --version       # 8.2 ou superior
composer --version  # 2.x
mysql --version     # 8.x (ou MariaDB 10+)
```

**Atenção à ordem do calendário:** este tópico usa um Model `Post` e um `PostController`, mas o tópico de Implementação de Controller (para o `task-manager`) vem depois neste material — é normal ainda não tê-los aqui. O Passo 0 cria o necessário.

### Passo 0 — monte o projeto base

Faça só as partes que faltam no seu projeto.

**1. Projeto Laravel apontando para o MySQL** (pule se já tem o `blog-app`):

```bash
composer create-project laravel/laravel blog-app
cd blog-app
mysql -u andre -p1234 -e "CREATE DATABASE IF NOT EXISTS blog_app CHARACTER SET utf8mb4;"
```

Ajuste o `.env` (mesmas linhas dos tópicos anteriores) e rode `php artisan migrate`.

**2. Model Usuario** (pule se já fez o Tópico 2 ou 3) — o Post vai referenciá-lo, então precisa existir primeiro. Repita a migration e o model do Tópico 2.

**3. Model Post**, o assunto de hoje:

```bash
php artisan make:model Post -m
```

```php
public function up(): void
{
    Schema::create('posts', function (Blueprint $table) {
        $table->id();
        $table->string('titulo');
        $table->text('conteudo');
        $table->foreignId('usuario_id')->constrained('usuarios')->cascadeOnDelete();
        $table->timestamps();
    });
}
```

`foreignId(...)->constrained('usuarios')` cria a coluna de chave estrangeira **e** a restrição no banco. `cascadeOnDelete()` diz que apagar um usuário apaga os posts dele — sem isso, o banco recusaria a exclusão.

```php
<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;

class Post extends Model
{
    protected $table = 'posts';

    protected $fillable = ['titulo', 'conteudo', 'usuario_id'];

    public function usuario(): BelongsTo
    {
        return $this->belongsTo(Usuario::class, 'usuario_id');
    }
}
```

O `$fillable` é obrigatório pelo mesmo motivo visto no Tópico 3. O método de relacionamento permite escrever `$post->usuario->nome` e, no Passo 3, o carregamento antecipado.

**4. Aplique e crie dados de teste:**

```bash
php artisan migrate
php artisan tinker
```

```php
$u = App\Models\Usuario::firstOrCreate(['email' => 'bruno@email.com'], ['nome' => 'Bruno Lima', 'password' => bcrypt('senha456')]);
App\Models\Post::create(['titulo' => 'Primeiro post', 'conteudo' => 'Conteudo', 'usuario_id' => $u->id]);
App\Models\Post::create(['titulo' => 'Segundo post', 'conteudo' => 'Conteudo', 'usuario_id' => $u->id]);
App\Models\Post::count();
```

**✅ Checkpoint:** a contagem de posts no tinker devolve 2.

### Passo 1 — entenda o problema que o DAO resolve

Um controller típico chama o Eloquent diretamente:

```php
$posts = Post::where('publicado', true)->latest()->get();
```

Isso **acopla** o controller ao Eloquent. Trocar de ORM, adicionar cache, ou testar sem banco exigiria mexer em cada controller que faz esse tipo de chamada. O DAO (*Data Access Object*) isola esse acesso atrás de uma interface: o controller passa a pedir "me dê todos os posts" sem saber de onde eles vêm. O que se ganha, e não é só arrumação:

- **Inversão de dependência**: o controller depende de uma abstração (a interface), não de uma implementação concreta.
- **Testabilidade**: dá para trocar a implementação por uma falsa, sem banco.
- **Um lugar só para mudar**: a consulta vive no DAO, não espalhada por vários controllers.

**✅ Checkpoint:** você consegue apontar, num controller seu, uma linha que fala diretamente com o Eloquent.

### Passo 2 — crie a interface PostDAOInterface

Crie a pasta `app/DAO` e, dentro, `PostDAOInterface.php`:

```php
<?php
namespace App\DAO;

use App\Models\Post;
use Illuminate\Support\Collection;

interface PostDAOInterface
{
    public function todos(): Collection;
    public function doUsuario(int $usuarioId): Collection;
    public function criar(array $dados): Post;
}
```

**Por que `Support\Collection` e não `Eloquent\Collection`, por enquanto.** Este detalhe decide se o Passo 6 vai funcionar. O Laravel tem duas classes de coleção: `Illuminate\Support\Collection`, genérica, e `Illuminate\Database\Eloquent\Collection`, que **estende** a primeira e é o que as consultas ao banco devolvem. Se a interface exigir a versão do Eloquent, qualquer implementação que **não** venha do banco fica impedida de cumprir o contrato — `collect()`, por exemplo, devolve a versão genérica, e o PHP lança um `TypeError` na hora do retorno. A regra geral: **a interface deve declarar o tipo mais geral que todas as implementações conseguem honrar.** Como a coleção do Eloquent é uma coleção genérica, declarar a genérica aceita as duas. Guarde essa escolha: o Tópico 5 volta a este mesmo arquivo e a estreita de propósito, quando fica claro que a aplicação real só vai ter implementações vindas do banco.

**✅ Checkpoint:** `php -l app/DAO/PostDAOInterface.php` não acusa erro de sintaxe.

### Passo 3 — implemente PostDAO com Eloquent

`app/DAO/PostDAO.php`:

```php
<?php
namespace App\DAO;

use App\Models\Post;
use Illuminate\Database\Eloquent\Collection;

class PostDAO implements PostDAOInterface
{
    public function todos(): Collection
    {
        return Post::with('usuario')->latest()->get();
    }

    public function doUsuario(int $usuarioId): Collection
    {
        return Post::where('usuario_id', $usuarioId)->latest()->get();
    }

    public function criar(array $dados): Post
    {
        return Post::create($dados);
    }
}
```

Aqui o retorno declarado é a coleção **do Eloquent**, mais específica que a da interface — permitido, porque o PHP aceita que uma implementação estreite o tipo de retorno (covariância). O contrário — a interface pedir a específica e a implementação devolver a genérica — é o que quebra. `Post::with('usuario')` faz *eager loading*: traz os autores numa segunda consulta em vez de uma consulta por post. Sem ele, listar 50 posts e exibir o nome do autor dispara 51 consultas — o problema conhecido como N+1.

**✅ Checkpoint:** `php -l app/DAO/PostDAO.php` não acusa erro de sintaxe.

### Passo 4 — registre o binding no Service Container

Em `app/Providers/AppServiceProvider.php`. **Os dois imports no topo não são opcionais:**

```php
use App\DAO\PostDAO;
use App\DAO\PostDAOInterface;
```

```php
public function register(): void
{
    $this->app->bind(PostDAOInterface::class, PostDAO::class);
}
```

**A armadilha silenciosa dos imports.** Se você escrever o `bind` sem os `use`, **nada acusa erro na hora**. O arquivo está no namespace `App\Providers`, então os nomes curtos resolvem para `App\Providers\PostDAOInterface` e `App\Providers\PostDAO` — classes que não existem. O binding é registrado para um nome fantasma, e o erro só aparece muito depois, ao resolver a interface de verdade:

```
BindingResolutionException: Target [App\DAO\PostDAOInterface] is not instantiable
```

A mensagem não menciona imports, e é fácil perder tempo procurando no lugar errado. O `bind` ensina o Laravel: "sempre que alguém pedir a interface, entregue uma instância desta classe". Confirme no tinker:

```php
get_class(app(App\DAO\PostDAOInterface::class));
```

**✅ Checkpoint:** o comando acima imprime `App\DAO\PostDAO`.

### Passo 5 — injete a interface no controller (não a classe concreta)

`app/Http/Controllers/PostController.php`:

```php
<?php

namespace App\Http\Controllers;

use App\DAO\PostDAOInterface;

class PostController extends Controller
{
    public function __construct(private PostDAOInterface $dao) {}

    public function index()
    {
        return view('posts.index', ['posts' => $this->dao->todos()]);
    }
}
```

`resources/views/posts/index.blade.php`:

```blade
<!DOCTYPE html>
<html lang="pt-BR">
<head><meta charset="UTF-8"><title>Posts</title></head>
<body>
    <h1>Posts</h1>
    <ul>
        @foreach($posts as $post)
            <li>{{ $post->titulo }} — por {{ $post->usuario->nome ?? 'sem autor' }}</li>
        @endforeach
    </ul>
</body>
</html>
```

```php
use App\Http\Controllers\PostController;

Route::get('/posts', [PostController::class, 'index']);
```

**Injeção de dependência via construtor**: o Laravel lê o tipo do parâmetro, consulta o binding do Passo 4 e constrói o objeto sozinho — o controller nunca escreve `new PostDAO`. O `private` na assinatura é *promoção de propriedade* do PHP 8: declara e atribui a propriedade numa linha só.

**✅ Checkpoint:** a rota de posts lista os dois posts com o nome do autor, agora passando pelo DAO.

### Passo 6 — crie uma implementação alternativa para testes (sem banco)

`app/DAO/PostDAOEmMemoria.php`:

```php
<?php
namespace App\DAO;

use App\Models\Post;
use Illuminate\Support\Collection;

class PostDAOEmMemoria implements PostDAOInterface
{
    private array $posts = [];
    private int $proximoId = 1;

    public function todos(): Collection
    {
        return collect($this->posts)->values();
    }

    public function doUsuario(int $usuarioId): Collection
    {
        return collect($this->posts)->where('usuario_id', $usuarioId)->values();
    }

    public function criar(array $dados): Post
    {
        $post = new Post($dados);
        $post->id = $this->proximoId++;
        $this->posts[$post->id] = $post;
        return $post;
    }
}
```

Um cuidado: o array é indexado **pelo id**, não por posição — se `criar()` empilhasse com colchetes vazios, as chaves seriam 0, 1, 2... e uma futura busca por id devolveria o post errado ou nada. O retorno é a coleção genérica, exatamente o tipo que a interface declarou no Passo 2 — se a interface exigisse a coleção do Eloquent, este método explodiria com `TypeError`.

**Comprove a troca, sem tocar no controller.** No tinker:

```php
$fake = new App\DAO\PostDAOEmMemoria();
$fake->criar(['titulo' => 'Post de teste', 'conteudo' => 'sem banco', 'usuario_id' => 1]);

app()->instance(App\DAO\PostDAOInterface::class, $fake);

$c = app(App\Http\Controllers\PostController::class);
$c->index()->getData()['posts'];
```

`app()->instance(...)` substitui o que o container entrega para aquela interface. O `PostController` continua **exatamente** o mesmo arquivo, sem uma linha alterada, e agora serve dados que nunca passaram pelo banco. Numa suíte de testes é isso que permite exercitar a lógica do controller sem migrations, sem seed e sem I/O de disco — testes que rodam em milissegundos.

**Nota sobre o que fica no projeto depois deste passo:** `PostDAOEmMemoria` é um exercício deste tópico — construa-o e comprove a troca localmente, seguindo os comandos acima. Ele **não** está commitado em `app/DAO/` na implementação de referência: o Tópico 5, ao retomar este mesmo `PostDAOInterface`, estreita o tipo de retorno de volta para `Illuminate\Database\Eloquent\Collection` (veja o Passo 3 de lá), porque a partir dali a aplicação só passa a ter implementações vindas do banco. Se você mantiver `PostDAOEmMemoria` no projeto depois de fazer essa troca, ele para de compilar contra a interface — é o próprio PHP aplicando a regra de covariância que o Passo 2 explicou, na direção contrária.

**✅ Checkpoint:** o mesmo `PostController` devolve os posts do banco antes da troca, e o post em memória depois — sem nenhuma alteração no controller.

### Resumo do que você construiu

```
✅ Projeto base do zero: Laravel 13 + MySQL, Models Usuario e Post relacionados
✅ Interface PostDAOInterface isolando o acesso a dados do Post
✅ Escolha consciente do tipo de retorno: o mais geral que todos conseguem honrar
✅ PostDAO implementando a interface com Eloquent, com eager loading contra N+1
✅ Binding registrado no Service Container (interface para implementação)
✅ Os imports no provider, e o erro silencioso que a falta deles provoca
✅ PostController injetando a interface via construtor, nunca a classe concreta
✅ Implementação alternativa em memória, comprovadamente trocável sem alterar o controller
```

### Exercícios

1. **ComentarioDAO**: crie a mesma estrutura para a entidade Comentario da aula de Segurança.
2. **Cache**: crie uma classe que decore o `PostDAO`, guardando o resultado de `todos()` em cache por 60 segundos.
3. **Troca de binding em teste**: pesquise como usar `bind` dentro de um teste Pest ou PHPUnit.
4. **Quando NÃO usar DAO**: escreva um parágrafo argumentando em que situações essa camada extra atrapalha mais do que ajuda.

### Perguntas de fixação

1. Por que a interface `PostDAOInterface` declara `Illuminate\Support\Collection`, mas `PostDAO` implementa devolvendo `Illuminate\Database\Eloquent\Collection`?
2. O que exatamente `$this->app->bind(...)` registra, e o que acontece se os imports da interface e da classe faltarem no provider?
3. Por que trocar a implementação do DAO por uma versão em memória não exige nenhuma mudança no `PostController`?

---

## 5. Projeto Integrador

**Objetivo:** consolidar Request/validação, Sessões/Auth e DAO numa única feature completa do `blog-app` — publicação de posts autenticada.

Laravel 13 (testado na 13.26.1, PHP 8.4, MariaDB 12.3).

### Pré-requisitos

```bash
php --version       # 8.3 ou superior (o Laravel 13 exige ^8.3)
composer --version  # 2.x
mariadb --version   # MariaDB 10+ (ou MySQL 8.x)
```

Se você já tem o `blog-app` das aulas anteriores com login funcionando, pule direto para o Passo 1. Se não tem — ou não tem certeza — o Passo 0 monta a base inteira do zero.

### Passo 0 — monte a base (projeto, banco, usuário e login)

**0.1 — Banco de dados no ar**

```bash
sudo systemctl enable --now mariadb
systemctl is-active mariadb        # deve imprimir: active
```

Crie o banco e o usuário — é o único comando com `sudo`:

```bash
sudo mariadb -e "
CREATE DATABASE IF NOT EXISTS blog_app CHARACTER SET utf8mb4;
CREATE USER IF NOT EXISTS 'andre'@'localhost' IDENTIFIED BY '1234';
GRANT ALL PRIVILEGES ON blog_app.* TO 'andre'@'localhost';
FLUSH PRIVILEGES;"
```

**0.2 — Projeto Laravel apontando para o MySQL**

```bash
composer create-project laravel/laravel blog-app
cd blog-app
rm database/database.sqlite
```

Ajuste o `.env`:

```
DB_CONNECTION=mysql
DB_HOST=127.0.0.1
DB_PORT=3306
DB_DATABASE=blog_app
DB_USERNAME=andre
DB_PASSWORD=1234
```

Ainda no `.env`, acrescente esta linha no fim do arquivo:

```
AUTH_MODEL=App\Models\Usuario
```

Ela é fácil de esquecer e cara de descobrir. O Laravel autentica, por padrão, o model `App\Models\User`. Como o nosso usuário é a classe `Usuario`, é preciso avisar o sistema de autenticação — senão o login nunca encontra ninguém. Essa variável funciona porque `config/auth.php`, na instalação padrão do Laravel 13, já lê `env('AUTH_MODEL', User::class)` — então basta a variável de ambiente, sem editar o arquivo de configuração à mão (a abordagem que os Tópicos 2 e 3 usaram, editando `config/auth.php` diretamente, continua válida; esta é um atalho equivalente).

```bash
php artisan migrate
```

**0.3 — Model Usuario**

```bash
php artisan make:model Usuario -m
```

```php
public function up(): void
{
    Schema::create('usuarios', function (Blueprint $table) {
        $table->id();
        $table->string('nome');
        $table->string('email')->unique();
        $table->string('password');
        $table->rememberToken();
        $table->timestamps();
    });
}
```

```php
<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Relations\HasMany;
use Illuminate\Foundation\Auth\User as Authenticatable;

class Usuario extends Authenticatable
{
    protected $table = 'usuarios';

    protected $fillable = ['nome', 'email', 'password'];

    protected $hidden = ['password', 'remember_token'];

    public function posts(): HasMany
    {
        return $this->hasMany(Post::class, 'usuario_id');
    }
}
```

O relacionamento com posts é o do Passo 4 — já deixamos pronto.

**0.4 — Login e logout**

```bash
php artisan make:controller SessaoController
```

```php
<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;

class SessaoController extends Controller
{
    public function mostrarLogin()
    {
        return view('sessao.login');
    }

    public function entrar(Request $request)
    {
        $credenciais = $request->validate([
            'email' => 'required|email',
            'password' => 'required|string',
        ]);

        if (! Auth::attempt($credenciais)) {
            return back()->withErrors(['email' => 'Credenciais inválidas.']);
        }

        $request->session()->regenerate();

        return redirect('/posts');
    }

    public function sair(Request $request)
    {
        Auth::logout();
        $request->session()->invalidate();
        $request->session()->regenerateToken();

        return redirect('/login');
    }
}
```

`resources/views/sessao/login.blade.php`:

```blade
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
        <button type="submit">Entrar</button>
    </form>
</body>
</html>
```

`routes/web.php`:

```php
<?php

use App\Http\Controllers\SessaoController;
use Illuminate\Support\Facades\Route;

Route::get('/', fn () => redirect('/posts'));

Route::get('/login', [SessaoController::class, 'mostrarLogin'])->name('login');
Route::post('/login', [SessaoController::class, 'entrar']);
Route::post('/logout', [SessaoController::class, 'sair'])->middleware('auth');
```

O `->name('login')` não é decoração: é para essa rota que o middleware de autenticação redireciona quem não está logado. Sem o nome, o redirecionamento do Passo 5 quebra.

**0.5 — Dois usuários de teste**

Precisamos de **dois**: o Passo 7 verifica que um usuário não consegue apagar o post do outro.

```php
App\Models\Usuario::firstOrCreate(['email' => 'bruno@email.com'], ['nome' => 'Bruno Lima', 'password' => bcrypt('senha456')]);
App\Models\Usuario::firstOrCreate(['email' => 'ana@email.com'], ['nome' => 'Ana Souza', 'password' => bcrypt('senha123')]);
App\Models\Usuario::count();
```

```bash
php artisan serve
```

**✅ Checkpoint:** em `http://127.0.0.1:8000/login`, entrar com bruno@email.com / senha456 funciona (ainda vai dar erro em `/posts`, que só existe a partir do Passo 5).

### Passo 1 — planeje a feature: o que já sabemos, o que falta

| Já sabemos | Vamos aplicar hoje |
|---|---|
| Formulários + validação (`$request->validate`) | Formulário de novo post |
| Sessão/Auth (`Auth::attempt`, `middleware('auth')`) | Só usuário logado publica |
| DAO (interface + implementação) | `PostDAO` isolando o acesso a dados |

**✅ Checkpoint:** você consegue explicar em uma frase o que cada aula anterior contribui para hoje.

### Passo 2 — crie o Model e a migration de Post

```bash
php artisan make:model Post -m
```

```php
public function up(): void
{
    Schema::create('posts', function (Blueprint $table) {
        $table->id();
        $table->string('titulo');
        $table->text('conteudo');
        $table->foreignId('usuario_id')->constrained('usuarios');
        $table->timestamps();
    });
}
```

`app/Models/Post.php`, com **duas** coisas — o relacionamento e a lista de campos liberados:

```php
<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;

class Post extends Model
{
    protected $fillable = ['titulo', 'conteudo', 'usuario_id'];

    public function usuario(): BelongsTo
    {
        return $this->belongsTo(Usuario::class, 'usuario_id');
    }
}
```

O **`$fillable` não é opcional.** O DAO do Passo 3 vai chamar `Post::create($dados)`, e sem essa lista o Eloquent recusa a gravação inteira com `MassAssignmentException`.

```bash
php artisan migrate
```

**✅ Checkpoint:** a tabela `posts` foi criada com a chave estrangeira para `usuarios`.

### Passo 3 — crie a interface e a implementação do DAO

`app/DAO/PostDAOInterface.php`:

```php
<?php

namespace App\DAO;

use App\Models\Post;
use Illuminate\Database\Eloquent\Collection;

interface PostDAOInterface
{
    public function todos(): Collection;
    public function doUsuario(int $usuarioId): Collection;
    public function criar(array $dados): Post;
}
```

`app/DAO/PostDAO.php`:

```php
<?php

namespace App\DAO;

use App\Models\Post;
use Illuminate\Database\Eloquent\Collection;

class PostDAO implements PostDAOInterface
{
    public function todos(): Collection
    {
        return Post::with('usuario')->latest()->get();
    }

    public function doUsuario(int $usuarioId): Collection
    {
        return Post::where('usuario_id', $usuarioId)->latest()->get();
    }

    public function criar(array $dados): Post
    {
        return Post::create($dados);
    }
}
```

No Tópico 4 usamos a coleção genérica na interface, para permitir uma implementação em memória. Aqui a interface pede a coleção do Eloquent, e está certo: todas as implementações desta aula vêm do banco. Escolha o tipo pelo conjunto de implementações que você pretende ter.

Registre o binding em `app/Providers/AppServiceProvider.php`:

```php
use App\DAO\PostDAO;
use App\DAO\PostDAOInterface;
```

```php
public function register(): void
{
    $this->app->bind(PostDAOInterface::class, PostDAO::class);
}
```

**✅ Checkpoint:** as classes compilam sem erro (`php -l app/DAO/PostDAO.php`).

### Passo 4 — confirme os relacionamentos

```php
php artisan tinker

$u = App\Models\Usuario::first();
App\Models\Post::create(['titulo' => 'Primeiro post', 'conteudo' => 'Conteudo de teste do post', 'usuario_id' => $u->id]);
App\Models\Post::first()->usuario->nome;
$u->posts()->count();
```

**✅ Checkpoint:** a primeira consulta devolve "Bruno Lima" e a segunda devolve 1.

### Passo 5 — crie o formulário de novo post (protegido por autenticação)

```bash
php artisan make:controller PostController
```

```php
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
}
```

O `use App\Models\Post;` ainda não é usado aqui, mas o Passo 7 depende dele — sem esse import, o nome curto `Post` resolveria para `App\Http\Controllers\Post`, e o erro só apareceria na hora de excluir.

```php
use App\Http\Controllers\PostController;

Route::get('/posts', [PostController::class, 'index']);
Route::get('/posts/novo', [PostController::class, 'mostrarFormulario'])->middleware('auth');
Route::post('/posts', [PostController::class, 'criar'])->middleware('auth');
```

Repare como as três aulas anteriores se encontram num único controller: `middleware('auth')` (Sessões), `$request->validate()` (Formulários), e a chamada ao DAO (Padrão DAO). `auth()->id()` pega o ID do usuário logado automaticamente, sem precisar de um campo oculto no formulário — e sem confiar em dado vindo do cliente.

**✅ Checkpoint:** deslogado, `/posts/novo` responde 302 para `/login`; logado, responde 200 com o formulário.

### Passo 6 — crie as views

`resources/views/posts/novo.blade.php`:

```blade
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
```

O bloco de erros e o `old(...)` fazem a validação do Passo 5 aparecer para quem usa. Sem eles, um envio inválido volta para o formulário em branco, sem explicar nada — o usuário só vê o texto sumir.

`resources/views/posts/index.blade.php`:

```blade
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
        </article>
    @endforeach
</body>
</html>
```

O botão "Sair" chama exatamente a rota `POST /logout` do Tópico 2 (`SessaoController::sair`) — este é o primeiro lugar do projeto onde ele fica acessível pela interface, e não só testável por `curl`. `?? 'sem autor'` cobre o caso, ainda incomum aqui mas real, de um post cujo autor foi apagado: a chave estrangeira `usuario_id` usa `cascadeOnDelete()` (Tópico 4), então normalmente o post some junto — mas um post criado por um script ou seed sem usuário associado corretamente não quebraria a página.

**✅ Checkpoint:** publicar um post logado como Bruno o mostra em `/posts` com "Por: Bruno Lima"; clicar em "Sair" desloga e volta ao formulário de login.

### Passo 7 — restrinja a exclusão ao autor do post

```php
public function excluir(Request $request, int $id)
{
    $post = Post::findOrFail($id);

    if ($post->usuario_id !== auth()->id()) {
        abort(403, 'Você não pode excluir este post');
    }

    $post->delete();
    return redirect('/posts');
}
```

`abort(403, ...)` interrompe a requisição imediatamente com "Forbidden", mesmo que a rota exista e o usuário esteja autenticado — ele só não é o **dono** do recurso. O método sozinho não faz nada: **a rota também precisa existir.**

```php
Route::delete('/posts/{id}', [PostController::class, 'excluir'])->middleware('auth');
```

E o botão, dentro do `@foreach` de `posts/index.blade.php`:

```blade
@auth
    <form method="POST" action="/posts/{{ $post->id }}">
        @csrf
        @method('DELETE')
        <button type="submit">Excluir</button>
    </form>
@endauth
```

**Por que o formulário, e não um curl direto.** Formulários HTML só sabem enviar GET e POST. `@method('DELETE')` adiciona um campo escondido que o Laravel lê para tratar a requisição como DELETE. E o `@csrf` é obrigatório: todas as rotas de `web.php` passam pela proteção contra CSRF. Um `curl -X DELETE` sem token **não** chega no seu `if` — ele é barrado antes, com **419 Page Expired**:

```bash
curl -X DELETE http://127.0.0.1:8000/posts/2     # 419, não 403
```

O 419 confunde porque parece problema de autorização, e não é: é o token que faltou.

**Verificado na implementação de referência**: contra o `blog-app` real, `curl -X DELETE` sem token devolveu `419`; logado como Ana tentando excluir um post do Bruno (com token válido, mas sem ser o dono) devolveu `403`; e Bruno excluindo o próprio post devolveu `302` (sucesso) — as três respostas confirmadas na mesma sessão de testes.

**✅ Checkpoint:** logado como Bruno, o botão Excluir apaga um post dele; no post da Ana, a mesma ação responde 403 Forbidden.

### Resumo do que você construiu

```
✅ Base completa: projeto, banco, model Usuario autenticável e login/logout
✅ Post relacionado a Usuario (belongsTo / hasMany)
✅ PostDAO com eager loading evitando N+1, ligado por binding no container
✅ Formulário protegido por middleware de autenticação
✅ ID do usuário logado associando o post, sem confiar no cliente
✅ Views listando posts com nome do autor e mostrando erros de validação
✅ Autorização por dono do recurso (403 se não for o autor)
```

### Exercícios

1. **Edição**: rota PATCH para um post, só para o autor.
2. **Comentários**: nova entidade Comentario relacionada a Post e Usuario.
3. **Paginação**: troque `->get()` por `->paginate(10)` na listagem.
4. **Policy**: pesquise `php artisan make:policy` e mova a checagem de dono para uma Policy dedicada.

### Perguntas de fixação

1. Por que `curl -X DELETE` sem token devolve 419 e não 403, mesmo quando o usuário logado não é o dono do post?
2. Onde, exatamente, o `usuario_id` do post é decidido — no formulário, no controller, ou em algum outro lugar? Por quê?
3. Aponte, no `PostController` deste tópico, uma linha de cada uma das três aulas anteriores (Formulários, Sessões, DAO).

---

## 6. Introdução a POO

**Objetivo:** revisar classes e objetos em PHP puro — a base conceitual — antes de aplicá-los dentro do Laravel. Novo projeto de estudo: "Gerenciador de Tarefas" (`task-manager`).

PHP 8.3 / Laravel 13.21.

### Pré-requisitos

PHP 8.3+ instalado (`php -v`).

**✅ Checkpoint:** `php -v` mostra 8.3 ou superior.

### Passo 1 — crie a classe Tarefa em PHP puro

```php
<?php
// Tarefa.php
class Tarefa
{
    public function __construct(
        private string $titulo,
        private bool $concluida = false,
    ) {}

    public function concluir(): void
    {
        $this->concluida = true;
    }

    public function getTitulo(): string
    {
        return $this->titulo;
    }

    public function estaConcluida(): bool
    {
        return $this->concluida;
    }
}
```

**Constructor property promotion** (PHP 8+): declarar `private string $titulo` direto no `__construct` cria o atributo e o atribui automaticamente — elimina o boilerplate de `$this->titulo = $titulo;`.

**✅ Checkpoint:** `php -l Tarefa.php` não reporta erro de sintaxe.

### Passo 2 — instancie objetos e chame métodos

```php
<?php
require 'Tarefa.php';

$t1 = new Tarefa("Estudar POO");
$t2 = new Tarefa("Revisar Laravel", concluida: true);

echo $t1->getTitulo() . "\n";              // Estudar POO
echo $t1->estaConcluida() ? "sim" : "não"; // não

$t1->concluir();
echo $t1->estaConcluida() ? "sim" : "não"; // sim
```

`concluida: true` é um **argumento nomeado** (named argument) — permite pular o parâmetro `titulo` na ordem e ser explícito sobre qual valor vai para qual parâmetro.

**✅ Checkpoint:** `php teste.php` imprime "Estudar POO", "não" e depois "sim".

### Passo 3 — entenda encapsulamento — por que `private` importa

```php
// Isso NÃO compila (erro): $t1->titulo é privado
// echo $t1->titulo;

// Isso funciona: acesso controlado via método público
echo $t1->getTitulo();
```

`private` impede acesso direto de fora da classe — força quem usa `Tarefa` a passar pelos métodos públicos (`getTitulo()`), que podem validar ou transformar o dado antes de expô-lo.

**✅ Checkpoint:** você entende por que "esconder" o atributo atrás de um getter é mais seguro que deixá-lo público.

### Passo 4 — adicione validação no construtor

```php
public function __construct(
    private string $titulo,
    private bool $concluida = false,
) {
    if (trim($titulo) === '') {
        throw new InvalidArgumentException('Título não pode ser vazio');
    }
}
```

Colocar a validação no construtor garante que **nenhum** objeto `Tarefa` inválido consiga existir — é impossível criar uma tarefa sem título em qualquer parte do sistema.

**✅ Checkpoint:** `new Tarefa("")` lança `InvalidArgumentException`.

### Passo 5 — traga a classe para dentro do Laravel como Model

```bash
composer create-project laravel/laravel task-manager
cd task-manager
```

> 💡 Se você instalou o **Laravel Boost** no `blog-app` (Tópico 1) e quer o mesmo suporte de IA aqui, é por projeto — repita `composer require laravel/boost --dev` e `php artisan boost:install` dentro deste novo projeto `task-manager`.

Ajuste o `.env` para MySQL (mesmo padrão dos tópicos anteriores, com um banco próprio — por exemplo `task_manager`, para não misturar com os dados do `blog-app`):

```bash
php artisan make:model Tarefa -m
```

```php
public function up(): void
{
    Schema::create('tarefas', function (Blueprint $table) {
        $table->id();
        $table->string('titulo');
        $table->date('prazo')->nullable();
        $table->boolean('concluida')->default(false);
        $table->string('tipo')->default('rotina'); // 'urgente' | 'rotina'
        $table->timestamps();
    });
}
```

```bash
php artisan migrate
```

`prazo` e `tipo` ainda não entram em nenhum método deste tópico — a tabela já nasce com o esquema completo (`nullable()` em `prazo`, porque nem toda tarefa precisa de data) para não exigir uma segunda migration de "adicionar coluna" mais adiante: o Tópico 7 (POO Aplicada) passa a usar `tipo`, e o Tópico 8 (Padrão MVC) em diante usa `prazo` para calcular urgência.

Um `Model` Eloquent **é** uma classe PHP — o Laravel adiciona persistência (`Tarefa::create(...)`, `Tarefa::find(1)`) por cima dos mesmos conceitos de classes e objetos que você acabou de praticar.

**✅ Checkpoint:** `Tarefa::create(['titulo' => 'Aprender Eloquent'])` no tinker cria uma linha na tabela `tarefas`.

### Resumo do que você construiu

```
✅ Classe Tarefa em PHP puro com constructor property promotion
✅ Instanciação de objetos e chamada de métodos
✅ Encapsulamento com atributos private e getters públicos
✅ Validação no construtor, tornando estados inválidos impossíveis
✅ Migração da classe conceitual para um Model Eloquent no Laravel
```

### Exercícios

1. **Método reabrir()**: volta uma tarefa concluída para pendente.
2. **Prioridade**: adicione um atributo `prioridade` (enum: baixa/media/alta) com validação.
3. **__toString()**: implemente para imprimir a tarefa de forma legível com `echo $t1;`.

### Perguntas de fixação

1. O que muda, na prática, entre declarar `$titulo` no corpo da classe e usar constructor property promotion?
2. Por que a validação de título vazio entra no construtor, e não num método separado chamado depois?
3. Um Model Eloquent como `Tarefa extends Model` ainda é "uma classe PHP normal" no sentido do Passo 1? O que o Eloquent acrescenta por cima?

---

## Factories — gerando dados de teste realistas

**Objetivo:** entender por que criar `Tarefa` manualmente, uma a uma, não escala além de dois ou três exemplos — e como uma Model Factory do Laravel gera dados fake, porém realistas e sempre válidos, reaproveitáveis em tinker, seeders e testes automatizados.

PHP 8.3 / Laravel 13.21. Continuação do "Gerenciador de Tarefas".

### Pré-requisitos

O Model `Tarefa` e a migration `tarefas` do Tópico 6, já migrados contra o banco `task_manager`.

**✅ Checkpoint:** `php artisan migrate:status` mostra `..._create_tarefas_table` com status `Ran`.

### Passo 1 — o problema: povoar o banco manualmente não escala

No tinker, criar algumas tarefas de teste é só isso — chamadas repetidas a `Tarefa::create([...])`:

```php
Tarefa::create(['titulo' => 'Revisar PR', 'tipo' => 'urgente', 'prazo' => now()->addDay()]);
Tarefa::create(['titulo' => 'Escrever relatório', 'tipo' => 'rotina', 'prazo' => now()->addWeek()]);
Tarefa::create(['titulo' => 'Corrigir bug crítico', 'tipo' => 'urgente', 'concluida' => false]);
Tarefa::create(['titulo' => 'Organizar reunião', 'tipo' => 'rotina', 'concluida' => true]);
```

Para 3 ou 4 exemplos isso ainda é tolerável. Mas peça "20 tarefas para testar a paginação" (Tópico 9) ou "uma tarefa nova para cada caso de teste" (a partir do Tópico 12) e o mesmo padrão vira dezenas de linhas quase idênticas, digitadas à mão, uma por uma. Além do tédio, títulos repetidos ou sempre curtos escondem bugs: um `titulo` que nunca varia de tamanho jamais revela, por exemplo, um bug de truncamento num `varchar` pequeno demais.

É exatamente esse problema que uma **Model Factory** resolve: uma classe que sabe gerar, sob demanda, quantas instâncias de `Tarefa` você quiser, cada uma com dados **fake** — gerados pela biblioteca **Faker**, que produz valores com "cara" de dado real (nomes, frases, datas plausíveis) em vez dos mesmos 2 ou 3 valores fixos repetidos à mão.

**✅ Checkpoint:** você já sentiu, na prática, o trabalho de criar mais de 3 tarefas manualmente no tinker.

### Passo 2 — gere a factory

```bash
php artisan make:factory TarefaFactory --model=Tarefa --no-interaction
```

```
INFO  Factory [database/factories/TarefaFactory.php] created successfully.
```

O comando cria `database/factories/TarefaFactory.php`, já ligado ao Model `Tarefa` (`--model=Tarefa`), estendendo `Illuminate\Database\Eloquent\Factories\Factory` com um método `definition(): array` vazio — é ali que você descreve, campo a campo, como é "uma `Tarefa` fake típica".

**⚠️ Armadilha real — o Model precisa do trait `HasFactory`.** Mesmo com o arquivo da factory já criado, chamar `Tarefa::factory()` no tinker agora lança:

```
BadMethodCallException: Call to undefined method App\Models\Tarefa::factory().
```

`Tarefa::create(...)` (Passo 1) sempre funcionou sem nenhum preparo extra, porque `create()` é um método do próprio `Model` do Eloquent. Já o método estático `factory()` **não** vem de `Model` — ele é adicionado pelo trait `Illuminate\Database\Eloquent\Factories\HasFactory`, que é quem sabe, por convenção de nomes, associar `Tarefa` à classe `Database\Factories\TarefaFactory`. Sem o trait, o Model simplesmente não tem esse método. A correção:

```php
use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Tarefa extends Model
{
    use HasFactory;

    protected $table = 'tarefas';
    // ...
}
```

Com o trait adicionado, `Tarefa::factory()` passa a devolver `Database\Factories\TarefaFactory` — confirmado rodando `get_class(Tarefa::factory())` no tinker.

**✅ Checkpoint:** `Tarefa::factory()` no tinker não lança mais exceção nenhuma.

### Passo 3 — defina `definition()`

```php
public function definition(): array
{
    return [
        'titulo' => fake()->sentence(4),
        'prazo' => fake()->dateTimeBetween('now', '+30 days'),
        'concluida' => fake()->boolean(20),
        'tipo' => fake()->randomElement(['urgente', 'rotina']),
    ];
}
```

Repare que `tipo` usa `fake()->randomElement(['urgente', 'rotina'])`, e não algo genérico como `fake()->word()`: `Tarefa::calcularPrioridade()` (Tópico 7) faz `match($this->tipo)` só com esses dois casos, caindo em `default => 0` para qualquer outro valor. Uma factory que gerasse `tipo` fora desse domínio produziria tarefas "válidas" no banco, mas silenciosamente quebradas do ponto de vista de negócio — o tipo de bug que só aparece muito depois, num relatório de prioridades zeradas sem explicação. `fake()->boolean(20)` gera `true` só 20% das vezes: a maioria das tarefas fake nasce pendente, o que é mais realista que 50/50.

Em tinker, `make()` monta o objeto em memória sem tocar no banco, e `create()` salva de verdade:

```php
$t = Tarefa::factory()->make();
// $t->id é null, e Tarefa::find($t->id) não encontra nada — nada foi salvo
```

```php
$t = Tarefa::factory()->create();
// $t->id já vem preenchido, e Tarefa::find($t->id) encontra a linha
```

**Verificado na implementação de referência:** `make()` devolveu um objeto com `titulo`, `prazo` e `tipo` preenchidos por Faker (ex.: `titulo: "Eos eum ducimus."`, `tipo: urgente`) e `id` nulo — confirmando que nada foi persistido. Em seguida, `create()` devolveu uma `Tarefa` já com `id` atribuído pelo banco, e uma nova consulta a `Tarefa::find($t->id)` encontrou a linha de verdade.

**✅ Checkpoint:** `Tarefa::factory()->create()` no tinker devolve uma `Tarefa` com `id` preenchido, e ela aparece numa consulta `Tarefa::find($id)` logo em seguida.

### Passo 4 — gere em lote e inspecione

```php
Tarefa::factory(20)->create();
```

Esse único comando salva 20 tarefas fake, cada uma com valores diferentes de `titulo`, `prazo` e `tipo`.

**Verificado na implementação de referência:** antes do comando, `Tarefa::count()` valia 5; depois, 25 — uma diferença de exatamente 20, confirmando que o lote inteiro foi persistido numa única chamada, sem nenhum loop escrito à mão.

**✅ Checkpoint:** `Tarefa::count()` antes e depois de `Tarefa::factory(20)->create()` difere em exatamente 20.

### Passo 5 — states para os casos de borda do domínio

Nem toda tarefa de teste é "genérica" — às vezes você precisa, especificamente, de uma tarefa urgente ou de uma já concluída. Para isso existem os **states**: variações nomeadas da factory, no mesmo padrão que `UserFactory::unverified()` já usa (`database/factories/UserFactory.php`) para gerar um usuário com `email_verified_at` nulo.

```php
/**
 * Indica que a tarefa é urgente e tem prazo próximo (dentro de 2 dias),
 * para bater com a regra de `Tarefa::isUrgente()`.
 */
public function urgente(): static
{
    return $this->state(fn (array $attributes) => [
        'tipo' => 'urgente',
        'prazo' => fake()->dateTimeBetween('now', '+2 days'),
        'concluida' => false,
    ]);
}

/**
 * Indica que a tarefa já foi concluída.
 */
public function concluida(): static
{
    return $this->state(fn (array $attributes) => [
        'concluida' => true,
    ]);
}
```

`$this->state(fn (array $attributes) => [...])` devolve uma nova instância da factory com esses campos sobrescrevendo o que `definition()` geraria — os outros campos continuam vindo do Faker normalmente. Os states são encadeáveis com `->create()`: `Tarefa::factory()->urgente()->create()`.

**Verificado na implementação de referência:** `Tarefa::factory()->urgente()->create()` gerou uma tarefa com `tipo: urgente` e `prazo` dentro de 2 dias; chamando `isUrgente()` no objeto retornado, o resultado foi `true`, e `calcularPrioridade()` devolveu `10` — a nota máxima, exatamente a regra de negócio do Tópico 7 reagindo aos dados gerados pela factory. `Tarefa::factory()->concluida()->create()` gerou uma tarefa com `concluida: true`, como esperado.

**✅ Checkpoint:** `Tarefa::factory()->urgente()->create()->isUrgente()` devolve `true` de verdade, não por coincidência do Faker.

### Passo 6 — use no seeder

```php
public function run(): void
{
    // User::factory(10)->create();

    User::factory()->create([
        'name' => 'Test User',
        'email' => 'test@example.com',
    ]);

    Tarefa::factory(15)->create();
    Tarefa::factory(3)->urgente()->create();
    Tarefa::factory(2)->concluida()->create();
}
```

```bash
php artisan migrate:fresh --seed
```

`migrate:fresh` derruba e recria todas as tabelas do zero, e `--seed` roda `DatabaseSeeder` em seguida — o mesmo banco `task_manager`, agora populado inteiramente por código, sem passos manuais.

**Verificado na implementação de referência:** rodando o comando contra o banco `task_manager` de verdade, as 5 migrations recriaram as tabelas e o seeder rodou sem erros. Consultando o banco depois: `Tarefa::count()` = 20 (15 + 3 + 2, como no seeder), `Tarefa::where('tipo', 'urgente')->count()` = 15 e `Tarefa::where('concluida', true)->count()` = 5 — a variação entre urgentes/rotina e concluída/pendente vindo da combinação dos states explícitos com a distribuição aleatória de `definition()`.

**✅ Checkpoint:** depois de `php artisan migrate:fresh --seed`, `Tarefa::count()` no tinker mostra 20.

### Passo 7 — use em um teste

```bash
php artisan make:test --phpunit TarefaFactoryTest --no-interaction
```

```php
<?php

namespace Tests\Feature;

use App\Models\Tarefa;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Tests\TestCase;

class TarefaFactoryTest extends TestCase
{
    use RefreshDatabase;

    public function test_tarefa_criada_pela_factory_aparece_na_listagem(): void
    {
        $tarefa = Tarefa::factory()->create(['titulo' => 'Tarefa de teste via factory']);

        $response = $this->get('/tarefas');

        $response->assertStatus(200);
        $response->assertSee('Tarefa de teste via factory');
    }

    public function test_state_urgente_produz_tarefa_urgente_com_prioridade_maxima(): void
    {
        $tarefa = Tarefa::factory()->urgente()->create();

        $this->assertTrue($tarefa->isUrgente());
        $this->assertSame(10, $tarefa->calcularPrioridade());
    }

    public function test_state_concluida_nunca_e_urgente(): void
    {
        $tarefa = Tarefa::factory()->concluida()->create(['tipo' => 'urgente']);

        $this->assertTrue($tarefa->concluida);
        $this->assertFalse($tarefa->isUrgente());
    }
}
```

`use RefreshDatabase;` garante que cada teste roda contra um banco limpo (recriado a partir das migrations), e cada `Tarefa::factory()->create(...)` monta uma tarefa completa — com todo campo obrigatório preenchido por Faker — sobrescrevendo só o que importa para aquele teste específico. Nenhum teste precisou listar `titulo`, `prazo`, `concluida` e `tipo` na mão, e continuam funcionando mesmo que o Model ganhe novas colunas no futuro, desde que a factory seja atualizada uma única vez.

```bash
php artisan test --filter=TarefaFactoryTest
```

**Verificado na implementação de referência:** os 3 testes passaram, com 6 asserções, em cerca de 260 ms. Rodando a suíte inteira depois (`php artisan test`), o resultado foi 4 de 5 testes passando — a única falha é o `ExampleTest` pré-existente, que já esperava `/` devolver 200 antes desta mudança (na prática `/` redireciona para `/tarefas`, um achado anterior a esta seção e fora do escopo aqui).

**✅ Checkpoint:** `php artisan test --filter=TarefaFactoryTest` mostra os 3 testes passando, sem nenhuma falha.

### Resumo do que você construiu

```
✅ TarefaFactory (database/factories/TarefaFactory.php), gerando titulo/prazo/concluida/tipo sempre válidos
✅ Trait HasFactory no Model Tarefa, sem o qual Tarefa::factory() não existe
✅ make() (em memória) vs create() (persistido) demonstrados de verdade em tinker
✅ Geração em lote (Tarefa::factory(20)->create()) substituindo loops manuais
✅ States urgente() e concluida(), no mesmo padrão de UserFactory::unverified()
✅ DatabaseSeeder populando Tarefa via factory, rodado de ponta a ponta com migrate:fresh --seed
✅ TarefaFactoryTest usando a factory para preparar cenários, sem digitar campos obrigatórios na mão
```

### Exercícios

1. **Fácil — state `atrasada()`**: adicione um novo state que fixa um `prazo` no passado e `concluida = false`, e confirme com `isAtrasada()`.
2. **Médio — `ConfiguracaoFactory`**: crie uma factory para o Model `Configuracao` (Tópico 9, Singleton). Antes de escrever `definition()`, pense em quantas linhas dessa tabela sua aplicação realmente usa ao mesmo tempo.
3. **Difícil — `sequence()`**: use `Tarefa::factory()->count(5)->sequence(fn ($seq) => ['titulo' => "Tarefa {$seq->index}"])` para gerar uma sequência numerada de títulos, e compare o resultado com simplesmente chamar `fake()` dentro de `definition()`.

### Perguntas de fixação

1. `Tarefa::create(...)` sempre funcionou sem nenhum preparo extra no Model. Por que `Tarefa::factory()` precisou do trait `HasFactory`, se os dois "criam uma tarefa"?
2. Por que uma factory faz menos sentido para `Configuracao` (Tópico 9, Singleton) do que para `Tarefa`?
3. Qual é a diferença prática entre usar `sequence()` e simplesmente deixar `fake()` gerar um valor diferente a cada chamada dentro de `definition()`?

---

## 7. POO Aplicada (herança, polimorfismo)

**Objetivo:** aplicar herança e polimorfismo a tipos diferentes de tarefa, e entender a alternativa prática dentro de um Model Eloquent único.

PHP 8.3 / Laravel 13.21. Continuação do "Gerenciador de Tarefas".

### Pré-requisitos

Classe `Tarefa` das aulas anteriores.

**✅ Checkpoint:** `new Tarefa("Teste")` funciona.

### Passo 1 — transforme Tarefa em classe abstrata

```php
<?php
abstract class Tarefa
{
    protected bool $concluida = false;

    public function __construct(protected string $titulo) {}

    abstract public function calcularPrioridade(): int;

    public function concluir(): void
    {
        $this->concluida = true;
    }

    public function getTitulo(): string
    {
        return $this->titulo;
    }
}
```

`abstract class` não pode ser instanciada diretamente (`new Tarefa(...)` agora dá erro) — força a existência de subclasses concretas. `abstract public function calcularPrioridade()` declara o **contrato**: toda subclasse é obrigada a implementá-lo.

**✅ Checkpoint:** `new Tarefa("x")` lança `Error: Cannot instantiate abstract class`.

### Passo 2 — crie subclasses concretas

```php
<?php
class TarefaUrgente extends Tarefa
{
    public function __construct(string $titulo, private \DateTime $prazo)
    {
        parent::__construct($titulo);
    }

    public function calcularPrioridade(): int
    {
        $diasRestantes = (new \DateTime())->diff($this->prazo)->days;
        return $diasRestantes <= 2 ? 10 : 5;
    }
}

class TarefaRotina extends Tarefa
{
    public function calcularPrioridade(): int
    {
        return 1;
    }
}
```

`extends Tarefa` herda `$titulo`, `concluir()` e `getTitulo()` automaticamente. `parent::__construct($titulo)` chama o construtor da classe pai antes de continuar a inicialização específica da subclasse.

**✅ Checkpoint:** `new TarefaUrgente("Entregar relatório", new DateTime('+1 day'))` compila e funciona.

### Passo 3 — use polimorfismo: mesma chamada, comportamento diferente

```php
$tarefas = [
    new TarefaUrgente("Corrigir bug crítico", new DateTime('+1 day')),
    new TarefaRotina("Revisar backlog semanal"),
];

foreach ($tarefas as $t) {
    echo $t->getTitulo() . ": prioridade " . $t->calcularPrioridade() . "\n";
}
```

O `foreach` chama `calcularPrioridade()` sem saber (nem precisar saber) se o objeto é `TarefaUrgente` ou `TarefaRotina` — cada um executa **sua própria** versão do método. Isso é polimorfismo.

**✅ Checkpoint:** a saída mostra prioridades diferentes (10 e 1) para o mesmo método chamado.

### Passo 4 — ordene tarefas pela prioridade calculada polimorficamente

```php
usort($tarefas, fn($a, $b) => $b->calcularPrioridade() <=> $a->calcularPrioridade());

foreach ($tarefas as $t) {
    echo $t->getTitulo() . "\n"; // TarefaUrgente aparece primeiro
}
```

`<=>` (*spaceship operator*) retorna -1, 0 ou 1 comparando dois valores — `$b <=> $a` ordena do maior para o menor. `usort` chama `calcularPrioridade()` de cada objeto sem conhecer seu tipo concreto.

**✅ Checkpoint:** a tarefa urgente aparece antes da tarefa de rotina após ordenar.

### Passo 5 — traga a hierarquia para dentro do Laravel via Model + accessor

```php
class Tarefa extends Model
{
    public function calcularPrioridade(): int
    {
        return match ($this->tipo) {
            'urgente' => $this->prazo->diffInDays(now()) <= 2 ? 10 : 5,
            'rotina' => 1,
            default => 0,
        };
    }
}
```

Sem herança real entre Models Eloquent (mais raro em Laravel — um único Model geralmente mapeia para uma única tabela), o `match` simula o mesmo polimorfismo conceitual usando um campo `tipo` — uma alternativa comum quando persistir uma hierarquia de classes complica o schema do banco. Este é exatamente o `Tarefa` que a implementação de referência usa: uma única tabela `tarefas` com uma coluna `tipo` (`urgente`/`rotina`), em vez de tabelas separadas por subclasse.

**Verificado na implementação de referência**: no tinker do `task-manager` real, `Tarefa::first()->calcularPrioridade()` devolveu `10` para uma tarefa com `tipo = 'urgente'` e prazo em menos de dois dias, e `1` para uma com `tipo = 'rotina'` — o mesmo padrão do Passo 3, agora persistido em banco.

**✅ Checkpoint:** `Tarefa::find(1)->calcularPrioridade()` retorna o valor correto conforme o campo `tipo`.

### Resumo do que você construiu

```
✅ Classe abstrata Tarefa com método abstrato calcularPrioridade()
✅ Subclasses TarefaUrgente e TarefaRotina implementando o contrato de formas diferentes
✅ Polimorfismo: mesmo método chamado, comportamento decidido em tempo de execução
✅ Ordenação de coleção heterogênea usando o comportamento polimórfico
✅ Alternativa via match() para simular polimorfismo dentro de um único Model Eloquent
```

### Exercícios

1. **TarefaRecorrente**: nova subclasse que reseta `concluida = false` a cada X dias.
2. **Template method**: extraia um método `descricaoCompleta()` na classe abstrata que chama `calcularPrioridade()` internamente.
3. **Comparação com interface**: reescreva usando uma `interface CalculavelPrioridade` em vez de classe abstrata e discuta a diferença.

### Perguntas de fixação

1. Por que `new Tarefa(...)` deixa de compilar depois que a classe vira `abstract`?
2. O que exatamente o `match($this->tipo)` do Passo 5 está simulando, e por que essa abordagem é mais comum em Laravel do que herança real entre Models?
3. `usort` no Passo 4 chama `calcularPrioridade()` em objetos de classes diferentes sem nenhum `if (get_class($t) === ...)`. Por que isso é seguro?

---

## 8. Padrão MVC

**Objetivo:** formalizar o padrão MVC — separar claramente Model, View e Controller, que o Laravel já organiza por convenção.

Laravel 13.21. Continuação do "Gerenciador de Tarefas".

### Pré-requisitos

Model `Tarefa` e algumas rotas já criadas.

**✅ Checkpoint:** `Tarefa::count()` funciona no tinker.

### Passo 1 — entenda as três camadas

| Camada | Responsabilidade | No Laravel |
|---|---|---|
| **Model** | Dados e regras de negócio | `app/Models/Tarefa.php` |
| **View** | Apresentação (HTML) | `resources/views/*.blade.php` |
| **Controller** | Orquestra: recebe requisição, chama Model, escolhe View | `app/Http/Controllers/*.php` |

Regra prática: se o código **consulta ou calcula dados**, é Model. Se **decide o que mostrar**, é Controller. Se só **exibe**, é View.

**✅ Checkpoint:** você consegue classificar qualquer trecho de código do projeto em uma das três camadas.

### Passo 2 — identifique uma violação do MVC (lógica de negócio na View)

```blade
{{-- ERRADO: cálculo de negócio dentro do Blade --}}
@foreach($tarefas as $tarefa)
    @if(now()->diffInDays($tarefa->prazo) <= 2 && !$tarefa->concluida)
        <span class="urgente">{{ $tarefa->titulo }}</span>
    @endif
@endforeach
```

Calcular "é urgente?" dentro do template mistura apresentação com regra de negócio — se essa regra mudar, é preciso caçar em vários arquivos Blade.

**✅ Checkpoint:** você aponta exatamente qual linha viola a separação de responsabilidades.

### Passo 3 — mova a regra para o Model

```php
// app/Models/Tarefa.php
public function isUrgente(): bool
{
    if ($this->concluida || ! $this->prazo) {
        return false;
    }

    return now()->diffInDays($this->prazo, false) <= 2;
}
```

Duas coisas que uma versão mais curta, tipo `!$this->concluida && now()->diffInDays($this->prazo) <= 2`, deixaria passar: `prazo` é `nullable()` desde a migration do Tópico 6 — uma tarefa sem prazo (criada, por exemplo, direto no tinker) faria `diffInDays` explodir num objeto nulo sem o `! $this->prazo` antes. E o segundo argumento `false` em `diffInDays` pede a diferença **com sinal**, não o valor absoluto: sem ele, uma tarefa **já atrasada** também cairia dentro de "≤ 2".

"Já atrasada" é, de propósito, um conceito **diferente** de "urgente" — e por isso mora no próprio método ao lado, não misturado dentro de `isUrgente()`:

```php
public function isAtrasada(): bool
{
    return ! $this->concluida && $this->prazo && $this->prazo->isPast();
}
```

Mesma forma: regra de negócio no Model, nada de `if` de data em Controller ou View.

```blade
{{-- CORRETO --}}
@foreach($tarefas as $tarefa)
    @if($tarefa->isUrgente())
        <span class="urgente">{{ $tarefa->titulo }}</span>
    @endif
@endforeach
```

Agora a regra de "urgência" vive em **um único lugar** — o Model — e a View só pergunta, sem saber como a resposta é calculada. Volte também ao `calcularPrioridade()` do Tópico 7 e troque o cálculo repetido por uma chamada a este método novo:

```php
public function calcularPrioridade(): int
{
    return match ($this->tipo) {
        'urgente' => $this->isUrgente() ? 10 : 5,
        'rotina' => 1,
        default => 0,
    };
}
```

Sem essa troca, a regra de "o que é urgente" ficaria duplicada em dois métodos do mesmo Model — exatamente o tipo de duplicação que este tópico existe para eliminar.

**✅ Checkpoint:** a página continua mostrando as mesmas tarefas urgentes, mas a lógica saiu do Blade; `Tarefa::find(1)->calcularPrioridade()` continua devolvendo o mesmo valor de antes da troca.

### Passo 4 — mantenha o Controller enxuto, delegando ao Model

```php
class TarefaController extends Controller
{
    public function index()
    {
        $tarefas = Tarefa::orderByDesc('prazo')->get();
        $urgentes = $tarefas->filter->isUrgente();

        return view('tarefas.index', compact('tarefas', 'urgentes'));
    }
}
```

`$tarefas->filter->isUrgente()` é um atalho de Collection do Laravel — equivalente a `$tarefas->filter(fn($t) => $t->isUrgente())`. O Controller não calcula nada sozinho, só orquestra Model → View. Guarde essa variável `$urgentes`: a partir daqui ela viaja junto com `$tarefas` por todos os tópicos seguintes — inclusive quando o Tópico 9 introduz paginação e o Tópico 12 introduz o DAO — porque é ela que `tarefas/index.blade.php` (Tópico 10) usa para mostrar "Urgentes nesta página".

**✅ Checkpoint:** `/tarefas` mostra a contagem de tarefas urgentes vinda do Controller.

### Passo 5 — reconheça o MVC nas rotas RESTful padrão do Laravel

```bash
php artisan make:controller TarefaController --resource
```

```php
Route::resource('tarefas', TarefaController::class);
```

`Route::resource` gera automaticamente as 7 rotas convencionais (`index`, `create`, `store`, `show`, `edit`, `update`, `destroy`) — cada uma mapeando para um método do Controller, seguindo a convenção MVC do framework sem configuração extra.

**✅ Checkpoint:** `php artisan route:list` mostra as 7 rotas geradas para `tarefas`.

### Resumo do que você construiu

```
✅ Entendimento formal das três camadas: Model, View, Controller
✅ Identificação de uma violação real (regra de negócio dentro do Blade)
✅ Regra de urgência movida para o Model (isUrgente(), com guarda contra prazo nulo)
✅ isAtrasada() ao lado, cobrindo um conceito relacionado mas diferente de "urgente"
✅ calcularPrioridade() (Tópico 7) atualizado para reaproveitar isUrgente(), sem duplicar a regra
✅ Controller enxuto, delegando cálculo ao Model e escolha de View
✅ Route::resource gerando as 7 rotas RESTful convencionais
```

### Exercícios

1. **View Composer**: pesquise como compartilhar `$urgentes` com múltiplas views sem repetir no Controller (o Tópico 10 mostra a implementação real).
2. **Resource Controller completo**: implemente os 7 métodos gerados por `--resource`.
3. **isProxima()**: um método novo, ao lado de `isUrgente()`/`isAtrasada()`, para tarefas com prazo entre 3 e 7 dias — sem repetir o guard contra `prazo` nulo.

### Perguntas de fixação

1. Por que `now()->diffInDays($tarefa->prazo) <= 2` dentro de um `@if` no Blade é considerado uma violação do MVC, mesmo funcionando corretamente?
2. O que `Route::resource` economiza, em comparação com registrar 7 `Route::get`/`post`/`put`/`delete` manualmente?
3. Dado o `TarefaController::index()` do Passo 4, qual camada seria responsável por decidir a **cor** do badge "urgente" no HTML — Model, View, ou Controller?

---

## 9. Implementação de Controller

**Objetivo:** implementar o `TarefaController` completo com os 7 métodos RESTful convencionais, e conhecer os outros tipos de Controller que o Laravel reconhece por convenção — Invokable e Singleton — além do Resource já usado e do API Controller do Tópico 14.

Laravel 13.21. Continuação do "Gerenciador de Tarefas".

### Pré-requisitos

`Route::resource('tarefas', TarefaController::class)` já registrada.

**✅ Checkpoint:** `php artisan route:list --name=tarefas` mostra as 7 rotas.

### Passo 1 — implemente index() e show()

```php
class TarefaController extends Controller
{
    public function index()
    {
        $tarefas = Tarefa::orderByDesc('prazo')->paginate(10);
        $urgentes = $tarefas->getCollection()->filter->isUrgente();

        return view('tarefas.index', compact('tarefas', 'urgentes'));
    }

    public function show(Tarefa $tarefa)
    {
        return view('tarefas.show', compact('tarefa'));
    }
}
```

`show(Tarefa $tarefa)` usa **route model binding**: o Laravel automaticamente busca a `Tarefa` pelo `id` da URL e injeta o objeto pronto — sem `Tarefa::findOrFail($id)` manual.

Repare que `index()` mudou de `->get()` (Tópico 8) para `->paginate(10)` — e por isso `$tarefas->filter->isUrgente()` também mudou para `$tarefas->getCollection()->filter->isUrgente()`. `paginate()` devolve um `LengthAwarePaginator`, não uma `Collection`: ele sabe navegar entre páginas e montar os links (Passo 5 do Tópico 10), mas não tem `filter()` diretamente. `getCollection()` pega só os itens **da página atual** como uma Collection de verdade, aí sim filtrável.

**✅ Checkpoint:** `/tarefas/1` mostra a tarefa de id 1 sem código explícito de busca no Controller.

### Passo 2 — implemente create() e store()

```php
public function create()
{
    return view('tarefas.create');
}

public function store(Request $request)
{
    $dados = $request->validate([
        'titulo' => 'required|string|max:150',
        'prazo' => 'required|date|after:today',
        'tipo' => 'required|in:urgente,rotina',
    ]);

    Tarefa::create($dados);

    return redirect()->route('tarefas.index')->with('sucesso', 'Tarefa criada!');
}
```

`redirect()->route(...)->with(...)` redireciona após o POST (padrão PRG — Post/Redirect/Get, evita reenvio duplicado ao atualizar a página) e carrega uma mensagem flash para a próxima página. `'tipo' => 'required|in:urgente,rotina'` é a regra `in:` — rejeita qualquer valor fora dessa lista, mesmo alguém adulterando o `<select>` do Passo 5 do Tópico 10 via DevTools. Sem essa validação, a coluna `tipo` (Tópico 6) nunca receberia outra coisa além do valor padrão `rotina` da migration, e a distinção urgente/rotina do Tópico 7 (POO Aplicada) nunca chegaria a aparecer numa tarefa criada de verdade pelo formulário.

**✅ Checkpoint:** criar uma tarefa redireciona para `/tarefas` mostrando a mensagem de sucesso.

### Passo 3 — implemente edit() e update()

```php
public function edit(Tarefa $tarefa)
{
    return view('tarefas.edit', compact('tarefa'));
}

public function update(Request $request, Tarefa $tarefa)
{
    $dados = $request->validate([
        'titulo' => 'required|string|max:150',
        'prazo' => 'required|date',
        'tipo' => 'required|in:urgente,rotina',
    ]);

    $tarefa->update($dados);

    return redirect()->route('tarefas.show', $tarefa);
}
```

`$tarefa->update($dados)` atualiza só os campos passados, mantendo o resto inalterado — mais direto que reatribuir cada atributo manualmente. Repare que `update()` não leva `after:today` em `prazo`, diferente de `store()`: editar uma tarefa cujo prazo já passou (por exemplo, para marcar como atrasada e ajustar só o título) não pode ser bloqueado pela mesma regra que impede **criar** uma tarefa já nascendo vencida.

**✅ Checkpoint:** editar o título de uma tarefa e salvar reflete a mudança na tela de detalhes.

### Passo 4 — implemente destroy() com confirmação

```php
public function destroy(Tarefa $tarefa)
{
    $tarefa->delete();
    return redirect()->route('tarefas.index')->with('sucesso', 'Tarefa removida.');
}
```

```blade
<!-- na view, um form disfarçado de DELETE via method spoofing -->
<form method="POST" action="{{ route('tarefas.destroy', $tarefa) }}">
    @csrf
    @method('DELETE')
    <button type="submit">Excluir</button>
</form>
```

Navegadores só enviam GET/POST nativamente — `@method('DELETE')` insere um campo oculto que o Laravel interpreta como se fosse uma requisição DELETE real, mantendo a rota RESTful correta.

**✅ Checkpoint:** clicar "Excluir" remove a tarefa e redireciona para a listagem.

### Passo 5 — revise os 7 métodos e sua correspondência HTTP

| Método do Controller | Verbo HTTP | Rota |
|---|---|---|
| index | GET | /tarefas |
| create | GET | /tarefas/create |
| store | POST | /tarefas |
| show | GET | /tarefas/{id} |
| edit | GET | /tarefas/{id}/edit |
| update | PUT/PATCH | /tarefas/{id} |
| destroy | DELETE | /tarefas/{id} |

**✅ Checkpoint:** você recita de cabeça a correspondência entre pelo menos 4 desses métodos e seus verbos.

### Passo 6 — Invokable Controller: quando o Controller faz só uma coisa

`TarefaController` tem 7 métodos porque uma tarefa tem 7 operações convencionais. Mas nem toda ação do sistema é assim — "marcar uma tarefa como concluída" é uma ação só, sem `create`/`edit` (não existe formulário de "concluir"), sem `index` (não lista nada), sem `show` (não exibe nada sozinha). Forçar essa ação a morar dentro de `TarefaController` como um oitavo método (`concluir()`, sem correspondência com nenhum dos 7 verbos RESTful) mistura duas responsabilidades diferentes na mesma classe. Um **Invokable Controller** resolve isso: uma classe, um único método `__invoke()`, nomeada pelo verbo que ela executa em vez de pelo recurso que ela gerencia.

```bash
php artisan make:controller MarcarTarefaConcluidaController --invokable
```

O `--invokable` gera a classe já com a assinatura certa — em vez de vários métodos (`index`, `show`, etc.), só existe `__invoke()`:

```php
class MarcarTarefaConcluidaController extends Controller
{
    public function __invoke(Tarefa $tarefa)
    {
        $tarefa->update(['concluida' => ! $tarefa->concluida]);

        return redirect()->route('tarefas.show', $tarefa);
    }
}
```

`__invoke` é um método mágico do PHP: quando uma classe o define, uma instância dessa classe pode ser chamada diretamente como se fosse uma função (`$controller($tarefa)`). É essa característica da linguagem, não uma convenção só do Laravel, que faz o roteamento funcionar sem precisar dizer qual método chamar — só existe um.

Registre a rota apontando direto para a classe, sem `::class, 'metodo'`:

```php
Route::patch('tarefas/{tarefa}/concluir', MarcarTarefaConcluidaController::class)->name('tarefas.concluir');
```

Repare que a rota ainda é RESTful no sentido amplo (usa `PATCH`, o verbo de atualização parcial, e recebe `{tarefa}` via route model binding, igual a qualquer método de `TarefaController`) — o que muda é só que ela não faz parte do conjunto fixo de 7 rotas do `Route::resource`, e por isso precisa ser declarada à parte.

**✅ Checkpoint:** `PATCH /tarefas/{id}/concluir` alterna o campo `concluida` da tarefa e redireciona de volta para a página de detalhes.

### Passo 7 — Singleton Controller: quando só existe UM registro

Até aqui, todo Controller construído neste tutorial lida com uma **coleção** de registros — vários posts, várias tarefas, cada um com seu próprio `id` na URL. Mas alguns recursos do sistema não são assim: as configurações do "Gerenciador de Tarefas" (por exemplo, quantas tarefas aparecem por página) não têm uma lista, nem um `id` — existe exatamente **uma** configuração, sempre a mesma, para o sistema inteiro. Esse tipo de recurso é chamado de **singleton** (mesmo termo do padrão de projeto: uma classe da qual só existe uma instância), e o Laravel reconhece essa convenção com seu próprio tipo de Controller.

Primeiro, o Model e a migration — a tabela de configurações já nasce com uma linha, em vez de esperar um primeiro cadastro:

```php
Schema::create('configuracoes', function (Blueprint $table) {
    $table->id();
    $table->unsignedTinyInteger('tarefas_por_pagina')->default(10);
    $table->timestamps();
});

DB::table('configuracoes')->insert(['tarefas_por_pagina' => 10, 'created_at' => now(), 'updated_at' => now()]);
```

```php
class Configuracao extends Model
{
    protected $table = 'configuracoes';
    protected $fillable = ['tarefas_por_pagina'];

    // Um Controller Singleton não recebe id nenhum vindo da URL — ele
    // precisa de um jeito de sempre encontrar "a" configuração sozinho.
    public static function atual(): self
    {
        return static::firstOrCreate([], ['tarefas_por_pagina' => 10]);
    }
}
```

`firstOrCreate([], [...])` busca a primeira linha da tabela e, se não existir nenhuma, cria uma com os valores padrão — uma segunda garantia de que "a" configuração sempre existe, mesmo que a migration nunca tivesse inserido a linha inicial.

Agora o Controller:

```bash
php artisan make:controller ConfiguracaoController --singleton
```

O `--singleton` gera só três métodos — `show`, `edit`, `update` — sem `index`, `create`, `store` nem `destroy`. Faz sentido: não existe "lista de configurações" para paginar, não existe "criar uma nova configuração" (a única já existe desde a migration), e não existe "excluir a configuração" (o sistema sempre precisa de uma).

```php
class ConfiguracaoController extends Controller
{
    public function show()
    {
        return view('configuracoes.show', ['configuracao' => Configuracao::atual()]);
    }

    public function edit()
    {
        return view('configuracoes.edit', ['configuracao' => Configuracao::atual()]);
    }

    public function update(Request $request)
    {
        $dados = $request->validate(['tarefas_por_pagina' => 'required|integer|min:1|max:100']);
        Configuracao::atual()->update($dados);

        return redirect()->route('configuracoes.show')->with('sucesso', 'Configurações salvas!');
    }
}
```

Repare na diferença central em relação a `TarefaController::show(Tarefa $tarefa)`: ali, o Laravel injeta a tarefa certa via route model binding, porque o `id` vem da URL (`/tarefas/3`). Aqui não há `id` nenhum na URL — é por isso que `show()`/`edit()`/`update()` não recebem nenhum parâmetro de Model, e o Controller precisa saber, sozinho, encontrar "a" configuração através de `Configuracao::atual()`.

Registre a rota com `Route::singleton`, o equivalente singular de `Route::resource`:

```php
Route::singleton('configuracoes', ConfiguracaoController::class);
```

```bash
php artisan route:list --name=configuracoes
```

```
GET|HEAD   configuracoes ................ configuracoes.show
PUT|PATCH  configuracoes ................ configuracoes.update
GET|HEAD   configuracoes/edit ........... configuracoes.edit
```

Três rotas, nenhuma com `{configuracoes}` — a ausência do parâmetro na URL é a assinatura visual de um recurso singleton, bem diferente das 7 rotas de `/tarefas/{tarefa}` geradas pelo `Route::resource` do Passo 5.

Para fechar o ciclo, use a configuração de verdade em vez de um número fixo — troque o `paginate(10)` que `TarefaController::index()` passou a usar no Passo 1 deste tópico (a linha de `$urgentes` logo abaixo continua igual, ela já lê da página atual via `getCollection()`):

```php
$tarefas = Tarefa::orderByDesc('prazo')->paginate(Configuracao::atual()->tarefas_por_pagina);
```

**✅ Checkpoint:** `GET /configuracoes` mostra o valor atual; editar e salvar em `/configuracoes/edit` muda o número de tarefas por página da listagem, sem passar por nenhuma URL com `id`.

> Se a tela de configurações no seu sistema real também precisasse de uma ação de "restaurar padrões" (recriar o registro do zero), o Laravel permite isso com `Route::singleton(...)->creatable()`, que acrescenta rotas de `store`/`destroy` a um singleton — este tutorial não usa, porque não há necessidade real de "criar" ou "apagar" a única configuração do sistema, só de editá-la.

### Resumo do que você construiu

```
✅ index/show usando route model binding, sem busca manual
✅ create/store com validação e padrão Post/Redirect/Get
✅ edit/update atualizando só os campos validados
✅ destroy com method spoofing (@method('DELETE')) mantendo rota RESTful
✅ Mapa completo dos 7 métodos convencionais e seus verbos HTTP
✅ Invokable Controller (--invokable) para uma ação isolada, sem recurso associado
✅ Singleton Controller (--singleton) para um recurso do qual só existe uma instância
✅ paginate() usando um valor configurável em vez de um número fixo no código
```

### Exercícios

1. **Autorização**: adicione um middleware garantindo que só o dono edite/exclua a tarefa.
2. **Flash de erro**: mostre uma mensagem se `update()` falhar.
3. **Soft deletes**: pesquise o trait `SoftDeletes` como alternativa a excluir permanentemente.
4. **Outro Invokable**: crie `DuplicarTarefaController`, uma ação de "duplicar" que cria uma cópia da tarefa com `concluida = false`.
5. **Singleton criatável**: pesquise `Route::singleton(...)->creatable()` e explique um cenário (fora deste projeto) onde faria sentido permitir recriar um recurso singleton.

### Perguntas de fixação

1. Por que `show(Tarefa $tarefa)` não precisa de `Tarefa::findOrFail($id)` explícito?
2. O que aconteceria se `destroy()` fosse acessível via link `<a href="...">`, em vez de um formulário POST com `@method('DELETE')`?
3. Por que `store()` usa `redirect()->route(...)` em vez de simplesmente devolver a view da listagem diretamente?
4. Por que `MarcarTarefaConcluidaController` não faz sentido como um oitavo método dentro de `TarefaController`, mesmo recebendo o mesmo tipo de parâmetro (`Tarefa $tarefa`) que `show`/`edit`/`update` já recebem?
5. Por que `ConfiguracaoController::show()` não pode usar route model binding do mesmo jeito que `TarefaController::show(Tarefa $tarefa)` usa?

---

## 10. Views e interação com Controller

**Objetivo:** aprofundar a comunicação Controller → View — layouts, componentes Blade e passagem de dados.

Laravel 13.21 / Blade. Continuação do "Gerenciador de Tarefas".

### Pré-requisitos

`TarefaController` com os 7 métodos RESTful já implementados.

**✅ Checkpoint:** `/tarefas` mostra a lista de tarefas.

### Passo 1 — crie um layout base reutilizável

```blade
<!-- resources/views/layouts/app.blade.php -->
<!DOCTYPE html>
<html lang="pt-BR">
<head><title>@yield('titulo', 'Gerenciador de Tarefas')</title></head>
<body>
    @if(session('sucesso'))
        <div class="alerta-sucesso">{{ session('sucesso') }}</div>
    @endif

    @yield('conteudo')
</body>
</html>
```

`@yield('conteudo')` marca onde cada página injeta seu próprio HTML; `session('sucesso')` lê a mensagem flash definida no Controller com `->with('sucesso', ...)`.

**✅ Checkpoint:** o layout compila sem erro (mesmo sem nenhuma view usando ele ainda).

### Passo 2 — estenda o layout numa view filha

```blade
<!-- resources/views/tarefas/index.blade.php -->
@extends('layouts.app')

@section('titulo', 'Minhas Tarefas')

@section('conteudo')
    <h1>Tarefas</h1>
    @foreach($tarefas as $tarefa)
        <p>{{ $tarefa->titulo }}</p>
    @endforeach
@endsection
```

`@extends` + `@section`/`@endsection` é o mecanismo de herança de templates do Blade — evita repetir `<html>`, `<head>` e a barra de mensagens em toda página.

**✅ Checkpoint:** `/tarefas` renderiza dentro do layout, com o título correto na aba do navegador.

### Passo 3 — extraia um componente Blade reutilizável

```blade
<!-- resources/views/components/tarefa-card.blade.php -->
<div class="tarefa-card {{ $tarefa->isUrgente() ? 'urgente' : '' }}">
    <h3>{{ $tarefa->titulo }}</h3>
    <span>{{ $tarefa->prazo->format('d/m/Y') }}</span>
</div>

<!-- em index.blade.php -->
@foreach($tarefas as $tarefa)
    <x-tarefa-card :tarefa="$tarefa" />
@endforeach
```

`<x-tarefa-card :tarefa="$tarefa" />` é a sintaxe de **componente Blade** — reutilizável em qualquer view (lista, busca, dashboard) sem duplicar o HTML do card.

**✅ Checkpoint:** a lista de tarefas usa o componente, e tarefas urgentes aparecem com a classe CSS diferenciada.

### Passo 4 — passe dados complexos do Controller para múltiplas views com View Composer

```php
// app/Providers/AppServiceProvider.php
use App\Models\Tarefa;
use Illuminate\Support\Facades\View;

public function boot(): void
{
    View::composer('layouts.app', function ($view) {
        $view->with(
            'totalUrgentes',
            Tarefa::where('concluida', false)
                ->whereNotNull('prazo')
                ->where('prazo', '<=', now()->addDays(2))
                ->count()
        );
    });
}
```

```blade
<!-- disponível automaticamente em qualquer view que use layouts.app -->
<span>Urgentes: {{ $totalUrgentes }}</span>
```

Um **View Composer** injeta dados automaticamente sempre que uma view específica é renderizada — evita repetir a mesma consulta em todo Controller que precisa mostrar esse contador no cabeçalho. A consulta precisa dos três filtros: `where('concluida', false)` para não contar tarefa já resolvida como urgente; `whereNotNull('prazo')` porque `prazo` é `nullable()` (Tópico 6) e comparar `NULL <= data` no banco nunca dá verdadeiro, mas deixar a condição fora não expressa a intenção; e só então `where('prazo', '<=', now()->addDays(2))`, o próprio critério de urgência de `Tarefa::isUrgente()` — a mesma regra do Model, só que como consulta SQL em vez de checagem em memória, porque contar "urgentes no sistema todo" via `isUrgente()` exigiria carregar toda a tabela para a aplicação primeiro.

**✅ Checkpoint:** o contador de urgentes aparece em qualquer página que estenda `layouts.app`, sem o Controller calcular isso explicitamente.

### Passo 5 — interaja com o Controller via formulário com validação exibida na View

```blade
<!-- resources/views/tarefas/create.blade.php -->
@extends('layouts.app')

@section('titulo', 'Nova tarefa')

@section('conteudo')
    <h1>Nova tarefa</h1>

    <form method="POST" action="{{ route('tarefas.store') }}">
        @csrf
        <input type="text" name="titulo" value="{{ old('titulo') }}">
        @error('titulo') <span class="erro">{{ $message }}</span> @enderror

        <input type="date" name="prazo" value="{{ old('prazo') }}">
        @error('prazo') <span class="erro">{{ $message }}</span> @enderror

        <select name="tipo">
            <option value="rotina" @selected(old('tipo') === 'rotina')>Rotina</option>
            <option value="urgente" @selected(old('tipo') === 'urgente')>Urgente</option>
        </select>
        @error('tipo') <span class="erro">{{ $message }}</span> @enderror

        <button type="submit">Criar</button>
    </form>
@endsection
```

`old('titulo')` repopula o campo com o valor digitado se a validação falhar (evita o usuário perder o que já escreveu); `@error('titulo')` mostra a mensagem de erro daquele campo específico — ambos alimentados automaticamente pelo Laravel quando `$request->validate()` falha no Controller. Os campos `prazo` e `tipo` completam o que `store()` (Tópico 9) já valida: sem eles no formulário, o `required` de `prazo` e o `in:urgente,rotina` de `tipo` rejeitariam **todo** envio, porque as chaves nem chegariam na requisição. `@selected(...)` é a diretiva Blade que marca a `<option>` certa depois de um envio inválido — o mesmo papel do `old()`, só que para `<select>`.

**✅ Checkpoint:** submeter o formulário sem título mostra a mensagem de erro e mantém os outros campos preenchidos, inclusive a opção de tipo já selecionada.

### Resumo do que você construiu

```
✅ Layout base reutilizável com @yield e mensagens flash
✅ Views filhas usando @extends/@section
✅ Componente Blade <x-tarefa-card /> reutilizável
✅ View Composer injetando dados automaticamente em múltiplas views
✅ Formulário com old() e @error integrados à validação do Controller
```

### Exercícios

1. **Slot nomeado**: adicione um slot opcional de "ações extras" ao componente `tarefa-card`.
2. **Layout de erro**: crie um layout dedicado para páginas 403/404.
3. **Componente de paginação customizado**: substitua o padrão do Laravel por um estilizado.

### Perguntas de fixação

1. Qual a diferença prática entre passar `$totalUrgentes` via `compact()` em cada Controller e usar um View Composer?
2. Por que `<x-tarefa-card :tarefa="$tarefa" />` usa dois-pontos antes de `tarefa`, e o que aconteceria sem eles?
3. Onde `old('titulo')` busca o valor a repopular, e por que ele só aparece preenchido depois de uma validação que falhou?

---

## 11. Revisão de MVC + helpers

**Objetivo:** revisar o fluxo MVC completo combinando Helper Functions e Blade directives customizadas — consolidando o semestre antes da integração final com DAO.

Laravel 13.21. Continuação do "Gerenciador de Tarefas".

### Pré-requisitos

`TarefaController` completo, views com layout e componentes Blade.

**✅ Checkpoint:** `/tarefas` funciona com o layout e componentes das aulas anteriores.

### Passo 1 — revise o fluxo MVC completo numa única passada

```
Requisição → Route → Controller → Model (regra de negócio) → View (Blade) → Resposta HTML
```

Percorra mentalmente uma requisição real: `GET /tarefas/1` bate na rota, chama `TarefaController::show`, que usa route model binding para buscar a `Tarefa` (Model), e passa para `tarefas/show.blade.php` (View).

**✅ Checkpoint:** você narra esse fluxo completo para uma rota do seu próprio projeto.

### Passo 2 — crie uma Helper Function customizada

```php
// app/helpers.php
if (! function_exists('formatar_prazo')) {
    function formatar_prazo($data): string
    {
        if (! $data) {
            return 'sem prazo';
        }

        return $data->isToday() ? 'Hoje' : $data->format('d/m/Y');
    }
}

if (! function_exists('tempo_restante')) {
    function tempo_restante($prazo): string
    {
        if (! $prazo) {
            return 'sem prazo';
        }

        $dias = now()->diffInDays($prazo, false);

        if ($dias < 0) {
            return 'atrasada há '.abs($dias).' dia(s)';
        }

        return "faltam {$dias} dia(s)";
    }
}
```

```json
// composer.json
"autoload": {
    "files": ["app/helpers.php"]
}
```

```bash
composer dump-autoload
```

Uma Helper Function fica disponível **globalmente**, sem precisar importar nenhuma classe — útil para pequenas formatações usadas em várias views, algo que não justifica virar um método de Model. As duas funções guardam a mesma cautela do `isUrgente()` do Tópico 8: `prazo` é `nullable()`, então as duas começam checando `! $data`/`! $prazo` antes de chamar qualquer método de data — sem essa guarda, uma tarefa sem prazo derrubaria a página inteira, não só o card daquela tarefa. `tempo_restante()` também usa `diffInDays($prazo, false)` com sinal, pelo mesmo motivo do Tópico 8: precisa distinguir "faltam 3 dias" de "atrasada há 3 dias", e o valor absoluto apagaria essa diferença.

**✅ Checkpoint:** `{{ formatar_prazo($tarefa->prazo) }}` e `{{ tempo_restante($tarefa->prazo) }}` funcionam em qualquer view sem `use` algum; para uma tarefa sem prazo, os dois devolvem "sem prazo" em vez de erro.

### Passo 3 — crie uma Blade directive customizada

```php
// AppServiceProvider::boot()
Blade::directive('urgente', function ($tarefa) {
    return "<?php if(($tarefa)->isUrgente()): ?>";
});

Blade::directive('endurgente', function () {
    return '<?php endif; ?>';
});
```

```blade
@urgente($tarefa)
    <span class="badge">URGENTE</span>
@endurgente
```

Diretivas customizadas deixam o Blade mais legível para regras usadas com frequência — `@urgente(...)` comunica a intenção melhor que um `@if($tarefa->isUrgente())` genérico espalhado pelo código.

**✅ Checkpoint:** a tag "URGENTE" aparece só em tarefas cujo `isUrgente()` retorna `true`.

Com as duas peças no ar, volte a `resources/views/components/tarefa-card.blade.php` (Tópico 10, Passo 3) e troque o `{{ $tarefa->prazo->format('d/m/Y') }}` cru pelas funções que você acabou de criar:

```blade
<div class="tarefa-card {{ $tarefa->isUrgente() ? 'urgente' : '' }}">
    <h3>{{ $tarefa->titulo }}</h3>
    <span>{{ formatar_prazo($tarefa->prazo) }} ({{ tempo_restante($tarefa->prazo) }})</span>

    @urgente($tarefa)
        <span class="badge">URGENTE</span>
    @endurgente
</div>
```

Esta é a versão final do componente, a mesma da implementação de referência: `formatar_prazo()` substitui o `format('d/m/Y')` que quebraria numa tarefa sem prazo, `tempo_restante()` acrescenta o "faltam N dias"/"atrasada há N dias" ao lado da data, e `@urgente`/`@endurgente` substitui a classe condicional por uma diretiva com nome — o card inteiro passa a usar as três peças deste tópico.

**✅ Checkpoint:** um card de tarefa sem prazo mostra "sem prazo" duas vezes (na data e no tempo restante), sem gerar erro 500; um card de tarefa atrasada mostra "atrasada há N dia(s)".

### Passo 4 — revise a diferença entre Helper e Model method

| Coloque em... | Quando |
|---|---|
| Método do Model (`$tarefa->isUrgente()`) | A regra depende dos dados **daquele objeto específico** |
| Helper Function (`formatar_prazo(...)`) | Utilitário genérico, sem estado, útil em qualquer lugar |
| Blade directive | Sintaxe recorrente que melhora legibilidade da View |

**✅ Checkpoint:** você classifica corretamente 3 exemplos do seu próprio projeto usando essa tabela.

### Passo 5 — faça um checklist de revisão do MVC do projeto inteiro

- [ ] Nenhuma view contém cálculo de negócio (só chamadas a métodos do Model)
- [ ] Nenhum Controller contém lógica de formatação de dados (isso é Helper ou View)
- [ ] Toda validação usa `$request->validate()`, nunca `if` manual espalhado
- [ ] Toda rota de escrita usa route model binding, não `find($id)` manual

**✅ Checkpoint:** você audita seu próprio `TarefaController` e views contra essa checklist.

**Verificado na implementação de referência**: `formatar_prazo()` e `tempo_restante()` estão em `app/helpers.php`, registradas via `"files": ["app/helpers.php"]` no `composer.json` do `task-manager` real; rodando `composer dump-autoload` e depois `php artisan tinker --execute="echo formatar_prazo(...)"`, a função respondeu corretamente sem nenhum `use` — confirmando que o autoload de arquivo (e não de classe) funciona como descrito.

### Resumo do que você construiu

```
✅ Revisão do fluxo completo Route → Controller → Model → View
✅ Helper Functions globais (formatar_prazo, tempo_restante) sem precisar de import, com guarda contra prazo nulo
✅ tarefa-card.blade.php na versão final, usando as duas helpers e a directive @urgente
✅ Blade directive customizada (@urgente/@endurgente)
✅ Critério claro entre Model method, Helper e Blade directive
✅ Checklist de auditoria MVC aplicado ao próprio projeto
```

### Exercícios

1. **Helper adicional**: crie `dias_desde_criacao($tarefa)`, retornando há quantos dias a tarefa foi criada (`created_at`).
2. **Directive condicional composta**: crie `@atrasada($tarefa)` para tarefas vencidas, reaproveitando `isAtrasada()` (Tópico 8).
3. **Auditoria real**: rode o checklist do Passo 5 no seu projeto e corrija o que encontrar.

### Perguntas de fixação

1. Por que `formatar_prazo()` é uma Helper Function e não um método do Model `Tarefa`?
2. O que exatamente `"files": ["app/helpers.php"]` no `composer.json` resolve, e o que aconteceria sem essa entrada (e sem rodar `composer dump-autoload`)?
3. Segundo a tabela do Passo 4, onde deveria morar uma regra que calcula "quantos dias faltam" para o prazo de **uma** tarefa específica?

---

## 12. Integração DAO + MVC

**Objetivo:** unir o padrão DAO (visto no Tópico 4) ao MVC — o Controller passa a depender só da interface, nunca do Eloquent diretamente.

Laravel 13.21. Continuação do "Gerenciador de Tarefas".

### Pré-requisitos

`TarefaController` funcionando com Eloquent direto (`Tarefa::create`, `Tarefa::find`, etc).

**✅ Checkpoint:** `/tarefas` funciona.

### Passo 1 — recrie a interface TarefaDAOInterface

```php
<?php
namespace App\DAO;

use App\Models\Tarefa;
use Illuminate\Database\Eloquent\Collection;

interface TarefaDAOInterface
{
    public function todas(): Collection;
    public function porId(int $id): ?Tarefa;
    public function criar(array $dados): Tarefa;
    public function atualizar(Tarefa $tarefa, array $dados): Tarefa;
    public function remover(Tarefa $tarefa): void;
}
```

**✅ Checkpoint:** a interface compila.

### Passo 2 — implemente TarefaDAO

```php
<?php
namespace App\DAO;

use App\Models\Tarefa;
use Illuminate\Database\Eloquent\Collection;

class TarefaDAO implements TarefaDAOInterface
{
    public function todas(): Collection
    {
        return Tarefa::orderByDesc('prazo')->get();
    }

    public function porId(int $id): ?Tarefa
    {
        return Tarefa::find($id);
    }

    public function criar(array $dados): Tarefa
    {
        return Tarefa::create($dados);
    }

    public function atualizar(Tarefa $tarefa, array $dados): Tarefa
    {
        $tarefa->update($dados);
        return $tarefa;
    }

    public function remover(Tarefa $tarefa): void
    {
        $tarefa->delete();
    }
}
```

**✅ Checkpoint:** a classe compila.

### Passo 3 — registre o binding e injete no Controller

```php
// AppServiceProvider::register()
$this->app->bind(TarefaDAOInterface::class, TarefaDAO::class);

class TarefaController extends Controller
{
    public function __construct(private TarefaDAOInterface $dao) {}

    public function index()
    {
        $tarefas = Tarefa::orderByDesc('prazo')->paginate(Configuracao::atual()->tarefas_por_pagina);
        $urgentes = $tarefas->getCollection()->filter->isUrgente();

        return view('tarefas.index', compact('tarefas', 'urgentes'));
    }

    public function store(Request $request)
    {
        $dados = $request->validate([
            'titulo' => 'required|string|max:150',
            'prazo' => 'required|date|after:today',
            'tipo' => 'required|in:urgente,rotina',
        ]);

        $this->dao->criar($dados);
        return redirect()->route('tarefas.index')->with('sucesso', 'Tarefa criada!');
    }
}
```

Repare que `store()` não menciona `Tarefa::` nenhuma vez — toda a escrita passa pela interface, exatamente o mesmo princípio da aula "Padrão DAO". `index()` é a exceção, de propósito: `todas()` da interface devolve uma `Collection` simples, sem paginação nem conhecimento da configuração de itens por página (Tópico 9) — encaixar isso na interface exigiria um método tipo `paginadas(int $porPagina)`, só para uma tela. A implementação de referência opta por deixar a **leitura paginada** direto no Controller via Eloquent, e reservar o DAO para as operações de **escrita** (`criar`, `atualizar`, `remover`), onde a troca de implementação (Passo 5) realmente importa — por exemplo, para testar `store`/`update`/`destroy` sem banco. Isso é uma escolha real de engenharia, não uma inconsistência: nem toda leitura precisa passar pela mesma abstração que as escritas.

**✅ Checkpoint:** `/tarefas` e a criação de tarefas continuam funcionando, `store`/`update`/`destroy` inteiramente via DAO.

### Passo 4 — complete os métodos restantes usando o DAO

```php
public function update(Request $request, Tarefa $tarefa)
{
    $dados = $request->validate([
        'titulo' => 'required|string|max:150',
        'prazo' => 'required|date',
        'tipo' => 'required|in:urgente,rotina',
    ]);

    $this->dao->atualizar($tarefa, $dados);
    return redirect()->route('tarefas.show', $tarefa);
}

public function destroy(Tarefa $tarefa)
{
    $this->dao->remover($tarefa);
    return redirect()->route('tarefas.index')->with('sucesso', 'Tarefa removida.');
}
```

Note que `update`/`destroy` ainda usam route model binding (`Tarefa $tarefa`) para localizar o registro pela URL — o DAO entra depois, para a operação de escrita em si.

**✅ Checkpoint:** editar e excluir tarefas continuam funcionando, agora via DAO.

### Passo 5 — prove o valor do DAO trocando a implementação num teste

```php
class TarefaDAOEmMemoria implements TarefaDAOInterface
{
    private array $tarefas = [];
    private int $proximoId = 1;

    public function todas(): Collection { return collect($this->tarefas); }
    public function porId(int $id): ?Tarefa { return $this->tarefas[$id] ?? null; }
    public function criar(array $dados): Tarefa {
        $tarefa = new Tarefa($dados);
        $tarefa->id = $this->proximoId++;
        $this->tarefas[$tarefa->id] = $tarefa;
        return $tarefa;
    }
    public function atualizar(Tarefa $tarefa, array $dados): Tarefa {
        $tarefa->fill($dados);
        return $tarefa;
    }
    public function remover(Tarefa $tarefa): void {
        unset($this->tarefas[$tarefa->id]);
    }
}

// Num teste:
$this->app->bind(TarefaDAOInterface::class, TarefaDAOEmMemoria::class);
```

Trocar o binding faz `store`, `update` e `destroy` rodarem **sem tocar no banco de dados** — a prova concreta de que a separação via interface funcionou nesses três métodos: o Controller nunca soube que a implementação mudou. `index()` é a exceção que o Passo 3 já assinalou: como ele lê direto de `Tarefa::` (não passa pelo DAO), trocar este binding não muda o que `/tarefas` lista — só o que `store`/`update`/`destroy` fazem com os dados.

**Nota sobre o que fica no projeto:** assim como `PostDAOEmMemoria` no Tópico 4, `TarefaDAOEmMemoria` é um exercício para você construir e testar localmente com os comandos acima — ele não está commitado em `app/DAO/` na implementação de referência, que só mantém `TarefaDAO` (a versão com Eloquent).

**✅ Checkpoint:** você entende por que isso torna testes de Controller muito mais rápidos (sem I/O de banco).

### Resumo do que você construiu

```
✅ TarefaDAOInterface e TarefaDAO cobrindo criar/atualizar/remover/buscar por id
✅ Binding registrado e injetado via construtor
✅ store/update/destroy operando inteiramente através do DAO — index() continua lendo direto, por escolha consciente
✅ Implementação alternativa em memória (exercício local), trocável sem alterar store/update/destroy
✅ Entendimento de por que isso acelera testes automatizados
```

### Exercícios

1. **Teste com TarefaDAOEmMemoria**: escreva um teste Pest/PHPUnit usando o binding trocado — use `Tarefa::factory()->create(...)` (seção "Factories", logo após o Tópico 6) para montar o cenário sem digitar cada campo obrigatório na mão.
2. **Cache**: crie `TarefaDAOComCache` decorando `TarefaDAO`.
3. **Métodos de consulta extra**: adicione `urgentes()` à interface e implemente nas duas classes.

### Perguntas de fixação

1. Depois desta integração, quantas linhas do `TarefaController` mencionam `Tarefa::` diretamente, e em qual método? Por que essa é a exceção consciente, e não um descuido?
2. Por que `update`/`destroy` continuam usando route model binding mesmo depois de introduzir o DAO?
3. O que precisaria mudar no `TarefaController` para trocar `TarefaDAO` por `TarefaDAOEmMemoria` em `store`/`update`/`destroy`? E o que **não** mudaria, mesmo com essa troca, em `index()`?

---

## 13. Interatividade sem reload — Livewire

**Objetivo:** adicionar interatividade reativa ao `task-manager` sem escrever JavaScript manual, consumindo o mesmo `TarefaDAOInterface` das aulas anteriores.

> Nota honesta sobre esta seção: o currículo original desta disciplina previa esta aula como "AJAX (Fetch API)" — chamar rotas com `fetch()` manualmente, tratar o JSON à mão e atualizar o DOM na unha. Na prática, a aula real ministrada substituiu essa abordagem por **Livewire**, e este tutorial segue o que foi de fato ensinado. Por isso o tópico foi renomeado. A comparação entre as duas abordagens é o assunto do primeiro exercício, no final.

Laravel 13.21 (PHP 8.3+) com Livewire. Continuação do "Gerenciador de Tarefas".

### Pré-requisitos

`TarefaController` funcionando com integração DAO (tópico anterior).

**✅ Checkpoint:** `/tarefas` mostra a lista normalmente.

### Passo 1 — instale o Livewire

```bash
composer require livewire/livewire
```

Confira em `resources/views/layouts/app.blade.php` que as diretivas do Livewire estão no lugar certo (o pacote não exige build step de Vite/Node — funciona direto sobre Blade):

```blade
<head>
    @livewireStyles
</head>
<body>
    ...
    @livewireScripts
</body>
```

**✅ Checkpoint:** `composer show livewire/livewire` lista a versão instalada sem erro.

> **Nota de versão, confirmada na implementação de referência:** no momento em que este material foi verificado, `composer require livewire/livewire` sem fixar versão instalou o Livewire 4, que mudou a estrutura padrão de componentes gerados (arquivos únicos com prefixo `⚡`, em vez da dupla classe+view usada neste tutorial) e não tem mais o comando `livewire:list`. O código e os comandos abaixo foram escritos e verificados contra **Livewire 3.x**, que é o que a implementação de referência usa de fato (`composer require "livewire/livewire:^3.0"`). Se seu `composer require livewire/livewire` instalar a versão 4, fixe a versão 3 explicitamente antes de continuar, ou adapte a localização dos arquivos conforme o gerador indicar.

### Passo 2 — entenda o que muda em relação ao Fetch API puro

Antes (Fetch API manual): o JavaScript chamava uma rota `/api/...`, tratava o JSON à mão e atualizava o DOM manualmente, repetindo o header `X-CSRF-TOKEN` em cada chamada. Com Livewire: um **componente PHP** guarda o estado da tela e re-renderiza automaticamente sua própria porção de HTML a cada interação — o Livewire cuida da chamada AJAX interna, do CSRF e da atualização do DOM por baixo dos panos.

**✅ Checkpoint:** você explica, com suas palavras, por que Livewire elimina a necessidade de JavaScript manual para esse tipo de interação.

### Passo 3 — crie o componente Livewire TarefaLista

```bash
php artisan make:livewire TarefaLista
```

Isso gera dois arquivos: `app/Livewire/TarefaLista.php` (classe) e `resources/views/livewire/tarefa-lista.blade.php` (view). Em `app/Livewire/TarefaLista.php`:

```php
<?php

namespace App\Livewire;

use App\DAO\TarefaDAOInterface;
use Livewire\Component;

class TarefaLista extends Component
{
    public function render()
    {
        $dao = app(TarefaDAOInterface::class);

        return view('livewire.tarefa-lista', [
            'tarefas' => $dao->todas(),
        ]);
    }
}
```

`app(TarefaDAOInterface::class)` resolve a mesma interface do DAO das aulas anteriores pelo Service Container — o componente Livewire continua sem conhecer o Eloquent diretamente.

**✅ Checkpoint:** os arquivos `app/Livewire/TarefaLista.php` e `resources/views/livewire/tarefa-lista.blade.php` existem, e a classe está listada em `app/Livewire/` (`ls app/Livewire`). *(O comando `php artisan livewire:list`, citado em versões antigas de material sobre Livewire, não existe nas versões atuais do pacote — verifique os arquivos diretamente, como acima.)*

### Passo 4 — renderize a lista e marque tarefas como concluídas sem JavaScript

Em `resources/views/livewire/tarefa-lista.blade.php`:

```blade
<div>
    @foreach($tarefas as $tarefa)
        <div class="tarefa-card {{ $tarefa->concluida ? 'concluida' : '' }}">
            <span>{{ $tarefa->titulo }}</span>

            @unless($tarefa->concluida)
                <button wire:click="concluir({{ $tarefa->id }})">
                    Concluir
                </button>
            @endunless

            <button wire:click="remover({{ $tarefa->id }})">Excluir</button>
        </div>
    @endforeach
</div>
```

No componente:

```php
public function concluir(int $tarefaId): void
{
    $tarefa = app(TarefaDAOInterface::class)->porId($tarefaId);
    $tarefa->update(['concluida' => true]);
}

public function remover(int $tarefaId): void
{
    $tarefa = app(TarefaDAOInterface::class)->porId($tarefaId);
    app(TarefaDAOInterface::class)->remover($tarefa);
}
```

`wire:click="concluir({{ $tarefa->id }})"` dispara uma chamada ao servidor que executa o método PHP `concluir()` e **automaticamente** re-renderiza o componente com o novo estado — nenhum `fetch`, nenhum header CSRF manual, nenhuma manipulação de DOM feita por você. `remover()` segue exatamente o mesmo padrão, chamando o `remover()` do próprio `TarefaDAOInterface` (Tópico 12) em vez de `$tarefa->delete()` direto — o componente Livewire nunca fala com o Eloquent, igual a qualquer outro consumidor do DAO. Acrescente o componente **dentro** de `resources/views/tarefas/index.blade.php` (Tópico 10) — sem substituir o que já está lá, só somando uma linha ao final de `@section('conteudo')`:

```blade
    @livewire('tarefa-lista')
```

**✅ Checkpoint:** clicar em "Concluir" atualiza o card visualmente sem reload de página, sem nenhum JavaScript escrito por você.

**Verificado na implementação de referência**: além de renderizar a página e conferir visualmente, o botão "Concluir" foi acionado via requisição HTTP real diretamente contra o endpoint interno `/livewire/update` do `task-manager` (o mesmo endpoint que o JavaScript do Livewire chama por trás dos panos) — a resposta trouxe o HTML já atualizado com a classe `concluida`, e uma consulta direta ao MySQL confirmou `concluida = 1` na linha correspondente. Ou seja: a ação clicada no navegador de fato percorre `wire:click` → método PHP do componente → `TarefaDAOInterface` → banco de dados, de ponta a ponta.

### Passo 5 — adicione feedback de carregamento com wire:loading

```blade
<button wire:click="concluir({{ $tarefa->id }})"
        wire:loading.attr="disabled"
        wire:target="concluir({{ $tarefa->id }})">
    <span wire:loading.remove wire:target="concluir({{ $tarefa->id }})">Concluir</span>
    <span wire:loading wire:target="concluir({{ $tarefa->id }})">Salvando...</span>
</button>
```

`wire:loading` mostra/esconde elementos automaticamente enquanto uma requisição Livewire está em andamento; `wire:target` restringe esse comportamento à ação específica daquele botão, evitando que **todos** os cards da lista entrem em estado de carregamento ao clicar em apenas um.

**✅ Checkpoint:** o botão mostra "Salvando..." brevemente e volta ao normal após a resposta do servidor.

### Passo 6 — crie um formulário reativo de nova tarefa com wire:model

```bash
php artisan make:livewire TarefaForm
```

```php
<?php

namespace App\Livewire;

use App\DAO\TarefaDAOInterface;
use Livewire\Attributes\Validate;
use Livewire\Component;

class TarefaForm extends Component
{
    #[Validate('required|string|max:150')]
    public string $titulo = '';

    #[Validate('required|date|after:today')]
    public string $prazo = '';

    public function salvar(TarefaDAOInterface $dao): void
    {
        $this->validate();

        $dao->criar(['titulo' => $this->titulo, 'prazo' => $this->prazo, 'tipo' => 'rotina']);

        $this->reset(['titulo', 'prazo']);
        $this->dispatch('tarefa-criada');
    }

    public function render()
    {
        return view('livewire.tarefa-form');
    }
}
```

```blade
<!-- resources/views/livewire/tarefa-form.blade.php -->
<form wire:submit="salvar">
    <input type="text" wire:model="titulo" placeholder="Título">
    @error('titulo') <span class="erro">{{ $message }}</span> @enderror

    <input type="date" wire:model="prazo">
    @error('prazo') <span class="erro">{{ $message }}</span> @enderror

    <button type="submit">Criar tarefa</button>
</form>
```

`wire:model` sincroniza o valor do input com a propriedade pública do componente a cada interação; `#[Validate(...)]` é um **atributo PHP 8** que declara a regra de validação direto na propriedade, sem precisar chamar `$request->validate()` manualmente — o Livewire injeta o `TarefaDAOInterface` no método `salvar()` da mesma forma que o Laravel injeta dependências em Controllers. Este formulário reativo não pergunta o `tipo` — ele sempre cria como `'rotina'`, deixando a criação de tarefas `urgente` (Tópico 9) para o formulário tradicional em `tarefas/create.blade.php`, que já tem o `<select>`. É uma limitação intencional deste componente, não um campo esquecido.

**✅ Checkpoint:** submeter o formulário sem título mostra o erro de validação sem recarregar a página; preenchido corretamente, cria a tarefa e limpa os campos.

### Passo 7 — monte a versão final de tarefas/index.blade.php

Some `@livewire('tarefa-form')` ao mesmo arquivo, do mesmo jeito aditivo do Passo 4. Juntando tudo que os Tópicos 9, 10 e 13 acrescentaram nesta mesma view — a listagem paginada, o contador de urgentes, o link para o formulário tradicional e os dois componentes Livewire — o arquivo final é este:

```blade
<!-- resources/views/tarefas/index.blade.php -->
@extends('layouts.app')

@section('titulo', 'Minhas Tarefas')

@section('conteudo')
    <h1>Tarefas</h1>
    <p>Urgentes nesta página: {{ $urgentes->count() }}</p>

    <a href="{{ route('tarefas.create') }}">Nova tarefa (formulário tradicional)</a>

    @foreach($tarefas as $tarefa)
        <x-tarefa-card :tarefa="$tarefa" />
        <a href="{{ route('tarefas.show', $tarefa) }}">Ver</a>
    @endforeach

    {{ $tarefas->links() }}

    <hr>

    <h2>Interatividade sem reload (Livewire)</h2>
    @livewire('tarefa-form')
    @livewire('tarefa-lista')
@endsection
```

`{{ $tarefas->links() }}` renderiza os links de paginação do `LengthAwarePaginator` (Tópico 9) — sem essa linha, `paginate()` continuaria limitando a lista, só que sem nenhum jeito de navegar para a página seguinte. Note as duas formas de criar uma tarefa convivendo na mesma tela: o link "Nova tarefa (formulário tradicional)" leva à rota `tarefas.create` de página inteira (única com o `<select>` de `tipo`), enquanto `@livewire('tarefa-form')` cria por baixo, sem reload, sempre como `rotina`.

**✅ Checkpoint:** `/tarefas` mostra, na mesma tela: o contador de urgentes da página atual, os links de paginação, o formulário reativo do Livewire e a lista com os botões "Concluir"/"Excluir".

### Resumo do que você construiu

```
✅ Livewire instalado e diretivas registradas no layout
✅ Componente TarefaLista renderizando dados via DAO, sem JavaScript manual
✅ wire:click executando concluir()/remover() diretamente a partir do clique do usuário, os dois via TarefaDAOInterface
✅ wire:loading/wire:target dando feedback visual durante requisições
✅ Formulário reativo com wire:model, wire:submit e #[Validate] (PHP 8 Attribute), criando sempre tipo=rotina
```

### Exercícios

1. **Confirmação antes de excluir**: use `wire:click` com `wire:confirm="Excluir esta tarefa?"` no botão "Excluir" do `TarefaLista`.
2. **Busca em tempo real**: adicione uma propriedade `$busca` com `wire:model.live` filtrando a lista a cada tecla digitada.
3. **Evento entre componentes**: use `dispatch()` / `#[On(...)]` para que `TarefaForm` avise `TarefaLista` a recarregar após criar uma tarefa.
4. **Comparação**: pesquise como o mesmo fluxo seria implementado com Fetch API puro (a abordagem original do currículo) ou com Inertia.js + Vue/React, e liste 2 diferenças de abordagem em relação ao Livewire.

### Perguntas de fixação

1. O que, exatamente, substitui o `fetch()` manual quando você escreve `wire:click="concluir(1)"`?
2. Por que `wire:target` é necessário ao lado de `wire:loading` quando existem vários botões "Concluir" na mesma página?
3. `TarefaForm::salvar()` recebe `TarefaDAOInterface $dao` como parâmetro do método, não no construtor. Isso muda o princípio de injeção de dependências visto no Tópico 4?

---

## 14. APIs + Web Services

**Objetivo:** expor uma API JSON completa a partir do mesmo Model, complementando a interface web já existente.

Laravel 13.21 / API Resources. Continuação do "Gerenciador de Tarefas".

### Pré-requisitos

`TarefaController` (web) funcionando via DAO.

**✅ Checkpoint:** `/tarefas` mostra a lista normalmente.

### Passo 1 — crie um Controller de API dedicado

```bash
php artisan make:controller Api/TarefaController --api
```

```php
Route::apiResource('tarefas', \App\Http\Controllers\Api\TarefaController::class);
```

`--api` gera um controller sem `create`/`edit` (rotas que só fazem sentido para views HTML) — só os 5 métodos que fazem sentido numa API JSON. `apiResource` registra as rotas correspondentes automaticamente. Este é o quarto tipo de Controller que o Laravel reconhece por convenção, ao lado do Resource completo (Tópico 8), do Invokable e do Singleton (ambos no Tópico 9) — cada um recorta o mesmo conjunto de 7 métodos convencionais de um jeito diferente, dependendo do formato do recurso e de quem consome a rota.

**✅ Checkpoint:** `php artisan route:list --name=api` mostra 5 rotas para tarefas.

### Passo 2 — implemente index/show retornando JSON

```php
namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\DAO\TarefaDAOInterface;
use App\Models\Tarefa;

class TarefaController extends Controller
{
    public function __construct(private TarefaDAOInterface $dao) {}

    public function index()
    {
        return $this->dao->todas();
    }

    public function show(Tarefa $tarefa)
    {
        return $tarefa;
    }
}
```

Retornar um Model/Collection diretamente do Controller faz o Laravel serializar automaticamente para JSON — o mesmo `TarefaDAOInterface` das aulas anteriores é reutilizado aqui, sem duplicar lógica de acesso a dados.

**✅ Checkpoint:** `GET /api/tarefas` retorna um array JSON de tarefas.

### Passo 3 — formate a resposta com um API Resource

```bash
php artisan make:resource TarefaResource
```

```php
use Illuminate\Http\Request;
use Illuminate\Http\Resources\Json\JsonResource;

class TarefaResource extends JsonResource
{
    public function toArray(Request $request): array
    {
        return [
            'id' => $this->id,
            'titulo' => $this->titulo,
            'prazo' => $this->prazo?->format('Y-m-d'),
            'concluida' => $this->concluida,
            'urgente' => $this->isUrgente(),
        ];
    }
}

public function index()
{
    return TarefaResource::collection($this->dao->todas());
}
```

Um API Resource controla exatamente **quais campos** vão para o JSON e em que formato — evita expor colunas internas do banco (ex: `updated_at` bruto) e formata datas de forma consistente. `$this->prazo?->format('Y-m-d')` usa o operador de encadeamento nulo (`?->`): como `prazo` é `nullable()` desde o Tópico 6, uma tarefa sem prazo devolveria `null` direto, em vez de derrubar a resposta inteira com um erro tentando chamar `format()` num valor nulo — a mesma cautela de `formatar_prazo()` (Tópico 11), só que na direção de uma API JSON em vez de uma view.

**✅ Checkpoint:** `GET /api/tarefas` retorna o campo `urgente` calculado, não presente diretamente na tabela.

### Passo 4 — implemente store com validação de API

```php
public function store(Request $request)
{
    $dados = $request->validate([
        'titulo' => 'required|string|max:150',
        'prazo' => 'required|date|after:today',
        'tipo' => 'required|in:urgente,rotina',
    ]);

    $tarefa = $this->dao->criar($dados);
    return new TarefaResource($tarefa);
}
```

Se a validação falhar, o Laravel retorna automaticamente **422** com um JSON de erros estruturado (`{"errors": {"titulo": [...]}}`) — sem código extra, o mesmo `validate()` já usado na versão web se comporta diferente ao detectar que a requisição espera JSON.

**✅ Checkpoint:** `POST /api/tarefas` sem título retorna 422 com o JSON de erros.

### Passo 5 — implemente update/destroy da API

```php
public function update(Request $request, Tarefa $tarefa)
{
    $dados = $request->validate([
        'titulo' => 'sometimes|string|max:150',
        'prazo' => 'sometimes|date',
        'tipo' => 'sometimes|in:urgente,rotina',
    ]);

    $this->dao->atualizar($tarefa, $dados);

    return new TarefaResource($tarefa);
}

public function destroy(Tarefa $tarefa)
{
    $this->dao->remover($tarefa);

    return response()->noContent();
}
```

`sometimes` troca de regra em relação ao `required` do `store()`: numa API JSON, é comum um cliente mandar só o campo que mudou (`{"concluida": true}`, por exemplo, sem repetir `titulo` e `prazo`) — `sometimes` só valida um campo **se ele estiver presente** na requisição, em vez de exigi-lo sempre. `response()->noContent()` devolve **204 No Content**: o corpo vazio é intencional, é a convenção REST para "a exclusão funcionou, não há nada a devolver".

**✅ Checkpoint:** `PUT /api/tarefas/{id}` com um JSON parcial (só `titulo`, por exemplo) atualiza apenas esse campo e devolve 200; `DELETE /api/tarefas/{id}` devolve 204 sem corpo.

**Verificado na implementação de referência**: contra a API real do `task-manager`, `POST /api/tarefas` sem `titulo` devolveu `422` com `{"message":"The titulo field is required.","errors":{"titulo":[...]}}`; com dados válidos devolveu `201` com o recurso formatado (incluindo `urgente` calculado); `PUT /api/tarefas/{id}` só com `{"titulo": "Novo título"}` devolveu `200` sem exigir os outros campos; e `DELETE /api/tarefas/{id}` devolveu `204 No Content` — os quatro códigos de status confirmados na mesma sessão de testes via `curl`.

### Passo 6 — compare a API com o "Web Service" tradicional (contexto)

| Aspecto | Sua API (REST/JSON) | Web Service tradicional (SOAP) |
|---|---|---|
| Formato | JSON | XML rígido |
| Descoberta | Convenção REST + Swagger opcional | WSDL obrigatório |
| Uso típico | Apps modernos, SPAs, mobile | Sistemas corporativos legados |

Sua API de tarefas, construída em poucos passos com `apiResource` + `JsonResource`, ilustra por que REST se tornou o padrão dominante: menos cerimônia que SOAP para o mesmo resultado.

**✅ Checkpoint:** você explica, com seu próprio projeto como exemplo, por que REST tem menos "cerimônia" que SOAP.

### Resumo do que você construiu

```
✅ Controller de API dedicado, separado do controller web
✅ apiResource gerando as 5 rotas convencionais de API
✅ TarefaResource controlando o formato exato do JSON retornado, com prazo nulo tratado via ?->
✅ store() com validação retornando 422 automático em caso de erro
✅ update()/destroy() completando os 5 métodos do apiResource, com validação sometimes e 204 No Content
✅ Comparação prática entre sua API REST e o modelo SOAP tradicional
```

### Exercícios

1. **Paginação**: adicione `paginate()` ao `index()` e observe o formato de resposta do Laravel.
2. **Versionamento**: mova as rotas para `/api/v1/tarefas` e explique por que isso é útil.
3. **PATCH vs PUT**: `Route::apiResource` registra `update` em ambos os verbos. Pesquise a diferença semântica entre eles e discuta se a validação `sometimes` do Passo 5 combina melhor com um dos dois.

### Perguntas de fixação

1. Por que `POST /api/tarefas` sem título devolve 422 em vez do redirecionamento que a versão web (Tópico 9) devolveria?
2. O que `TarefaResource` acrescenta que retornar o Model Eloquent diretamente (Passo 2) não teria?
3. Compare `Route::resource` (Tópico 8) com `Route::apiResource` (Passo 1): quais métodos faltam no segundo, e por quê?

---

## 15. Projeto Final + Apresentações

Aula dupla: metade construção final, metade apresentação. Consolide o "Gerenciador de Tarefas" construído ao longo do semestre (POO, MVC, DAO, Livewire, API) num projeto final coeso.

### Contexto

Diferente de outras disciplinas que têm um bimestre inteiro dedicado a projeto final, em PWII o projeto final é a **consolidação** do que já foi construído passo a passo desde o Tópico 1 — não um projeto novo do zero.

### Checklist de consolidação

- [ ] CRUD completo de Tarefa funcionando (POO + MVC + DAO)
- [ ] Autenticação (login/logout) protegendo rotas de escrita
- [ ] Pelo menos uma interação via Livewire (sem reload de página)
- [ ] API JSON dedicada (`/api/tarefas`) funcionando em paralelo à interface web
- [ ] Nenhuma lógica de negócio dentro de Views (auditoria do padrão MVC)

### Roteiro de apresentação (5 minutos por grupo)

1. **Demo da interface web** (2 min): CRUD completo, incluindo a interação Livewire.
2. **Demo da API** (1,5 min): mostre `GET /api/tarefas` retornando JSON, via Postman ou curl.
3. **Uma decisão de arquitetura** (1 min): explique por que a lógica está onde está (Model vs Controller vs DAO).
4. **Perguntas** (30s).

### Rubrica de avaliação

| Critério | Peso |
|---|---|
| CRUD completo funcionando (interface web) | 30% |
| Autenticação protegendo rotas corretamente | 20% |
| Interação Livewire funcionando sem reload | 20% |
| API JSON respondendo corretamente | 20% |
| Clareza da explicação arquitetural | 10% |

### Próximos passos

Após a apresentação, considere publicar o projeto no GitHub como peça de portfólio, documentando as decisões de arquitetura no README.

---

## Revisão geral / Exercícios finais

Sem consultar o material anterior, responda e depois confira:

1. **POO** — explique, com suas palavras, a diferença entre herança e polimorfismo, usando `TarefaUrgente`/`TarefaRotina` como exemplo.
2. **MVC** — aponte um trecho de código (real, do seu projeto) que viola a separação de camadas, e reescreva-o corretamente.
3. **DAO** — por que um Controller não deveria chamar `Model::` diretamente, se tanto o DAO quanto o Model acabam rodando a mesma query no fim das contas?
4. **Sessões** — descreva o caminho completo de um login bem-sucedido: `Auth::attempt()` → sessão → cookie → middleware `auth` na requisição seguinte.
5. **Segurança** — por que a proteção contra XSS do Blade acontece na exibição, e não no momento de salvar o dado?
6. **Livewire** — por que `wire:click` não precisa de um header `X-CSRF-TOKEN` manual, ao contrário de uma chamada `fetch()` pura?
7. **API** — escreva, de memória, um `JsonResource` simples para um recurso `Produto` com `id`, `nome` e `preco`.

### Autoavaliação

| Tópico | Consigo explicar sem consultar material | Consigo implementar do zero |
|---|---|---|
| Formulários/HTTP | | |
| Sessões | | |
| Segurança | | |
| Padrão DAO | | |
| POO (herança/polimorfismo) | | |
| MVC | | |
| Controller RESTful | | |
| Views/Blade | | |
| Livewire | | |
| API REST | | |

---

## Projeto completo — todos os arquivos juntos

Dois projetos Laravel separados, um por linha de tópicos. Cada arquivo abaixo está marcado com o tópico que o introduziu.

### `blog-app/` — Tópicos 1 a 5

```
blog-app/
├── .env
├── .env.example                         ← Tópico 2 (placeholder de GOOGLE_CLIENT_ID)
├── composer.json                        ← Tópico 2 (google/apiclient 2.x)
├── routes/
│   ├── web.php                          ← Tópicos 1, 2, 3, 5
│   └── api.php                          ← Tópico 1
├── bootstrap/app.php                    ← Tópico 1 (registro de routes/api.php)
├── config/services.php                  ← Tópico 2 (services.google.client_id)
├── database/migrations/
│   └── ..._add_google_id_to_usuarios_table.php ← Tópico 2 (password opcional, google_id)
├── app/
│   ├── Models/
│   │   ├── Usuario.php                  ← Tópico 2 (Authenticatable, google_id, hasMany posts do Tópico 5)
│   │   ├── Post.php                     ← Tópicos 4/5 (belongsTo)
│   │   └── Comentario.php               ← Tópico 3 (fillable, mass assignment)
│   ├── DAO/
│   │   ├── PostDAOInterface.php         ← Tópico 4
│   │   └── PostDAO.php                  ← Tópico 4
│   ├── Services/
│   │   ├── GoogleIdTokenVerifier.php         ← Tópico 2
│   │   └── GoogleClientIdTokenVerifier.php   ← Tópico 2
│   ├── Http/Controllers/
│   │   ├── NewsletterController.php     ← Tópico 1
│   │   ├── BuscaController.php          ← Tópico 3
│   │   ├── ComentarioController.php     ← Tópico 3
│   │   ├── SessaoController.php         ← Tópico 2 (mostrarLogin/entrar/sair + entrarComGoogle)
│   │   └── PostController.php           ← Tópicos 4/5 (DAO + auth + 403 de dono)
│   └── Providers/AppServiceProvider.php ← Tópico 4 (bind do PostDAOInterface); Tópico 2 (bind do GoogleIdTokenVerifier)
└── resources/views/
    ├── newsletter.blade.php             ← Tópico 1
    ├── busca.blade.php                  ← Tópico 3
    ├── comentarios.blade.php            ← Tópico 3
    ├── sessao/login.blade.php           ← Tópico 2 (formulário de senha + botão do Google)
    └── posts/
        ├── index.blade.php              ← Tópico 5
        └── novo.blade.php               ← Tópico 5
```

Um usuário navegando pelo `blog-app` de ponta a ponta: abre `/posts` (**Model** `Post` com `usuario` carregado via `PostDAO`, **View** `posts/index.blade.php`, **Controller** `PostController@index`); sem estar logado, vê só a listagem e um link para `/login`; loga com `bruno@email.com`/`senha456` (Tópico 2), e o middleware `auth` libera `/posts/novo`; publica um post, que grava com `auth()->id()` como autor (Tópico 5) através do `PostDAO` (Tópico 4); tenta excluir um post que não é seu e recebe 403; excluir o próprio post funciona e redireciona de volta para `/posts`. Em paralelo, `/usuarios/busca` e `/comentarios` (Tópico 3) mostram, lado a lado, as proteções automáticas do Laravel contra SQL Injection, mass assignment e XSS.

### `task-manager/` — Tópicos 6 a 15

```
task-manager/
├── .env
├── composer.json                        ← Tópico 11 (autoload de app/helpers.php)
├── routes/
│   ├── web.php                          ← Tópicos 8/9 (Route::resource, Invokable, Singleton)
│   └── api.php                          ← Tópico 14 (Route::apiResource)
├── database/migrations/
│   ├── ..._create_tarefas_table.php       ← Tópico 6 (titulo, concluida; prazo/tipo já previstos para 7/8)
│   └── ..._create_configuracoes_table.php ← Tópico 9 (singleton, já nasce com uma linha)
├── database/factories/TarefaFactory.php   ← Factories (titulo/prazo/concluida/tipo sempre válidos; states urgente()/concluida())
├── database/seeders/DatabaseSeeder.php    ← Factories (popula Tarefa via factory: 15 genéricas + 3 urgentes + 2 concluídas)
├── app/
│   ├── helpers.php                      ← Tópico 11 (formatar_prazo, tempo_restante)
│   ├── Models/
│   │   ├── Tarefa.php                   ← Tópicos 6/7/8 (isUrgente, calcularPrioridade via match) + Factories (trait HasFactory)
│   │   └── Configuracao.php             ← Tópico 9 (atual(), acessor do singleton)
│   ├── DAO/
│   │   ├── TarefaDAOInterface.php       ← Tópico 12
│   │   └── TarefaDAO.php                ← Tópico 12
│   ├── Http/
│   │   ├── Controllers/
│   │   │   ├── TarefaController.php               ← Tópicos 8/9/12 (7 métodos RESTful; store/update/destroy via DAO, index/show direto via Eloquent)
│   │   │   ├── MarcarTarefaConcluidaController.php ← Tópico 9 (Invokable, --invokable)
│   │   │   ├── ConfiguracaoController.php          ← Tópico 9 (Singleton, --singleton)
│   │   │   └── Api/TarefaController.php            ← Tópico 14 (5 métodos JSON, --api)
│   │   └── Resources/TarefaResource.php ← Tópico 14
│   ├── Livewire/
│   │   ├── TarefaLista.php              ← Tópico 13
│   │   └── TarefaForm.php               ← Tópico 13
│   └── Providers/AppServiceProvider.php ← Tópicos 10/11/12 (View Composer, directive @urgente, bind do DAO)
├── resources/views/
│   ├── layouts/app.blade.php            ← Tópico 10 (@yield, View Composer totalUrgentes) + Tópico 13 (@livewireStyles/@livewireScripts)
│   ├── components/tarefa-card.blade.php ← Tópico 10 (+ formatar_prazo/tempo_restante/@urgente do Tópico 11)
│   ├── livewire/
│   │   ├── tarefa-lista.blade.php       ← Tópico 13
│   │   └── tarefa-form.blade.php        ← Tópico 13
│   ├── tarefas/
│   │   ├── index.blade.php              ← Tópicos 10/13 (montagem final no Passo 7 do Tópico 13)
│   │   ├── create.blade.php             ← Tópicos 9/10 (campos prazo/tipo no Passo 5 do Tópico 10)
│   │   ├── show.blade.php               ← Tópico 9 (+ botão "concluir" do Invokable)
│   │   └── edit.blade.php               ← Tópico 9
│   └── configuracoes/
│       ├── show.blade.php               ← Tópico 9 (Singleton)
│       └── edit.blade.php               ← Tópico 9 (Singleton)
└── tests/Feature/TarefaFactoryTest.php  ← Factories (cenários montados com Tarefa::factory(), sem digitar campos obrigatórios na mão)
```

Um usuário navegando pelo `task-manager` de ponta a ponta: abre `/tarefas` (**Controller** `TarefaController@index`, que consulta o **Model** `Tarefa` diretamente — a exceção deliberada do Tópico 12, já que paginar por `Configuracao::atual()->tarefas_por_pagina` não é algo que `TarefaDAOInterface::todas()` sabe fazer — e devolve `tarefas/index.blade.php` — **View** — dentro do `layouts.app`, cujo View Composer já injetou `$totalUrgentes` sem o Controller pedir); cada tarefa aparece através do componente `<x-tarefa-card />`, que usa a directive `@urgente` e os helpers `formatar_prazo()`/`tempo_restante()`; na tela de detalhes, um botão aciona o `MarcarTarefaConcluidaController` (Invokable) para alternar a conclusão sem passar por `TarefaController`; em `/configuracoes`, o `ConfiguracaoController` (Singleton) mostra e edita a única linha de configuração do sistema, sem `id` nenhum na URL; na mesma página de tarefas, o componente Livewire `TarefaLista` permite marcar tarefas como concluídas com um clique, sem reload; `TarefaForm`, outro componente Livewire, cria tarefas reativamente, validando com `#[Validate(...)]`; em paralelo, `GET /api/tarefas` devolve a mesma informação em JSON, formatada por `TarefaResource`, através de um quarto tipo de Controller (`--api`), para consumo por outro programa — prontas para uma eventual apresentação via Postman, como pede o Tópico 15.
