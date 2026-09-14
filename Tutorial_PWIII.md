# Tutorial — Programação para Web III (PWIII)

Linguagem/framework de referência: PHP (Laravel-like ORM) — conceitos aplicáveis a outros stacks.

### 1. Integração com Banco de Dados (ORM)

Um ORM (Object-Relational Mapper) traduz objetos do código para linhas de tabelas do banco, evitando escrever SQL manualmente para operações comuns.

```php
class Produto extends Model {
    protected $table = 'produto';
    protected $fillable = ['nome', 'preco', 'categoria_id'];

    public function categoria() {
        return $this->belongsTo(Categoria::class);
    }
}

// uso
$produto = Produto::find(1);
$produtos = Produto::where('preco', '>', 100)->get();
```

**Vantagens do ORM:** menos código repetitivo, proteção automática contra SQL Injection (usa prepared statements internamente), migrations para versionar o schema.
**Quando evitar:** consultas muito complexas/otimizadas podem precisar de SQL puro mesmo com ORM disponível.

**Exercício:** Configure um model `Cliente` mapeado para a tabela `cliente`, e escreva consultas usando o ORM para buscar clientes por cidade e ordenar por nome.

### 2. CRUD com ORM

```php
// Create
$produto = new Produto();
$produto->nome = 'Caneta';
$produto->preco = 2.50;
$produto->save();

// Read
$produto = Produto::find(1);
$todos = Produto::all();

// Update
$produto = Produto::find(1);
$produto->preco = 3.00;
$produto->save();

// Delete
Produto::destroy(1);
```

**Exercício:** Implemente o CRUD completo de `Categoria` usando apenas métodos do ORM (sem SQL manual), incluindo validação antes de salvar (nome obrigatório).

### 3. Introdução a APIs com o Framework

Uma API expõe funcionalidades do sistema para outros programas consumirem, tipicamente via HTTP retornando JSON. Rotas de API costumam seguir o padrão REST (recursos + verbos HTTP).

```php
// routes/api.php
Route::get('/produtos', [ProdutoController::class, 'index']);
Route::post('/produtos', [ProdutoController::class, 'store']);

// ProdutoController
class ProdutoController {
    public function index() {
        return response()->json(Produto::all());
    }

    public function store(Request $request) {
        $produto = Produto::create($request->all());
        return response()->json($produto, 201);
    }
}
```

**Exercício:** Crie um endpoint `GET /api/produtos` que retorne todos os produtos em JSON, e teste-o com o navegador ou `curl`.

### 4. Padrões REST (XML/JSON)

REST (Representational State Transfer) define convenções para expor recursos via HTTP:

| Verbo HTTP | Ação            | Exemplo               |
|-----------|------------------|------------------------|
| GET       | ler              | `GET /produtos/5`      |
| POST      | criar            | `POST /produtos`       |
| PUT       | atualizar (todo) | `PUT /produtos/5`      |
| PATCH     | atualizar (parcial) | `PATCH /produtos/5` |
| DELETE    | remover          | `DELETE /produtos/5`   |

JSON é o formato de troca mais comum hoje (leve, nativo em JS); XML ainda aparece em sistemas legados/corporativos (SOAP, integrações bancárias).

```json
{ "id": 5, "nome": "Caneta", "preco": 2.50 }
```
```xml
<produto>
  <id>5</id>
  <nome>Caneta</nome>
  <preco>2.50</preco>
</produto>
```

**Exercício:** Implemente os 5 verbos REST para o recurso `produto`, retornando o código HTTP correto para cada caso (200, 201, 204, 404).

### 5. Autenticação JWT

JWT (JSON Web Token) é um token assinado digitalmente que carrega informações do usuário autenticado, enviado em cada requisição — dispensa sessão no servidor (*stateless*).

**Estrutura:** `header.payload.assinatura` (codificados em Base64).

