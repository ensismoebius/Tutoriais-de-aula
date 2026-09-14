# Tutorial — Programação para Web II (PWII)

Linguagem de referência: PHP.

### 1. Formulários/HTTP (POST, GET, superglobais)

Formulários HTML enviam dados ao servidor via método `GET` (dados na URL, visíveis, limitados) ou `POST` (dados no corpo da requisição, ocultos, sem limite prático de tamanho).

```html
<form method="POST" action="cadastro.php">
  <input type="text" name="nome">
  <input type="email" name="email">
  <button type="submit">Enviar</button>
</form>
```

```php
<?php
// cadastro.php
$nome = $_POST['nome'] ?? '';
$email = $_POST['email'] ?? '';

if (empty($nome) || empty($email)) {
    die('Preencha todos os campos.');
}
echo "Cadastrado: $nome ($email)";
```

**Superglobais principais:** `$_GET`, `$_POST`, `$_SERVER`, `$_SESSION`, `$_COOKIE`, `$_FILES` — arrays disponíveis em qualquer escopo do script.

⚠️ Nunca confie em dados vindos de `$_GET`/`$_POST` sem validar e sanitizar — eles vêm do cliente e podem ser manipulados.

**Exercício:** Crie um formulário de cadastro de produto (nome, preço, categoria) que envie via `POST` e um script PHP que valide os campos (preço numérico e positivo) antes de processar.

### 2. Sessões (login/logout, cookies)

Sessões (`$_SESSION`) mantêm dados do usuário entre requisições (o HTTP é *stateless*). Um cookie com o ID da sessão é enviado ao navegador; o servidor usa esse ID para recuperar os dados armazenados.

```php
<?php
session_start();

// login.php
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    if (autenticar($_POST['usuario'], $_POST['senha'])) {
        $_SESSION['usuario_id'] = 5;
        $_SESSION['usuario_nome'] = 'Ana';
        header('Location: painel.php');
        exit;
    }
}
```

```php
<?php
// painel.php — página protegida
session_start();
if (!isset($_SESSION['usuario_id'])) {
    header('Location: login.php');
    exit;
}
echo "Bem-vindo, " . $_SESSION['usuario_nome'];
```

```php
<?php
// logout.php
session_start();
session_destroy();
header('Location: login.php');
```

**Exercício:** Implemente um fluxo completo de login/logout com uma "área restrita" que redireciona para a tela de login caso o usuário não esteja autenticado.

### 3. Segurança (SQL Injection, XSS)

**SQL Injection** — ocorre quando entrada do usuário é concatenada diretamente em uma query SQL, permitindo que o atacante altere o comando executado.

```php
// VULNERÁVEL — nunca fazer isso
$sql = "SELECT * FROM usuario WHERE login = '" . $_POST['login'] . "'";

// SEGURO — usando prepared statements (PDO)
$stmt = $pdo->prepare('SELECT * FROM usuario WHERE login = :login');
$stmt->execute(['login' => $_POST['login']]);
```

**XSS (Cross-Site Scripting)** — ocorre quando dados do usuário são exibidos no HTML sem escapar, permitindo injeção de scripts maliciosos.

```php
// VULNERÁVEL
echo "Olá, " . $_GET['nome'];

// SEGURO — escapando saída
echo "Olá, " . htmlspecialchars($_GET['nome'], ENT_QUOTES, 'UTF-8');
```

**Regra geral:** validar/sanitizar dados na **entrada**, sempre usar *prepared statements* para SQL, e sempre escapar dados na **saída** (HTML).

**Exercício:** Pegue um formulário de busca que monta SQL por concatenação e refatore-o para usar PDO com prepared statements; garanta que o termo buscado seja exibido de volta na tela com `htmlspecialchars`.

### 4. Padrão DAO

O padrão DAO (Data Access Object) isola o acesso ao banco de dados em classes dedicadas, com um método por operação (CRUD), evitando SQL espalhado pela aplicação.

```php
class ProdutoDAO {
    private PDO $pdo;
    public function __construct(PDO $pdo) { $this->pdo = $pdo; }

    public function salvar(array $produto): void {
        $stmt = $this->pdo->prepare(
            'INSERT INTO produto (nome, preco) VALUES (:nome, :preco)'
        );
        $stmt->execute(['nome' => $produto['nome'], 'preco' => $produto['preco']]);
    }

    public function listarTodos(): array {
        return $this->pdo->query('SELECT * FROM produto')->fetchAll(PDO::FETCH_ASSOC);
    }

    public function buscarPorId(int $id): ?array {
        $stmt = $this->pdo->prepare('SELECT * FROM produto WHERE id = :id');
        $stmt->execute(['id' => $id]);
        $resultado = $stmt->fetch(PDO::FETCH_ASSOC);
        return $resultado ?: null;
    }
}
```