```php
// gerar token no login
$payload = ['sub' => $usuario->id, 'exp' => time() + 3600];
$token = JWT::encode($payload, $chaveSecreta, 'HS256');

// middleware de autenticação
function autenticarRequisicao(Request $request) {
    $token = str_replace('Bearer ', '', $request->header('Authorization'));
    try {
        $decoded = JWT::decode($token, new Key($chaveSecreta, 'HS256'));
        return $decoded->sub; // id do usuário
    } catch (Exception $e) {
        abort(401, 'Token inválido ou expirado');
    }
}
```

**Fluxo típico:** cliente faz login → recebe o JWT → envia o JWT no header `Authorization: Bearer <token>` em cada requisição subsequente → servidor valida a assinatura e a expiração.

**Exercício:** Implemente login que gera um JWT, e proteja o endpoint `GET /api/produtos` exigindo um token válido no header `Authorization`.

### 6. Testes de APIs (Postman)

Postman (ou ferramentas equivalentes como Insomnia) permite montar requisições HTTP manualmente para testar uma API sem precisar de front-end pronto.

**Fluxo básico de teste:**
1. Criar uma *collection* com uma requisição por endpoint.
2. Configurar método, URL, headers (`Content-Type`, `Authorization`) e corpo (JSON).
3. Usar variáveis de ambiente (`{{base_url}}`, `{{token}}`) para reaproveitar entre requisições.
4. Escrever *tests* (scripts JS) para validar status code e corpo da resposta automaticamente.

```javascript
// aba "Tests" no Postman
pm.test("Status é 200", () => pm.response.to.have.status(200));
pm.test("Retorna lista de produtos", () => {
    const json = pm.response.json();
    pm.expect(json).to.be.an('array');
});
```

**Exercício:** Monte uma collection Postman com requisições para todo o CRUD de produto (incluindo o login JWT), usando uma variável de ambiente para o token.

### 7. Web Services (SOAP vs REST, Swagger)

**SOAP** — protocolo mais rígido e formal, baseado em XML, com contrato descrito em WSDL; comum em sistemas corporativos/bancários legados.
**REST** — estilo arquitetural mais leve, baseado em HTTP + JSON, mais simples de consumir e mais usado atualmente.

| | SOAP | REST |
|---|---|---|
| Formato | XML | JSON (ou XML) |
| Contrato | WSDL (rígido) | OpenAPI/Swagger (opcional) |
| Complexidade | Alta | Baixa |
| Uso típico | Sistemas legados, bancos | APIs web modernas |

**Swagger/OpenAPI** documenta uma API REST de forma padronizada e navegável:
```yaml
paths:
  /produtos:
    get:
      summary: Lista todos os produtos
      responses:
        '200':
          description: Lista de produtos em JSON
```

**Exercício:** Escreva a documentação OpenAPI (YAML) dos endpoints de `produto` criados no tópico de Padrões REST.

### 8. Consumo de APIs externas

Consumir uma API de terceiros envolve fazer requisições HTTP a partir do seu próprio backend (ou front-end) e tratar a resposta.

```php
$response = Http::withHeaders([
    'Authorization' => 'Bearer ' . $token,
])->get('https://api.terceiro.com/dados');

if ($response->successful()) {
    $dados = $response->json();
} else {
    // tratar erro: 4xx (cliente), 5xx (servidor), timeout
    Log::error('Falha ao consumir API externa: ' . $response->status());
}
```

**Cuidados:** sempre tratar timeout e erros de rede; nunca expor chaves de API de terceiros no front-end (fazer a chamada pelo backend); respeitar limites de taxa (*rate limit*) da API consumida.

**Exercício:** Consuma uma API pública gratuita (ex.: ViaCEP, câmbio de moedas) a partir do seu backend PHP, tratando erro de CEP/moeda inexistente.

### 9. Websockets / aplicações em tempo real