**Exercício:** Implemente `ClienteDAO` com `salvar`, `buscarPorId`, `listarTodos`, `atualizar` e `remover`, usando PDO com prepared statements em todos os métodos.

### 5. Projeto Integrador

Momento de integrar formulários, sessões, segurança e DAO em um mini-sistema funcional: cadastro com validação, login protegendo áreas internas, e persistência via DAO com queries seguras.

**Checklist de integração:**
- Formulário com validação server-side.
- Login com sessão e página protegida.
- Toda entrada validada; toda saída escapada (`htmlspecialchars`).
- Toda consulta SQL via *prepared statements* (nunca concatenação).
- Camada DAO isolando o acesso a dados da lógica de página.

**Exercício:** Monte um mini-CRUD de produtos com login obrigatório para acessar a área de cadastro, seguindo o checklist acima.

### 6. Introdução a POO (classes, objetos)

PHP suporta Programação Orientada a Objetos: classes definem a estrutura (atributos e métodos) e objetos são instâncias dessa estrutura.

```php
class Produto {
    private string $nome;
    private float $preco;

    public function __construct(string $nome, float $preco) {
        $this->nome = $nome;
        $this->preco = $preco;
    }

    public function getNome(): string { return $this->nome; }
    public function getPreco(): float { return $this->preco; }

    public function aplicarDesconto(float $percentual): void {
        $this->preco -= $this->preco * ($percentual / 100);
    }
}

$produto = new Produto('Caneta', 2.50);
$produto->aplicarDesconto(10);
echo $produto->getPreco(); // 2.25
```

**Exercício:** Modele a classe `ContaBancaria` com atributos privados (`saldo`, `titular`) e métodos `depositar`, `sacar` (que valida saldo suficiente) e `getSaldo`.

### 7. POO Aplicada (herança, polimorfismo)

**Herança** permite que uma classe reutilize e estenda o comportamento de outra:
```php
abstract class Funcionario {
    protected string $nome;
    public function __construct(string $nome) { $this->nome = $nome; }
    abstract public function calcularSalario(): float;
}

class FuncionarioCLT extends Funcionario {
    private float $salarioBase;
    public function __construct(string $nome, float $salarioBase) {
        parent::__construct($nome);
        $this->salarioBase = $salarioBase;
    }
    public function calcularSalario(): float { return $this->salarioBase; }
}

class FuncionarioComissionado extends Funcionario {
    private float $vendas;
    public function __construct(string $nome, float $vendas) {
        parent::__construct($nome);
        $this->vendas = $vendas;
    }
    public function calcularSalario(): float { return $this->vendas * 0.05; }
}
```

**Polimorfismo** — tratar objetos de subclasses diferentes de forma uniforme:
```php
function imprimirFolha(array $funcionarios): void {
    foreach ($funcionarios as $f) {
        echo $f->calcularSalario() . "\n"; // cada um calcula à sua maneira
    }
}
```

**Exercício:** Modele `FormaPagamento` (abstrata) com subclasses `Boleto`, `Cartao` e `Pix`, cada uma implementando `processarPagamento()` de forma diferente.

### 8. Padrão MVC

MVC organiza a aplicação em três camadas:
- **Model** — dados e regras de negócio (entidades + DAO).
- **View** — templates HTML/PHP que apenas exibem dados.
- **Controller** — recebe a requisição HTTP, aciona o Model, escolhe a View.

```
Requisição HTTP → Controller → Model (busca/salva dados) → Controller → View (renderiza HTML)
```

**Vantagens:** separa responsabilidades, facilita manutenção e testes, evita "spaghetti code" com HTML e SQL misturados no mesmo arquivo.

**Exercício:** Desenhe a estrutura de pastas (`app/models`, `app/views`, `app/controllers`) para um sistema de cadastro de produtos em MVC.

### 9. Implementação de Controller

O Controller recebe a requisição, decide o que fazer (chamando o Model) e direciona para a View correta.

```php
class ProdutoController {
    private ProdutoDAO $dao;
    public function __construct(ProdutoDAO $dao) { $this->dao = $dao; }

    public function listar(): void {
        $produtos = $this->dao->listarTodos();
        require __DIR__ . '/../views/produto/listar.php';
    }

    public function salvar(): void {
        if ($_SERVER['REQUEST_METHOD'] === 'POST') {
            $this->dao->salvar([
                'nome' => $_POST['nome'],
                'preco' => (float) $_POST['preco'],
            ]);
            header('Location: /produtos');
            exit;
        }
        require __DIR__ . '/../views/produto/form.php';
    }
}
```

**Exercício:** Implemente `ClienteController` com ações `listar`, `novo` (exibe formulário) e `salvar` (processa o POST), seguindo o padrão acima.

### 10. Views e interação com Controller

Views devem conter apenas apresentação (HTML) com o mínimo de lógica possível — os dados chegam prontos do Controller.

```php
<!-- views/produto/listar.php -->
<table>
  <?php foreach ($produtos as $produto): ?>
    <tr>
      <td><?= htmlspecialchars($produto['nome']) ?></td>
      <td><?= number_format($produto['preco'], 2, ',', '.') ?></td>
    </tr>
  <?php endforeach; ?>
</table>
```

**Boas práticas:** sempre escapar dados vindos do Model com `htmlspecialchars` antes de exibi-los (proteção contra XSS, retomando o tópico de Segurança); evitar `SELECT *`/SQL dentro da View.

**Exercício:** Crie a View `form.php` para cadastro/edição de produto, reutilizável tanto para "novo produto" quanto para "editar produto" (dica: usar uma variável `$produto` que pode vir vazia ou preenchida).

### 11. Revisão de MVC + helpers

Funções *helper* centralizam lógica repetida entre Views/Controllers (formatação, autenticação, roteamento simples).

```php
// helpers.php
function formatarMoeda(float $valor): string {
    return 'R$ ' . number_format($valor, 2, ',', '.');
}

function estaLogado(): bool {
    return isset($_SESSION['usuario_id']);
}

function redirecionarSeNaoLogado(): void {
    if (!estaLogado()) {
        header('Location: /login');
        exit;
    }
}
```

**Exercício:** Extraia para um arquivo `helpers.php` todas as funções de formatação e autenticação repetidas nas Views/Controllers do seu projeto até aqui.

### 12. Integração DAO + MVC

Consolidação: o Controller nunca acessa o banco diretamente — sempre por meio do DAO (injetado via construtor), mantendo a separação de responsabilidades completa.

```php
// index.php — front controller simples
require 'config/database.php'; // cria $pdo
require 'helpers.php';

$dao = new ProdutoDAO($pdo);
$controller = new ProdutoController($dao);

$acao = $_GET['acao'] ?? 'listar';
match ($acao) {
    'novo', 'salvar' => $controller->salvar(),
    default => $controller->listar(),
};
```

**Exercício:** Monte um `index.php` único (front controller) que roteie para os Controllers de `Produto` e `Cliente` com base em um parâmetro `?acao=`.

### 13. AJAX (Fetch API)

AJAX permite atualizar parte de uma página sem recarregá-la, chamando o servidor em segundo plano com JavaScript.

```javascript
// front-end
async function buscarProdutos() {
  const resp = await fetch('/api/produtos');
  const produtos = await resp.json();
  const lista = document.getElementById('lista');
  lista.innerHTML = produtos.map(p => `<li>${p.nome} - R$ ${p.preco}</li>`).join('');
}
```

```php
<?php
// api/produtos.php — endpoint que retorna JSON
header('Content-Type: application/json');
$produtos = $dao->listarTodos();
echo json_encode($produtos);
```

**Exercício:** Implemente uma busca de produtos "ao vivo" (sem recarregar a página) que filtra a lista conforme o usuário digita, usando `fetch` e um endpoint PHP que retorna JSON.

### 14. APIs + Web Services

Um endpoint de API segue o mesmo padrão MVC, mas a "View" é uma resposta JSON em vez de HTML, e o Controller responde de acordo com o método HTTP (`GET`, `POST`, `PUT`, `DELETE`).

```php
<?php
header('Content-Type: application/json');
$metodo = $_SERVER['REQUEST_METHOD'];

match ($metodo) {
    'GET' => responderListar($dao),
    'POST' => responderCriar($dao),
    'PUT' => responderAtualizar($dao),
    'DELETE' => responderRemover($dao),
    default => http_response_code(405),
};

function responderListar(ProdutoDAO $dao): void {
    echo json_encode($dao->listarTodos());
}
```

**Exercício:** Transforme o CRUD de produtos em uma API REST completa (`GET /produtos`, `POST /produtos`, `PUT /produtos/{id}`, `DELETE /produtos/{id}`), retornando os códigos HTTP corretos (200, 201, 404, etc.).

### 15. Projeto Final + Apresentações

Aplicação de todo o conteúdo do curso em um sistema web completo, seguindo MVC com DAO, autenticação por sessão, proteção contra SQL Injection/XSS, e ao menos uma funcionalidade via AJAX ou API JSON.

**Checklist para a apresentação:**
- Estrutura MVC clara (pastas `models`/`views`/`controllers`).
- Login funcional protegendo as áreas restritas.
- Todo acesso a dados via DAO com *prepared statements*.
- Toda saída HTML escapada (`htmlspecialchars`).
- Pelo menos uma interação via AJAX/fetch sem recarregar a página.