Diferente do HTTP tradicional (requisição/resposta), WebSocket mantém uma conexão aberta bidirecional entre cliente e servidor — ideal para chat, notificações em tempo real, dashboards ao vivo.

```javascript
// front-end
const socket = new WebSocket('wss://exemplo.com/chat');
socket.onopen = () => socket.send(JSON.stringify({ tipo: 'entrar', sala: 'geral' }));
socket.onmessage = (event) => {
    const msg = JSON.parse(event.data);
    exibirMensagem(msg);
};
```

```php
// servidor (ex.: usando Ratchet/PHP)
class ChatServer implements MessageComponentInterface {
    public function onMessage(ConnectionInterface $from, $msg) {
        foreach ($this->clientes as $cliente) {
            if ($cliente !== $from) $cliente->send($msg);
        }
    }
}
```

**Exercício:** Implemente um chat simples de uma sala só, onde mensagens enviadas por um cliente aparecem em tempo real para todos os demais conectados.

### 10. Internacionalização (i18n)

i18n adapta a aplicação para diferentes idiomas e formatos regionais (datas, moeda, números), mantendo os textos fora do código-fonte.

```php
// resources/lang/pt-BR/mensagens.php
return ['boas_vindas' => 'Bem-vindo, :nome!'];

// resources/lang/en/mensagens.php
return ['boas_vindas' => 'Welcome, :name!'];

// uso
echo __('mensagens.boas_vindas', ['nome' => 'Ana']);
```

```php
// formatação de moeda/data conforme o locale
$formatter = new NumberFormatter('pt_BR', NumberFormatter::CURRENCY);
echo $formatter->formatCurrency(19.9, 'BRL'); // R$ 19,90
```

**Exercício:** Extraia todos os textos fixos de uma tela para arquivos de idioma (`pt-BR` e `en`), e implemente um seletor de idioma que troca o locale ativo.

### 11. Segurança avançada em APIs (CORS, Rate Limiting)

**CORS (Cross-Origin Resource Sharing)** controla quais origens (domínios) podem chamar sua API a partir do navegador.
```php
header('Access-Control-Allow-Origin: https://meuapp.com');
header('Access-Control-Allow-Methods: GET, POST, PUT, DELETE');
header('Access-Control-Allow-Headers: Content-Type, Authorization');
```

**Rate Limiting** limita quantas requisições um cliente pode fazer em um intervalo de tempo, protegendo contra abuso/DoS acidental.
```php
Route::middleware('throttle:60,1')->group(function () {
    // no máximo 60 requisições por minuto por cliente/IP
    Route::get('/api/produtos', [ProdutoController::class, 'index']);
});
```

**Outras práticas:** validar e sanitizar todo input mesmo em rotas autenticadas; nunca retornar mensagens de erro detalhadas (stack trace) em produção; usar HTTPS sempre.

**Exercício:** Configure CORS para aceitar apenas o domínio do seu front-end e adicione rate limiting de 30 requisições/minuto ao endpoint de login (para dificultar força bruta).

### 12. Projeto Final — Início

Aplicação de todo o conteúdo do curso em uma API completa:
1. Modelagem de recursos com ORM (migrations, models, relacionamentos).
2. Endpoints REST completos para pelo menos 2 recursos.
3. Autenticação via JWT protegendo rotas sensíveis.
4. Pelo menos um consumo de API externa.
5. CORS e rate limiting configurados.
6. Documentação da API (Swagger/OpenAPI) e collection Postman.
7. (Opcional/bônus) um canal WebSocket para alguma funcionalidade em tempo real.

### 13. Projeto Final — Apresentações

Checklist para a apresentação:
- Demonstração ao vivo dos endpoints via Postman (incluindo login JWT).
- Documentação OpenAPI navegável.
- Evidência de CORS e rate limiting configurados (testar excedendo o limite).
- Explicação das escolhas de modelagem do ORM.
- Discussão de limitações e possíveis melhorias futuras.
