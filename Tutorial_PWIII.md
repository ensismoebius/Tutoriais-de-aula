# Tutorial — Programação para Web III (PWIII)

Framework de referência: backend em Node.js/Express com Prisma ORM (MySQL/MariaDB), frontend em React (Vite), sempre em JavaScript. O projeto construído ao longo deste material é o `catalogo-produtos`: uma API de catálogo de produtos com categorias, autenticação, tempo real e integrações externas, consumida por um frontend React separado (`catalogo-frontend`).

Convenção de nomes: tudo que é criado neste tutorial (funções, variáveis, componentes, nomes de arquivo) é nomeado em português — `produto`, `autenticar`, `ListaProdutos`. Tudo que pertence a uma biblioteca ou API externa mantém o nome original em inglês — `PrismaClient`, `useEffect`, `express.json()`. Essa distinção ajuda a enxergar de relance o que é seu para editar e o que é contrato de terceiros para copiar exatamente como está.

> Atualizado para Prisma ORM 7. O Prisma 7 removeu o engine em Rust (agora é TS + WASM), passou a exigir ESM (sem `require`/`module.exports`) e tornou obrigatório o uso de um *driver adapter* para qualquer banco, incluindo MySQL. As diferenças em relação a versões anteriores estão marcadas com 🆕.

## Sumário

- [O básico, para quem está começando agora](#o-básico-para-quem-está-começando-agora)
- [Conceitos fundamentais antes de começar](#conceitos-fundamentais-antes-de-começar)
- [1. Integração com Banco de Dados (ORM)](#1-integração-com-banco-de-dados-orm)
- [Prisma com bancos de dados diferentes](#prisma-com-bancos-de-dados-diferentes)
- [2. CRUD com ORM](#2-crud-com-orm)
- [3. Introdução a APIs com o Framework](#3-introdução-a-apis-com-o-framework)
- [4. Padrões REST (XML/JSON)](#4-padrões-rest-xmljson)
- [5. Autenticação JWT](#5-autenticação-jwt)
- [6. Testes de APIs (Postman)](#6-testes-de-apis-postman)
- [7. Web Services (SOAP vs REST, Swagger)](#7-web-services-soap-vs-rest-swagger)
- [8. Consumo de APIs externas](#8-consumo-de-apis-externas)
- [9. Internacionalização (i18n)](#9-internacionalização-i18n)
- [10. Segurança avançada em APIs (CORS, Rate Limiting)](#10-segurança-avançada-em-apis-cors-rate-limiting)
- [11. Websockets / aplicações em tempo real](#11-websockets--aplicações-em-tempo-real)
- [12. Projeto Final — Início](#12-projeto-final--início)
- [13. Projeto Final — Apresentações](#13-projeto-final--apresentações)
- [Projeto completo — todos os arquivos juntos](#projeto-completo--todos-os-arquivos-juntos)

Cada tópico é um tutorial *build-along*: execute cada passo no seu computador e confira o resultado antes de prosseguir. Cada seção termina com um **✅ Checkpoint** — pare e confirme que o resultado bate antes de continuar.

## O básico, para quem está começando agora

Esta seção existe para quem nunca programou para a web antes, ou que só viu HTML/CSS estático até aqui. Se os termos abaixo já são familiares, pode pular direto para "Conceitos fundamentais antes de começar". Se não, vale ler com calma — tudo que vem depois neste tutorial assume que essas ideias já fazem sentido.

**O modelo cliente-servidor.** Quando você abre um site no navegador, duas máquinas (ou dois programas, às vezes na mesma máquina, como será o caso deste tutorial) conversam entre si com papéis diferentes. O **cliente** é quem inicia a conversa pedindo alguma coisa — o navegador, o Postman, um app de celular, ou até outro servidor agindo como cliente de um terceiro. O **servidor** é quem fica esperando por pedidos e responde a cada um. Uma API como a que você vai construir neste tutorial é, tecnicamente, um servidor: um programa que fica ligado, escutando por conexões numa porta de rede, e respondendo a cada requisição que chega. Um detalhe importante para não confundir mais à frente: "cliente" e "servidor" são papéis, não tipos fixos de programa — o frontend React que você vai construir no Tópico 3 é o **cliente** da sua API, mesmo sendo ele próprio servido por outro programa (o Vite) enquanto você o desenvolve.

**O que é HTTP.** HTTP (*HyperText Transfer Protocol*) é o conjunto de regras que cliente e servidor seguem para conversar pela web — um protocolo, no sentido de "um formato combinado antecipadamente", parecido com como duas pessoas combinam falar em português em vez de inventar uma língua nova a cada conversa. Toda troca em HTTP segue o mesmo formato básico: o cliente manda uma **requisição** (*request*), contendo um método (o que ele quer fazer: `GET` para ler, `POST` para criar, entre outros — você vai usar vários ao longo deste tutorial), uma URL (o endereço do que ele quer) e, às vezes, um corpo (dados extras, como os campos de um formulário). O servidor devolve uma **resposta** (*response*), contendo um **código de status** (um número de três dígitos dizendo o que aconteceu — `200` para sucesso, `404` para "não encontrado", e vários outros que você vai conhecer ao longo do tutorial) e, geralmente, um corpo com o resultado. Cada requisição HTTP comum é independente: o servidor não "lembra" automaticamente da requisição anterior do mesmo cliente, a menos que alguma técnica explícita (como cookies de sessão, ou o token JWT que você vai construir no Tópico 5) seja usada para carregar esse contexto de uma requisição para a próxima.

**O que é uma API.** Uma API (*Application Programming Interface*) é, de forma geral, qualquer conjunto de pontos de entrada que um programa expõe para que outros programas o usem, sem precisarem saber como ele funciona por dentro — só o "contrato" de entrada e saída importa. Neste tutorial, o termo é usado especificamente para uma **API web**: um servidor que, em vez de devolver páginas HTML prontas para exibição (como um site tradicional), devolve dados estruturados (quase sempre em JSON, explicado a seguir), pensados para serem consumidos por outro programa — um app mobile, um frontend React, outro servidor. É essa distinção que separa o `catalogo-produtos` (uma API) do `catalogo-frontend` (um cliente dessa API, que é quem efetivamente decide como mostrar aqueles dados numa tela).

**O que é JSON.** JSON (*JavaScript Object Notation*) é um formato de texto para representar dados estruturados — números, textos, listas, e objetos com pares chave-valor — de um jeito que tanto humanos quanto programas conseguem ler facilmente. Apesar do nome vir do JavaScript, hoje é um formato universal, suportado por praticamente qualquer linguagem. Um exemplo simples:

```json
{ "nome": "Teclado Mecânico", "preco": 249.90, "disponivel": true }
```

Isso representa um objeto com três campos: um texto (`nome`), um número (`preco`) e um valor verdadeiro/falso (`disponivel`). JSON é o formato que a API deste tutorial usa para responder às requisições — é o que `res.json(...)` (que você vai ver já no Tópico 1) efetivamente devolve ao cliente.

**O que é Node.js.** JavaScript nasceu como a linguagem que roda *dentro* do navegador, para dar interatividade a páginas web — por muito tempo, não existia jeito de rodar JavaScript fora desse ambiente. O Node.js é um **runtime**: um programa que pega o mesmo motor que executa JavaScript dentro do Chrome (o V8) e o disponibiliza fora do navegador, como um interpretador de linha de comando, capaz de ler e escrever arquivos, abrir conexões de rede, e — o que interessa aqui — rodar um servidor HTTP. É graças ao Node.js que este tutorial consegue escrever tanto o backend quanto o frontend na mesma linguagem.

**O que é npm, e o que é um pacote.** Escrever tudo do zero — inclusive coisas já resolvidas por milhares de outros desenvolvedores, como "como montar um servidor HTTP" ou "como validar um token JWT" — seria um desperdício enorme de tempo. Um **pacote** (ou *biblioteca*) é código já pronto, publicado por alguém, que você pode incluir no seu projeto para resolver um problema específico, sem reescrevê-lo. O **npm** (*Node Package Manager*) é a ferramenta de linha de comando, instalada junto com o Node.js, que baixa esses pacotes de um repositório público e os organiza dentro do seu projeto, numa pasta chamada `node_modules`. Quando você rodar `npm install express` no Tópico 1, é isso que vai acontecer: o npm vai baixar o pacote `express` (e tudo que ele precisa para funcionar) e deixá-lo pronto para você importar no seu código.

**O terminal, e o que significa "rodar um comando".** O terminal (também chamado de linha de comando, CLI, ou console) é uma forma de interagir com o computador digitando comandos de texto, em vez de clicar em ícones. Praticamente todo passo deste tutorial que aparece dentro de uma caixa cinza com fundo escuro (como `npm install express`, ou `node src/server.js`) é um comando para você digitar literalmente no terminal e confirmar com Enter — o resultado aparece como texto logo abaixo, e é aí que você vai ver mensagens de sucesso, erros, ou a saída de um programa em execução. Vários passos deste tutorial pedem para você manter um comando rodando num terminal (como o servidor Express) enquanto usa um **segundo terminal** para testar — isso é normal: o primeiro terminal fica "ocupado" enquanto o servidor está ativo, então testes precisam de uma janela própria.

**✅ Checkpoint:** você sabe explicar, com suas próprias palavras, a diferença entre cliente e servidor, o que uma requisição HTTP carrega, e por que uma API devolve JSON em vez de uma página pronta.

## Conceitos fundamentais antes de começar

Com o básico alinhado, vale agora aprofundar quatro ideias mais específicas deste projeto, que aparecem o tempo todo ao longo do material. Elas são intencionalmente curtas aqui — cada uma delas é retomada e aprofundada no tópico onde ela passa a importar de verdade.

**O que é um módulo, e o que é ESM.** Um arquivo JavaScript isolado não sabe nada sobre outros arquivos por padrão — se você quer usar, em `server.js`, uma função definida em `database.js`, precisa de um sistema de módulos que permita "exportar" essa função de um arquivo e "importar" no outro. O Node.js historicamente usava o CommonJS (`require('./database.js')` e `module.exports = ...`), um sistema próprio criado antes de existir um padrão oficial da linguagem. Mais tarde, o JavaScript ganhou um sistema de módulos padronizado pela própria especificação da linguagem, chamado **ES Modules** (ESM), com a sintaxe `import`/`export` que você já deve ter visto em React. Hoje o Node.js suporta os dois, mas um projeto precisa escolher um: ou todo o projeto usa `require`, ou todo o projeto usa `import`. Você verá no Tópico 1 por que este projeto usa ESM.

**O que é REST.** REST (*Representational State Transfer*) é um estilo de arquitetura para APIs HTTP — não uma biblioteca ou protocolo, mas um conjunto de convenções sobre como organizar rotas e respostas. As ideias centrais são: cada "coisa" do seu sistema (um produto, um usuário) é um **recurso**, identificado por uma URL (`/produtos/5`); as ações sobre esse recurso usam os **verbos HTTP** que já existem (`GET` para ler, `POST` para criar, `PUT`/`PATCH` para atualizar, `DELETE` para remover) em vez de inventar uma URL para cada ação (`/produtos/criar`, `/produtos/excluir`); e cada requisição carrega tudo que o servidor precisa para respondê-la, sem depender de "lembranças" de requisições anteriores. O Tópico 4 revisa esses princípios em detalhe, mas você já vai aplicá-los desde o Tópico 1.

**O que é um middleware.** Num servidor Express, uma requisição não vai direto da rota até a resposta — ela pode passar por uma fila de funções intermediárias antes de chegar ao código que efetivamente trata aquela rota. Cada uma dessas funções intermediárias é um **middleware**: uma função que recebe a requisição (`req`), a resposta (`res`) e uma função `next`, faz alguma coisa (ler o corpo da requisição, verificar um token, registrar um log), e então chama `next()` para passar adiante — ou interrompe tudo respondendo diretamente, se algo estiver errado. Você já vai usar seu primeiro middleware (pronto, de uma biblioteca) no Tópico 1, e vai escrever o seu próprio middleware do zero no Tópico 5.

**O padrão MVC, e onde cada parte mora neste projeto.** MVC (*Model-View-Controller*) é um jeito de organizar código que separa três responsabilidades: o **Model** representa os dados e as regras do domínio (o que é um "produto", quais campos ele tem); o **Controller** recebe uma requisição, decide o que fazer, conversa com o Model, e devolve uma resposta; a **View** decide como mostrar o resultado para quem pediu. Em uma aplicação PHP tradicional (como a que você viu em PWII), a View é um template HTML renderizado no próprio servidor. Neste projeto isso muda de forma importante: como o `catalogo-produtos` é uma **API REST pura**, ela não renderiza HTML nenhum — ela só devolve dados em JSON. Isso significa que, no backend, existem Model e Controller, mas **não existe View no sentido clássico**. O papel de "decidir como mostrar os dados" migra inteiro para quem consome a API: o frontend React que você vai construir no Tópico 3 (que decide em quais componentes e com que aparência os dados aparecem), o Postman ou o `curl` (que só mostram o JSON cru), ou a documentação Swagger (que mostra uma visão interativa da API). Concretamente, neste projeto: o **Model** é o arquivo `prisma/schema.prisma` mais o `PrismaClient` gerado a partir dele — é ali que os dados e suas regras (campos, tipos, relacionamentos) são definidos; o **Controller** são as funções em `src/controllers/*.js`, que recebem a requisição, chamam o Prisma, e decidem o status HTTP e o corpo da resposta; a **View**, quando existir, vive no projeto `catalogo-frontend`, em componentes React que recebem esse JSON e decidem como desenhá-lo na tela.

## 1. Integração com Banco de Dados (ORM)

**Objetivo:** criar a API `catalogo-produtos` do zero, com o modelo `Produto` guardado em MySQL através do Prisma ORM.

Um ORM (*Object-Relational Mapper*) traduz entre objetos do código e linhas de tabelas do banco: em vez de escrever `INSERT INTO produtos...`, chama-se um método como `prisma.produto.create(...)`.

### Pré-requisitos

Antes de começar, confirme que as ferramentas certas estão instaladas rodando os três comandos abaixo em um terminal. `node` é o interpretador que executa JavaScript fora do navegador; `npm` é o gerenciador de pacotes que vem junto com ele (usado para instalar bibliotecas); `mysql` é o cliente de linha de comando do banco de dados que este projeto vai usar.

```bash
node --version    # Deve mostrar v20.19+ (recomendado v22.x)
npm --version     # Deve mostrar v10+
mysql --version   # Deve mostrar v8+
```

**✅ Checkpoint:** os três comandos devem retornar versões sem erro.

**Passo 1 — crie o projeto.** Todo projeto Node.js começa com um arquivo `package.json`, que guarda o nome do projeto, suas dependências e alguns metadados. O comando `npm init -y` cria esse arquivo com valores padrão (o `-y` responde "sim" a todas as perguntas que ele faria, para agilizar).

```bash
mkdir catalogo-produtos
cd catalogo-produtos
npm init -y
```

Isso cria o `package.json` com as configurações padrão. Agora é preciso decidir qual sistema de módulos este projeto vai usar — lembre do que foi dito na seção de conceitos fundamentais: ou CommonJS (`require`), ou ESM (`import`). O Prisma 7 exige ESM: o código que ele gera usa `import`/`export`, e não funcionaria dentro de um projeto configurado para CommonJS. Você avisa ao Node.js "este projeto inteiro é ESM" adicionando o campo `"type": "module"` ao `package.json` — a partir daí, todo arquivo `.js` do projeto é interpretado como ESM, e você pode (e deve) usar `import`/`export` em vez de `require`/`module.exports`. Abra o `package.json` e adicione essa linha:

```json
{
  "name": "catalogo-produtos",
  "version": "1.0.0",
  "type": "module",
  "description": "",
  "main": "index.js",
  "scripts": {
    "test": "echo \"Error: no test specified\" && exit 1"
  },
  "keywords": []
}
```

**✅ Checkpoint:** apareceu "Wrote to package.json"? O campo `"type": "module"` foi adicionado? Se sim, prossiga.

**Passo 2 — instale as dependências.** Rode os dois comandos abaixo. O primeiro instala bibliotecas que o projeto precisa para **funcionar em produção**; o segundo instala ferramentas que você só usa **durante o desenvolvimento** (por isso a flag `--save-dev`, que registra essas bibliotecas em `devDependencies` em vez de `dependencies` no `package.json`).

```bash
npm install express dotenv cors mariadb @prisma/client @prisma/adapter-mariadb
npm install prisma zod --save-dev
```

Cada uma dessas bibliotecas tem um papel específico, e vale entender o de cada uma antes de seguir em frente, já que todas vão aparecer em código daqui a pouco:

`express` é o framework que vai receber requisições HTTP e decidir o que fazer com cada uma — é a peça central de todo o backend deste tutorial, e você vai usá-lo já no Passo 8. `dotenv` lê um arquivo `.env` do projeto e carrega seu conteúdo como variáveis de ambiente (`process.env.ALGO`) — é assim que você mantém segredos (senha do banco, chave JWT) fora do código-fonte e fora do controle de versão. `cors` é um middleware Express que controla quais sites podem chamar sua API a partir do navegador — ele resolve um problema chamado CORS, que você vai entender em detalhe no Tópico 3, quando o frontend React tentar conversar com esta API pela primeira vez. `mariadb` 🆕 é o driver de baixo nível que sabe conversar, pela rede, com um banco MySQL ou MariaDB — o Prisma 7 não fala mais diretamente com o banco através de um engine binário embutido, então ele delega essa conversa a um driver JavaScript como este. `@prisma/client` 🆕 continua necessário como dependência de runtime, mesmo o código gerado morando numa pasta própria do projeto (você vai ver isso no Passo 5) — internamente, o código gerado ainda importa utilitários deste pacote. `@prisma/adapter-mariadb` 🆕 é o que chamamos de *driver adapter*: uma ponte que ensina o `PrismaClient` a usar especificamente o driver `mariadb` para se conectar. A partir do Prisma 7, todo banco de dados exige um adapter desses — não existe mais uma conexão "direta" e automática.

Já as dependências de desenvolvimento: `prisma` é a ferramenta de linha de comando (CLI) do Prisma — é ela que cria migrations, gera o cliente e faz introspecção de bancos existentes; você vai chamá-la o tempo todo como `npx prisma <comando>`. `zod` é uma biblioteca de validação de dados: ela permite descrever a "forma" esperada de um objeto (quais campos, de que tipo) e verificar se um dado recebido bate com essa forma — vamos usá-la mais adiante para validar o corpo das requisições antes de tocar o banco.

**✅ Checkpoint:** o terminal mostrou "added X packages" sem erros vermelhos.

**Passo 3 — inicialize o Prisma.** O comando `prisma init` cria a estrutura básica de arquivos que o Prisma precisa para funcionar: onde fica o schema, onde ficam as migrations, e onde estão as variáveis de conexão com o banco.

```bash
npx prisma init
```

Este comando cria os arquivos `prisma/schema.prisma`, `prisma7.config.ts` e `.env`. 🆕 O arquivo de configuração leva o número da versão no nome (`prisma7.config.ts`, não `prisma.config.ts`) — assim, quando a versão 8 chegar, os dois formatos de configuração podem conviver no mesmo projeto durante a migração.

**✅ Checkpoint:** os arquivos existem? Verifique com `ls prisma/ prisma7.config.ts .env`.

**Passo 4 — configure o banco de dados.** Abra o `.env` e altere a linha `DATABASE_URL` para suas credenciais. Essa string segue um formato padrão de conexão: protocolo, usuário, senha, endereço do servidor, porta e nome do banco, tudo em uma única linha.

```
DATABASE_URL="mysql://usuario:senha@localhost:3306/catalogo"
```

Depois, crie o banco no MySQL — o comando abaixo executa um `CREATE DATABASE` diretamente pela linha de comando, sem precisar abrir um cliente SQL interativo.

```bash
mysql -u usuario -psenha -e "CREATE DATABASE IF NOT EXISTS catalogo;"
```

**✅ Checkpoint:** o comando rodou sem erros.

> ⚠️ Se o MySQL/MariaDB do seu computador já é usado há muito tempo (ou foi atualizado de uma versão antiga), rodar `prisma migrate dev` mais adiante pode falhar com o erro `Cannot load from mysql.proc. The table is probably corrupted`. Isso não é um problema do seu schema — é o MariaDB avisando que suas tabelas internas de sistema estão desatualizadas. A correção é rodar, uma única vez, como usuário com privilégios administrativos: `sudo mariadb-upgrade` (ou `mysql_upgrade` em instalações MySQL). Depois disso, `prisma migrate dev` funciona normalmente.

**Passo 5 — defina o primeiro modelo.** Antes de editar os arquivos, vale entender o que cada um faz: o `prisma7.config.ts` diz ao Prisma **onde as coisas estão e como se conectar** (caminho do schema, das migrations, URL do banco); o `prisma/schema.prisma` descreve **a estrutura dos dados em si** (quais tabelas existem, quais campos cada uma tem). Comece pelo primeiro. Abra `prisma7.config.ts` e substitua todo o conteúdo por:

```typescript
import 'dotenv/config'
import { defineConfig, env } from 'prisma/config'

export default defineConfig({
  schema: 'prisma/schema.prisma',
  migrations: {
    path: 'prisma/migrations',
    seed: 'node prisma/seed.js',
  },
  datasource: {
    url: env('DATABASE_URL'),
  },
})
```

🆕 O campo `migrations.seed` substitui o antigo bloco `"prisma": { "seed": ... }` do `package.json` — em Prisma 7 o seed é configurado aqui.

Agora o schema. Ele é dividido em blocos: um `generator`, que diz ao Prisma que código gerar e onde salvar; um `datasource`, que diz que tipo de banco está sendo usado; e um ou mais `model`, cada um descrevendo uma tabela. Abra `prisma/schema.prisma` e substitua todo o conteúdo por:

```prisma
// This is your Prisma schema file,
// learn more about it in the docs: https://pris.ly/d/prisma-schema

generator client {
  provider = "prisma-client"
  output   = "../generated/prisma"
}

datasource db {
  provider = "mysql"
}

model Produto {
  id        Int      @id @default(autoincrement())
  nome      String
  sku       String   @unique
  preco     Decimal  @db.Decimal(10, 2)
  estoque   Int      @default(0)
  createdAt DateTime @default(now())
  updatedAt DateTime @updatedAt

  @@map("produtos")
}
```

🆕 Note que o `generator client` não tem mais `moduleFormat = "cjs"` — o Prisma 7 gera ESM por padrão, e o `output` (antes opcional, gerado dentro de `node_modules`) agora é **obrigatório** e aponta para uma pasta do seu projeto. 🆕 Repare também que o bloco `datasource db` **não tem mais** a linha `url = env("DATABASE_URL")` — em versões anteriores do Prisma essa linha era obrigatória dentro do schema; no Prisma 7 ela foi banida do `schema.prisma` (o CLI recusa rodar se você a deixar lá) e migrou de vez para o `datasource.url` do `prisma7.config.ts`, que você já configurou acima.

**A relação entre o schema e o banco de dados.** Cada `model` no schema corresponde a uma **tabela** no banco; cada campo dentro do model corresponde a uma **coluna** dessa tabela; e o tipo de cada campo (`Int`, `String`, `Decimal`, `DateTime`) é traduzido pelo Prisma para o tipo de coluna equivalente no banco escolhido (no MySQL, por exemplo, `String` normalmente vira `VARCHAR`, e `Int` vira `INT`). É essa correspondência model↔tabela e campo↔coluna que o `prisma migrate dev` calcula automaticamente: ele lê o schema, compara com o que já existe no banco, e gera o SQL necessário para that o banco "alcance" o schema — sem que você precise escrever `CREATE TABLE` ou `ALTER TABLE` manualmente. Os pequenos símbolos depois de cada campo (`@id`, `@unique`, `@default(...)`) são atributos que refinam essa tradução, e valem uma explicação linha a linha:

- `@id` → marca esse campo como chave primária da tabela.
- `@default(autoincrement())` → o banco preenche esse valor sozinho, incrementando (1, 2, 3...) a cada novo registro.
- `@unique` → cria uma restrição de unicidade na coluna; o banco recusa dois registros com o mesmo valor.
- `@db.Decimal(10, 2)` → em vez de deixar o Prisma escolher o tipo de coluna padrão, esse atributo especifica o tipo exato do MySQL: `DECIMAL(10, 2)`, ou seja, até 10 dígitos no total, sendo 2 depois da vírgula — o tipo correto para dinheiro, porque evita os erros de arredondamento de ponto flutuante que um `Float` teria.
- `@default(now())` → preenche a data automaticamente com o momento da criação.
- `@updatedAt` → atualiza a data automaticamente toda vez que o registro sofre um `update`.
- `@@map("produtos")` → por padrão, o Prisma nomearia a tabela `Produto` (igual ao model); esse atributo renomeia a tabela real no banco para `produtos`, seguindo a convenção SQL de nomes de tabela em minúsculo e no plural, enquanto o model no código continua se chamando `Produto` (singular, PascalCase) para soar natural em JavaScript.

### Por que um arroba e dois arrobas?

No Prisma, um arroba (`@`) e dois arrobas (`@@`) têm significados diferentes:
- `@`: atributo de um **campo** (*field attribute*) — aplica-se a uma única propriedade, como `@id` ou `@unique` em `sku`.
- `@@`: atributo do **modelo inteiro** (*model attribute*) — aplica-se à tabela como um todo, como `@@map("produtos")`.

| Sintaxe | Aplica-se a | Exemplo |
|---|---|---|
| `@id` | Campo | `id Int @id` |
| `@default()` | Campo | `idade Int @default(18)` |
| `@unique` | Campo | `sku String @unique` |
| `@@map()` | Modelo | `@@map("produtos")` |
| `@@index()` | Modelo | `@@index([nome])` |
| `@@unique()` | Modelo | `@@unique([sku, nome])` |
| `@@id()` | Modelo | `@@id([empresaId, usuarioId])` |

Essa sintaxe foi inspirada em linguagens com anotações/atributos, como Java (`@Override`) e C# (`[Key]`): ao ver um único `@`, você sabe que ele modifica apenas aquela coluna; ao ver `@@`, sabe que a configuração envolve a tabela inteira.

**✅ Checkpoint:** salve o arquivo e verifique se não há erros de sintaxe.

**Passo 6 — crie a tabela no banco (migration).** Uma *migration* é um arquivo SQL gerado automaticamente que leva o banco de um estado para outro — no seu caso, do estado "banco vazio" para o estado "banco com a tabela `produtos`". O comando `prisma migrate dev` faz três coisas de uma vez: compara o schema com o banco, gera o SQL da diferença, e já executa esse SQL no banco. O sufixo `dev` não é decorativo — ele existe porque este comando é pensado para o dia a dia de desenvolvimento: ele roda de forma interativa (pode fazer perguntas se detectar uma mudança arriscada, como apagar uma coluna com dados) e mantém um histórico de migrations no projeto. Em produção, existe um comando irmão, `prisma migrate deploy`, que só aplica migrations já existentes, sem gerar nada novo e sem perguntas — o tipo de comportamento previsível que você quer rodar automaticamente num servidor, sem alguém respondendo prompts.

```bash
npx prisma migrate dev --name create_produtos
```

O Prisma comparou o schema com o banco, gerou o SQL necessário, executou-o criando a tabela, e salvou a migration em `prisma/migrations/`.

🆕 Em Prisma 7, `migrate dev` não roda mais `prisma generate` automaticamente — ou seja, o passo que gera o código JavaScript/TypeScript que você importa no seu programa (o `PrismaClient`) agora precisa ser chamado à parte. Gere o client explicitamente:

```bash
npx prisma generate
```

Se quiser conferir exatamente o SQL que o Prisma gerou (útil para aprender o que está acontecendo por trás da abstração), o arquivo fica salvo dentro da pasta da migration:

```bash
cat prisma/migrations/*/migration.sql
```

**✅ Checkpoint:** apareceu "Your database is now in sync" e o `prisma generate` rodou sem erros.

**Passo 7 — popule o banco com dados de teste (seed).** Um *seed* é um script que insere dados iniciais no banco — útil para ter algo para testar sem precisar cadastrar tudo manualmente toda vez que o banco é recriado. Crie o arquivo `prisma/seed.js`:

```javascript
import { PrismaClient } from '../generated/prisma/client.ts'
import { PrismaMariaDb } from '@prisma/adapter-mariadb'

const adapter = new PrismaMariaDb({
  host: 'localhost',
  user: 'usuario',
  password: 'senha',
  database: 'catalogo',
})
const prisma = new PrismaClient({ adapter })

async function main() {
  const produtos = [
    { nome: 'Teclado Mecânico', sku: 'TEC-001', preco: 249.90, estoque: 15 },
    { nome: 'Mouse Sem Fio', sku: 'MOU-002', preco: 89.90, estoque: 30 },
    { nome: 'Monitor 24"', sku: 'MON-003', preco: 799.00, estoque: 8 },
  ]

  for (const p of produtos) {
    await prisma.produto.create({ data: p })
    console.log(`Criado: ${p.nome}`)
  }
}

main()
  .then(() => prisma.$disconnect())
  .catch(e => { console.error(e); prisma.$disconnect(); process.exit(1) })
```

🆕 O seed agora usa `import`/`export` (ESM) em vez de `require`/`module.exports`, e instancia o `PrismaClient` passando o `adapter` — não existe mais `new PrismaClient()` sem adapter. Repare em dois detalhes que fogem do que a maioria dos tutoriais por aí mostra, e que só aparecem ao rodar o código de verdade:

- O import aponta para `client.ts`, **não** `client.js` — o gerador do Prisma 7 produz apenas arquivos TypeScript dentro de `generated/prisma/`, mesmo em um projeto que não usa TypeScript em nenhum outro lugar. Isso funciona porque o Node.js moderno (22+) sabe interpretar arquivos `.ts` simples nativamente, sem precisar instalar `ts-node` ou compilar nada — mas o caminho do import precisa apontar para o arquivo real, com a extensão `.ts`.
- `PrismaMariaDb` **não aceita** `{ connectionString: ... }` como os adapters de outros bancos (Postgres, SQLite) aceitam — ele espera os campos separados (`host`, `user`, `password`, `database`). Passar uma connection string faz o adapter criar o pool sem erro aparente, mas toda consulta trava até estourar em `pool timeout` — um erro enganoso, porque parece que o banco está fora do ar quando na verdade é só a forma de configuração que está errada.

Como configuramos `migrations.seed` no `prisma7.config.ts`, não é mais necessário adicionar um bloco `"prisma": { "seed": ... }` no `package.json`. Execute o seed com o comando abaixo — ele lê a configuração do `prisma7.config.ts` para saber qual script rodar, em vez de você precisar lembrar o caminho manualmente:

```bash
npx prisma db seed
```

🆕 O comando de seed mudou de `node prisma/seed.js` direto para `npx prisma db seed`.

**✅ Checkpoint:** apareceu "Criado: Teclado Mecânico", "Criado: Mouse Sem Fio"...

**Passo 8 — crie o servidor Express.** Um servidor HTTP é um programa que fica escutando por conexões numa porta de rede e responde a cada requisição recebida. `express()` cria uma dessas instâncias, com uma API simples para registrar "o que fazer quando chegar uma requisição GET/POST/etc. em tal URL". O `express.json()` que você vê logo abaixo é o seu **primeiro middleware**: ele intercepta toda requisição antes dela chegar às suas rotas, lê o corpo (se houver) como texto, tenta interpretá-lo como JSON e, se conseguir, disponibiliza o resultado em `req.body` — sem esse middleware, `req.body` ficaria `undefined` mesmo que o cliente tenha enviado um JSON válido. Crie `src/server.js`:

```javascript
import express from 'express'
const app = express()

app.use(express.json())

app.get('/health', (req, res) => {
  res.json({ status: 'ok', message: 'API funcionando!' })
})

const PORT = 3000
app.listen(PORT, () => {
  console.log(`Servidor rodando em http://localhost:${PORT}`)
})
```

🆕 `require('express')` virou `import express from 'express'` — necessário porque o `package.json` agora tem `"type": "module"`.

Rode o servidor:

```bash
node src/server.js
```

Com o servidor rodando, abra um segundo terminal (deixe o primeiro rodando o servidor) e teste a rota que você acabou de criar:

```bash
curl http://localhost:3000/health
```

**✅ Checkpoint:** o curl retornou `{"status":"ok","message":"API funcionando!"}`.

**Passo 9 — conecte o Prisma ao servidor.** Até aqui o Prisma só foi usado pelo script de seed, isoladamente. Agora ele precisa estar disponível dentro do servidor Express, para que as rotas possam consultar e alterar dados. Em vez de instanciar o `PrismaClient` toda vez que uma rota precisar dele, é mais eficiente criar **uma única instância compartilhada** e exportá-la de um módulo próprio — assim todo o projeto reaproveita a mesma conexão. Crie `src/database.js`:

```javascript
import { PrismaClient } from '../generated/prisma/client.ts'
import { PrismaMariaDb } from '@prisma/adapter-mariadb'
import 'dotenv/config'

const adapter = new PrismaMariaDb({
  host: 'localhost',
  user: 'usuario',
  password: 'senha',
  database: 'catalogo',
})
const prisma = new PrismaClient({ adapter })

export default prisma
```

🆕 O import não vem mais de `@prisma/client` — vem do caminho gerado (arquivo `client.ts`), e o client é criado com o adapter do MariaDB obrigatoriamente, passando os campos de conexão separadamente (não uma connection string), pelos mesmos motivos explicados no Passo 7.

Agora vem o **Controller** — lembrando da seção de conceitos fundamentais: é aqui que a lógica de "o que fazer quando chega uma requisição" mora. Cada função abaixo recebe a requisição (`req`) e a resposta (`res`), conversa com o Model (o `prisma` importado de `database.js`) para buscar ou alterar dados, e decide o que devolver. Crie `src/controllers/produtoController.js`:

```javascript
import prisma from '../database.js'

export async function listar(req, res) {
  const produtos = await prisma.produto.findMany()
  res.json(produtos)
}

export async function criar(req, res) {
  const { nome, sku, preco, estoque } = req.body
  const produto = await prisma.produto.create({
    data: { nome, sku, preco, estoque }
  })
  res.status(201).json(produto)
}
```

Com o Controller pronto, falta ligar cada função a uma combinação de verbo HTTP + URL — isso é feito registrando **rotas** no Express. Abra `src/server.js` e adicione:

```javascript
import { listar, criar } from './controllers/produtoController.js'

app.get('/produtos', listar)
app.post('/produtos', criar)
```

🆕 Note as extensões `.js` explícitas nos imports relativos (`'../database.js'`, `'./controllers/produtoController.js'`) — obrigatórias em ESM (diferente do CommonJS, que resolvia sem extensão).

Reinicie o servidor (pare com `Ctrl+C` e rode `node src/server.js` de novo) e teste as duas rotas novas:

```bash
curl http://localhost:3000/produtos
```

```bash
curl -X POST http://localhost:3000/produtos \
  -H "Content-Type: application/json" \
  -d '{"nome":"Novo","sku":"NOV-004","preco":19.90,"estoque":5}'
```

**✅ Checkpoint:**
- `GET /produtos` retornou uma lista com os 3 produtos do seed.
- `POST /produtos` retornou o novo produto com status 201.

**Passo 10 — adicione busca por ID e exclusão.** Siga o mesmo padrão do Controller: cada função só cuida de uma responsabilidade — buscar um produto específico pelo `id` da URL, ou excluí-lo. Repare que `buscarPorId` trata explicitamente o caso de o produto não existir (`404`), em vez de deixar o Prisma devolver `null` sem tratamento. Adicione as duas funções em `src/controllers/produtoController.js`:

```javascript
export async function buscarPorId(req, res) {
  const { id } = req.params
  const produto = await prisma.produto.findUnique({ where: { id: Number(id) } })

  if (!produto) {
    return res.status(404).json({ erro: 'Produto não encontrado' })
  }
  res.json(produto)
}

export async function deletar(req, res) {
  const { id } = req.params
  await prisma.produto.delete({ where: { id: Number(id) } })
  res.json({ mensagem: 'Produto excluído' })
}
```

Agora registre as duas rotas correspondentes. `:id` na URL é um parâmetro dinâmico — o Express captura o que vier naquela posição e disponibiliza em `req.params.id` (sempre como string, por isso o `Number(id)` dentro do controller). Atualize o bloco de imports e rotas em `src/server.js`:

```javascript
// src/server.js (acrescentar)
import { listar, criar, buscarPorId, deletar } from './controllers/produtoController.js'

app.get('/produtos/:id', buscarPorId)
app.delete('/produtos/:id', deletar)
```

Com o servidor reiniciado, teste o ciclo completo: buscar um produto existente, excluí-lo, e confirmar que ele realmente sumiu.

```bash
curl http://localhost:3000/produtos/1
```

```bash
curl -X DELETE http://localhost:3000/produtos/1
```

```bash
curl http://localhost:3000/produtos/1   # confirma 404
```

**✅ Checkpoint:** `GET /produtos/1` retorna os dados; `DELETE` retorna sucesso; `GET` de novo retorna 404.

**Passo 11 — veja o Prisma Studio.** O Prisma Studio é uma interface gráfica, servida localmente no navegador, para inspecionar e editar os dados do banco sem escrever SQL nem passar por sua API — útil para depurar rapidamente "o que tem no banco agora" enquanto você desenvolve.

```bash
npx prisma studio
```

Isso abre uma interface gráfica no navegador em `http://localhost:5555`, onde você pode ver, adicionar, editar e excluir registros em tempo real.

**✅ Checkpoint:** o navegador abriu mostrando a tabela "Produtos" com os dados.

### Resumo do que você construiu

```
✅ Projeto Node.js configurado em ESM ("type": "module")
✅ Prisma ORM 7 conectado ao MySQL via driver adapter (mariadb + @prisma/adapter-mariadb)
✅ Modelo Produto definido com campos e tipos corretos (Decimal para dinheiro)
✅ Migration executada (tabela criada)
✅ Client gerado explicitamente com prisma generate
✅ Seed configurado via prisma7.config.ts e executado com prisma db seed
✅ Servidor Express rodando na porta 3000
✅ GET /produtos, GET /produtos/:id, POST /produtos, DELETE /produtos/:id
✅ Prisma Studio — interface visual
```

**Exercícios:**
- *Fácil:* implemente `PUT /produtos/:id` — atualizar nome, sku, preço e estoque.
- *Médio:* valide que, se o SKU já existir, a API retorne erro 409 em vez de deixar o Prisma lançar uma exceção não tratada.
- *Difícil:* implemente `GET /produtos?nome=Mouse` filtrando produtos pelo nome usando `contains`, e um soft delete (campo `deletedAt`) em vez de exclusão real.

**Perguntas para fixação:**
1. Por que `preco` usa o tipo `Decimal` do banco em vez de `Float`?
2. O que o driver adapter (`@prisma/adapter-mariadb`) resolve que antes, em versões anteriores do Prisma, não era necessário configurar manualmente?

## Prisma com bancos de dados diferentes

Esta seção não faz parte da sequência numerada — ela é uma referência para duas situações que vão aparecer com frequência fora do ambiente controlado deste tutorial: herdar um banco que já existe, e trocar de motor de banco de dados no meio de um projeto.

### Adaptando o Prisma a um banco que já existe

Até aqui, o fluxo foi sempre "o schema manda": você descreve os models no `schema.prisma`, e `prisma migrate dev` cria as tabelas no banco a partir dele. Mas em muitos projetos reais o banco já existe antes do Prisma entrar em cena — um sistema legado, um banco compartilhado com outras equipes, ou simplesmente um banco que você herdou sem a documentação do schema. Para esses casos existe o caminho inverso: a **introspecção**, em que o Prisma lê a estrutura real do banco e gera o `schema.prisma` a partir dela, em vez do contrário.

O comando é `prisma db pull`. Ele se conecta ao banco apontado por `DATABASE_URL`, examina todas as tabelas, colunas, chaves e relacionamentos, e escreve (ou atualiza) os `model`s correspondentes no seu `schema.prisma`.

```bash
npx prisma db pull
```

Depois de rodar isso, gere o client normalmente (`npx prisma generate`) para poder usar esses models recém-descobertos no seu código, exatamente como fez com `Produto` no Passo 6. Duas ressalvas importantes sobre esse fluxo: primeiro, o Prisma nomeia os models a partir dos nomes reais das tabelas, então se o banco usa `tb_produtos` em vez de `produtos`, o resultado inicial não vai seguir a convenção `Produto` + `@@map("produtos")` que este tutorial usa — você pode ajustar isso manualmente depois. Segundo, a introspecção não sabe "intenção de negócio": ela reconstrói a estrutura, mas não substitui o entendimento de por que aquelas tabelas existem daquele jeito.

### Trocando o banco de dados: de MySQL/MariaDB para SQLite

Trocar de motor de banco é uma tarefa comum o suficiente (mover um protótipo de MySQL para um banco mais simples, por exemplo) para valer a pena entender exatamente o que muda. Não existe um comando único "migre meu banco" — são três mudanças distintas, e nenhuma delas move os dados automaticamente.

A primeira mudança é o `provider` no `datasource` do `schema.prisma`: trocar `"mysql"` por `"sqlite"`. Junto com isso, alguns atributos `@db.*` específicos do MySQL (como o `@db.Decimal(10, 2)` que você usou em `preco`) podem não ter equivalente direto no novo banco — o SQLite, por exemplo, não tem um tipo `DECIMAL` nativo com precisão fixa, então normalmente se usa `Float` ou se guarda o valor como inteiro (em centavos) para evitar problemas de arredondamento. Isso significa reler o schema campo a campo ao trocar de banco, não só trocar uma palavra.

A segunda mudança é o *driver adapter*: cada banco tem o seu, e eles não são intercambiáveis. Para SQLite, em vez de `@prisma/adapter-mariadb` você instalaria `@prisma/adapter-better-sqlite3`, e o código de conexão mudaria de host/user/password/database para um caminho de arquivo local:

```bash
npm install @prisma/adapter-better-sqlite3 better-sqlite3
```

```javascript
import { PrismaBetterSQLite3 } from '@prisma/adapter-better-sqlite3'

const adapter = new PrismaBetterSQLite3({ url: 'file:./dev.db' })
```

A terceira mudança é a mais fácil de esquecer: como o novo banco é, na prática, um banco **diferente e vazio** (mesmo que seja o "mesmo projeto"), o Prisma não tem como saber que você já tinha migrations aplicadas em outro banco — é preciso rodar `prisma migrate dev` de novo, desta vez contra o SQLite, para que ele crie as tabelas do zero seguindo o schema atualizado. Nenhum dado é transferido automaticamente entre um banco e outro: se você precisa levar os dados existentes junto, isso é uma tarefa separada (exportar do banco antigo, transformar se necessário, importar no novo) — o Prisma cuida da estrutura, não do transporte de dados entre motores diferentes.

## 2. CRUD com ORM

**Objetivo:** modelar o relacionamento 1:N entre `Produto` e `Categoria`, e usar `include`, paginação e `connectOrCreate` — o "CRUD avançado" que fecha o ciclo do ORM.

**Pré-requisitos:** projeto `catalogo-produtos` da Integração com Banco de Dados, com o modelo `Produto` funcionando.

**✅ Checkpoint:** `GET /produtos` retorna a lista de produtos do seed.

**Passo 1 — adicione o modelo `Categoria` e o relacionamento.** Um relacionamento 1:N (um-para-muitos) descreve o caso em que um registro de um lado pode estar associado a vários registros do outro — aqui, uma categoria pode ter vários produtos, mas cada produto pertence no máximo a uma categoria. No Prisma, esse relacionamento é declarado nos dois lados: no lado "muitos" (`Categoria`), um campo que é uma **lista** do outro model. Em `prisma/schema.prisma`, adicione:

```prisma
model Categoria {
  id       Int       @id @default(autoincrement())
  nome     String    @unique
  produtos Produto[]

  @@map("categorias")
}
```

E no lado "um" (`Produto`), um campo de chave estrangeira (`categoriaId`) mais um campo de relação que aponta para ele — é essa combinação que o Prisma usa para saber como ligar as duas tabelas. Atualize o model `Produto`:

```prisma
model Produto {
  id          Int        @id @default(autoincrement())
  nome        String
  sku         String     @unique
  preco       Decimal    @db.Decimal(10, 2)
  estoque     Int        @default(0)
  categoriaId Int?
  categoria   Categoria? @relation(fields: [categoriaId], references: [id])
  createdAt   DateTime   @default(now())
  updatedAt   DateTime   @updatedAt

  @@map("produtos")
}
```

Vale notar a diferença entre os dois campos novos em `Produto`: `produtos Produto[]` em `Categoria` → lado "muitos" do relacionamento, existe só no código (não vira coluna no banco) — é só uma forma conveniente de navegar do lado Categoria para seus Produtos. Já `categoriaId Int?` + `@relation(fields: [categoriaId], references: [id])` → é isso que vira a coluna de FK real na tabela `produtos`. O `?` torna o campo opcional (um produto pode existir sem categoria ainda).

**✅ Checkpoint:** salve o arquivo e confira que não há erro de sintaxe.

**Passo 2 — rode a migration.** Assim como no Tópico 1, o Prisma compara o schema (agora com o novo model e o novo campo) contra o banco atual, e gera o SQL necessário — desta vez, criar a tabela `categorias` e adicionar a coluna `categoriaId` (com sua chave estrangeira) em `produtos`.

```bash
npx prisma migrate dev --name add_categoria
```

Como visto no Tópico 1, gerar a migration não regenera o client automaticamente — rode isso também:

```bash
npx prisma generate
```

**✅ Checkpoint:** apareceu "Your database is now in sync" e a tabela `categorias` foi criada.

**Passo 3 — popule categorias e associe produtos.** O seed atual só cria produtos soltos, sem categoria. Para testar o relacionamento, é preciso criar as categorias primeiro (guardando o `id` que o banco gerou para cada uma) e então referenciar esse `id` ao criar os produtos. Atualize `prisma/seed.js`, adicionando este trecho antes da lista de produtos:

```javascript
const perifericos = await prisma.categoria.create({ data: { nome: 'Periféricos' } })
const monitores = await prisma.categoria.create({ data: { nome: 'Monitores' } })

const produtos = [
  { nome: 'Teclado Mecânico', sku: 'TEC-001', preco: 249.90, estoque: 15, categoriaId: perifericos.id },
  { nome: 'Mouse Sem Fio', sku: 'MOU-002', preco: 89.90, estoque: 30, categoriaId: perifericos.id },
  { nome: 'Monitor 24"', sku: 'MON-003', preco: 799.00, estoque: 8, categoriaId: monitores.id },
]
```

Com o seed atualizado, rode-o de novo para popular as novas categorias e associar os produtos a elas:

```bash
npx prisma db seed
```

**✅ Checkpoint:** `npx prisma studio` mostra produtos com `categoriaId` preenchido.

**Passo 4 — liste produtos incluindo a categoria (`include`).** Por padrão, uma consulta do Prisma só traz as colunas da própria tabela — mesmo que exista um relacionamento declarado, os dados da tabela relacionada não vêm "de brinde". Isso é proposital: buscar dados relacionados custa uma consulta (ou um `JOIN`) a mais, então o Prisma exige que você peça explicitamente com a opção `include`. Em `src/controllers/produtoController.js`, atualize `listar`:

```javascript
export async function listar(req, res) {
  const produtos = await prisma.produto.findMany({
    include: { categoria: true }
  })
  res.json(produtos)
}
```

Sem `include`, o Prisma retorna só as colunas de `produtos` — o objeto `categoria` aninhado só aparece se você pedir explicitamente. Isso evita buscar dados relacionados que você não vai usar.

**✅ Checkpoint:** `GET /produtos` agora retorna cada produto com um objeto `categoria: { id, nome }` aninhado.

**Passo 5 — crie um endpoint de categorias com seus produtos (relação inversa).** O mesmo princípio do `include` vale no sentido contrário: partindo de uma categoria, é possível trazer a lista de produtos associados a ela. Adicione esta função em `src/controllers/produtoController.js`:

```javascript
export async function listarCategorias(req, res) {
  const categorias = await prisma.categoria.findMany({
    include: { produtos: true }
  })
  res.json(categorias)
}
```

Registre a rota correspondente em `src/server.js`:

```javascript
// src/server.js
import { listarCategorias } from './controllers/produtoController.js'
app.get('/categorias', listarCategorias)
```

**✅ Checkpoint:** `GET /categorias` retorna cada categoria com o array `produtos` de todos os produtos daquela categoria.

**Passo 6 — pagine a listagem de produtos.** Devolver **todos** os produtos de uma vez funciona enquanto o catálogo é pequeno, mas não escala — paginação é a técnica de devolver só um "pedaço" dos resultados por vez, junto com informação suficiente para o cliente pedir o próximo pedaço. Atualize `listar` para aceitar os parâmetros de página vindos da URL (`req.query`):

```javascript
export async function listar(req, res) {
  const pagina = Number(req.query.pagina) || 1
  const porPagina = Number(req.query.porPagina) || 10

  const produtos = await prisma.produto.findMany({
    include: { categoria: true },
    skip: (pagina - 1) * porPagina,
    take: porPagina,
  })

  const total = await prisma.produto.count()

  res.json({ pagina, porPagina, total, produtos })
}
```

Três peças novas nesse código: `skip` diz ao banco quantos registros pular antes de começar a devolver resultados (calculado a partir da página pedida); `take` limita quantos registros vêm no máximo; e `count()` é uma consulta **separada**, que conta o total de produtos no banco (ignorando a paginação) — sem ela, o cliente não teria como saber quantas páginas existem no total.

```bash
curl "http://localhost:3000/produtos?pagina=1&porPagina=2"
```

**✅ Checkpoint:** a resposta traz só 2 produtos, mais os metadados `pagina`, `total`.

**Passo 7 — crie um produto já associado a uma categoria (*nested write*).** Até agora, associar um produto a uma categoria exigia já saber o `categoriaId` de antemão. Na prática, é comum que o cliente da API só saiba o **nome** da categoria, que pode já existir ou não. O Prisma resolve isso com uma operação chamada *nested write*: uma escrita que, dentro da mesma chamada, também lida com o relacionamento. Atualize `criar`:

```javascript
export async function criar(req, res) {
  const { nome, sku, preco, estoque, categoriaNome } = req.body

  const produto = await prisma.produto.create({
    data: {
      nome, sku, preco, estoque,
      categoria: {
        connectOrCreate: {
          where: { nome: categoriaNome },
          create: { nome: categoriaNome },
        }
      }
    },
    include: { categoria: true }
  })

  res.status(201).json(produto)
}
```

`connectOrCreate` → conecta a uma categoria existente pelo nome, ou cria uma nova se não existir — tudo em uma única operação atômica, sem precisar de duas queries manuais (uma para checar se existe, outra para criar ou conectar).

**✅ Checkpoint:** criar um produto com `"categoriaNome": "Áudio"` (nova) cria a categoria automaticamente e associa o produto.

### Resumo do que você construiu

```
✅ Modelo Categoria com relacionamento 1:N para Produto
✅ Migration aplicando a nova tabela e coluna de FK
✅ include trazendo dados relacionados sob demanda
✅ Endpoint de categorias com a relação inversa (produtos de cada categoria)
✅ Paginação real com skip/take + contagem total
✅ connectOrCreate criando produto já associado a categoria nova ou existente
```

**Exercícios:**
- *Fácil:* implemente `GET /produtos?categoria=Periféricos`, filtrando produtos por categoria.
- *Médio:* implemente `PATCH /produtos/:id/categoria`, atualizando o `categoriaId` de um produto existente, e `GET /produtos?ordenarPor=preco&direcao=desc` usando `orderBy`.
- *Difícil:* implemente `DELETE /categorias/:id`, só permitindo a exclusão se não houver produtos associados àquela categoria.

**Perguntas para fixação:**
1. Por que `produtos Produto[]` em `Categoria` não gera nenhuma coluna na tabela `categorias`?
2. O que `connectOrCreate` evita que fazer manualmente "buscar categoria, se não existir criar, depois criar produto com esse id" não evitaria?

## 3. Introdução a APIs com o Framework

**Objetivo:** consumir a API `catalogo-produtos` a partir de um frontend React separado, usando `fetch` e hooks.

Até agora construímos a API (backend Express/Prisma) — o Model e o Controller do padrão MVC discutido na seção de conceitos fundamentais. Agora entra a peça que faltava: a View. Como já foi dito, esta API não gera HTML nenhuma — a View mora inteiramente neste novo projeto, separado, feito em React.

**Pré-requisitos:** API `catalogo-produtos` das aulas anteriores rodando em `http://localhost:3000`.

**✅ Checkpoint:** `curl http://localhost:3000/produtos` retorna a lista de produtos.

**Passo 1 — crie o projeto React.** Antes de rodar o comando, vale entender duas ferramentas que ele traz consigo: React em si, e o Vite. React é uma biblioteca para construir interfaces a partir de componentes — você já usou React antes neste curso. Vite é diferente: é um **bundler**, ou seja, uma ferramenta que pega o código-fonte dividido em vários arquivos `.jsx`/`.js`/`.css` (que o navegador não sabe carregar de forma eficiente um por um) e o transforma em um pacote otimizado, pronto para o navegador servir. Um bundler resolve três problemas de uma vez: ele entende `import`/`export` entre seus próprios arquivos de componente (o navegador, sozinho, não sabe resolver `import ListaProdutos from './ListaProdutos.jsx'` apontando para um arquivo no seu disco); ele transforma sintaxe que o navegador não entende nativamente (como JSX, a mistura de HTML com JavaScript do React) em JavaScript puro; e ele junta e otimiza tudo isso para produção, reduzindo o tamanho e a quantidade de arquivos que o navegador precisa baixar. Durante o desenvolvimento, o Vite também roda um servidor local que recarrega a página automaticamente a cada alteração salva — é esse servidor que você vai ver em `localhost:5173`. Vite é hoje a opção recomendada para novos projetos React (mais rápido e mais simples de configurar que o antigo Create React App, hoje descontinuado).

```bash
npm create vite@latest catalogo-frontend -- --template react
cd catalogo-frontend
npm install
```

**✅ Checkpoint:** `npm run dev` sobe o projeto em `http://localhost:5173`.

**Passo 2 — habilite CORS na API (backend).** Assim que você tentar chamar `http://localhost:3000` a partir de uma página servida em `http://localhost:5173`, o navegador vai bloquear a resposta — mesmo a API respondendo normalmente. Isso acontece por causa de uma proteção de segurança embutida em todo navegador moderno, chamada **política de mesma origem** (*same-origin policy*): por padrão, uma página só pode ler livremente respostas de requisições feitas para o mesmo "site" (mesmo protocolo, mesmo domínio, mesma porta) que a serviu. `localhost:5173` (o frontend) e `localhost:3000` (a API) contam como **origens diferentes**, mesmo rodando na mesma máquina — a porta já é suficiente para diferenciar. **CORS** (*Cross-Origin Resource Sharing*) é o mecanismo que permite ao servidor **abrir uma exceção** a essa política, dizendo explicitamente ao navegador "está tudo bem, esta origem pode ler minhas respostas". Sem essa autorização, o navegador entrega a resposta ao Express normalmente (a requisição realmente chega e é processada), mas **bloqueia o JavaScript do frontend de ler o resultado** — por isso o erro aparece só no console do navegador, nunca no terminal do backend. No projeto `catalogo-produtos`, confirme que `src/server.js` tem:

```javascript
import cors from 'cors'
app.use(cors())
```

Sem isso, o navegador bloqueia as requisições do frontend (porta 5173) para a API (porta 3000) por política de mesma origem. `cors()`, sem argumentos, libera qualquer origem — cômodo agora, mas você vai restringir isso de propósito no Tópico 10, quando essa liberação geral deixa de ser aceitável.

**✅ Checkpoint:** reinicie a API e confirme que ela continua respondendo normalmente.

**Passo 3 — busque dados com `useEffect` + `fetch`.** `fetch` é a função nativa do navegador (e também do Node.js moderno) para fazer requisições HTTP — é o que substitui, no lado do cliente, o `curl` que você usou até agora para testar a API manualmente. Em `src/App.jsx`:

```jsx
import { useEffect, useState } from 'react'

function App() {
  const [produtos, setProdutos] = useState([])
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState(null)

  useEffect(() => {
    fetch('http://localhost:3000/produtos')
      .then(res => {
        if (!res.ok) throw new Error(`Erro HTTP: ${res.status}`)
        return res.json()
      })
      .then(dados => setProdutos(dados))
      .catch(err => setErro(err.message))
      .finally(() => setCarregando(false))
  }, [])

  if (carregando) return <p>Carregando...</p>
  if (erro) return <p>Erro: {erro}</p>

  return (
    <ul>
      {produtos.map(p => (
        <li key={p.id}>{p.nome} — R$ {p.preco}</li>
      ))}
    </ul>
  )
}

export default App
```

- `useEffect(() => {...}, [])` com array vazio → roda **uma vez**, quando o componente monta (equivalente a "buscar dados ao abrir a tela").
- `.finally()` → garante que `carregando` vira `false` tanto em caso de sucesso quanto de erro.

**✅ Checkpoint:** a página mostra "Carregando...", depois a lista de produtos do banco.

**Passo 4 — envie dados com POST (criar produto).** Para enviar dados (não só ler), `fetch` recebe um segundo argumento com o método, os cabeçalhos e o corpo da requisição:

```javascript
async function criarProduto(dados) {
  const resposta = await fetch('http://localhost:3000/produtos', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(dados),
  })

  if (!resposta.ok) throw new Error('Falha ao criar produto')
  return resposta.json()
}
```

`JSON.stringify` converte o objeto JS em texto JSON; o header `Content-Type: application/json` avisa o Express que o corpo deve ser interpretado assim (é o que `express.json()` no backend espera).

**✅ Checkpoint:** chamando `criarProduto({nome: 'Teste', sku:'T1', preco:10, estoque:1})` no console do navegador, um novo produto é criado no banco.

**Passo 5 — crie um formulário de cadastro controlado.** Um formulário "controlado" em React é aquele cujos valores dos campos vivem no estado do componente (via `useState`), não no DOM — assim o React sempre sabe exatamente o que está digitado, sem precisar ler o DOM manualmente.

```jsx
function FormularioProduto({ aoCriar }) {
  const [nome, setNome] = useState('')
  const [preco, setPreco] = useState('')

  async function enviar(e) {
    e.preventDefault()
    const novoProduto = await criarProduto({
      nome, sku: nome.toUpperCase().slice(0, 5), preco: Number(preco), estoque: 0
    })
    aoCriar(novoProduto)
    setNome('')
    setPreco('')
  }

  return (
    <form onSubmit={enviar}>
      <input value={nome} onChange={e => setNome(e.target.value)} placeholder="Nome" />
      <input value={preco} onChange={e => setPreco(e.target.value)} placeholder="Preço" />
      <button type="submit">Criar</button>
    </form>
  )
}
```

`e.preventDefault()` → impede o comportamento padrão do HTML (recarregar a página ao submeter), deixando o React controlar o envio via `fetch`.

**✅ Checkpoint:** preencher e enviar o formulário cria um produto sem recarregar a página.

**Passo 6 — atualize a lista automaticamente após criar.** Depois que o produto é criado no backend, a lista na tela precisa refletir isso. A forma mais simples seria refazer o `fetch` da lista inteira — mas isso significa uma segunda viagem ao servidor por uma informação que você já tem em mãos (o produto recém-criado, devolvido pelo próprio `POST`). Em vez disso, atualize o estado local diretamente:

```jsx
function App() {
  // ...estado de produtos já existente

  function aoCriarProduto(novoProduto) {
    setProdutos(anteriores => [...anteriores, novoProduto])
  }

  return (
    <>
      <FormularioProduto aoCriar={aoCriarProduto} />
      <ul>{produtos.map(p => <li key={p.id}>{p.nome}</li>)}</ul>
    </>
  )
}
```

Em vez de recarregar a página ou refazer o `fetch` de tudo, adicionamos o novo produto **diretamente no estado** — a UI reage instantaneamente, sem round-trip extra ao servidor.

**✅ Checkpoint:** ao criar um produto pelo formulário, ele aparece na lista imediatamente, sem F5.

### Resumo do que você construiu

```
✅ Projeto React (Vite) consumindo a API Express/Prisma das aulas anteriores
✅ CORS habilitado permitindo comunicação entre frontend e backend
✅ useEffect + fetch buscando dados ao montar o componente
✅ Estados de carregando/erro tratados explicitamente
✅ POST criando produtos via formulário controlado
✅ Atualização otimista da UI sem recarregar a lista inteira
```

**Exercícios:**
- *Fácil:* implemente um botão "Excluir produto" que chama `DELETE` e remove o item da lista local.
- *Médio:* implemente um formulário de edição usando `PATCH`, e mostre um indicador de carregamento só no botão sendo clicado, não na tela inteira.
- *Difícil:* mova `http://localhost:3000` para uma variável de ambiente (`import.meta.env.VITE_API_URL`), configurada em um arquivo `.env` do Vite.

**Perguntas para fixação:**
1. Por que a API precisa de `cors()` mesmo funcionando perfeitamente quando testada com `curl`?
2. O que `useEffect(() => {...}, [])` faria de diferente se o array de dependências fosse omitido por completo?

## 4. Padrões REST (XML/JSON)

**Objetivo:** revisar os princípios REST já aplicados no projeto, corrigir os códigos de status HTTP, e entender por que JSON dominou sobre XML nas APIs modernas.

**Pré-requisitos:** API `catalogo-produtos` funcionando.

**✅ Checkpoint:** `GET /produtos` funciona.

**Passo 1 — revise os princípios REST.** A seção de conceitos fundamentais já apresentou a ideia central de REST — recursos como URLs, verbos como ações, cada requisição autocontida. Agora que você já tem uma API rodando, vale conferir onde cada princípio já apareceu na prática:

| Princípio | O que significa | No seu projeto |
|---|---|---|
| Recursos como URLs | `/produtos`, `/produtos/5` — não `/getProduto?id=5` | Já aplicado |
| Verbos HTTP como ações | GET lê, POST cria, PUT/PATCH atualiza, DELETE remove | Já aplicado |
| Stateless | Cada requisição carrega tudo que precisa (o token JWT) | Já aplicado (a partir do Tópico 5) |
| Códigos de status corretos | 200, 201, 404, 401, 403, 422 — não sempre 200 | Vamos revisar agora |

**✅ Checkpoint:** você consegue apontar, no seu próprio código, onde cada princípio já aparece.

**Passo 2 — audite e corrija os códigos de status das rotas.** Um código de status HTTP é um número de três dígitos que a resposta carrega, indicando de forma padronizada "o que aconteceu" — devolver sempre `200 OK`, mesmo em casos de erro ou de criação, obriga quem consome a API a inspecionar o corpo da resposta só para saber se deu certo. Revise as três rotas principais:

```javascript
router.post('/produtos', autenticar, async (req, res) => {
  const produto = await prisma.produto.create({ data: req.body })
  res.status(201).json(produto)   // 201 Created, não 200
})

router.get('/produtos/:id', async (req, res) => {
  const produto = await prisma.produto.findUnique({ where: { id: Number(req.params.id) } })
  if (!produto) return res.status(404).json({ erro: 'Produto não encontrado' })
  res.json(produto)
})

router.delete('/produtos/:id', autenticar, async (req, res) => {
  await prisma.produto.delete({ where: { id: Number(req.params.id) } })
  res.status(204).send()   // 204 No Content — sem corpo na resposta
})
```

`201` para criação, `204` para exclusão sem corpo de retorno, `404` para recurso inexistente — usar sempre `200` esconde informação do cliente da API.

**✅ Checkpoint:** `DELETE /produtos/1` retorna 204 com corpo vazio.

**Passo 3 — entenda por que JSON venceu XML na prática.** Os dois formatos abaixo carregam exatamente a mesma informação — o mesmo produto, descrito de duas formas:

```json
{ "id": 1, "nome": "Fone Bluetooth", "preco": 149.90 }
```

A versão equivalente em XML é bem mais verbosa para representar a mesma coisa:

```xml
<produto>
  <id>1</id>
  <nome>Fone Bluetooth</nome>
  <preco>149.90</preco>
</produto>
```

JSON é mais compacto, mapeia diretamente para objetos JavaScript (sem parser adicional) e é o formato nativo de `fetch`/`axios`. XML sobrevive em contextos como SOAP e integrações corporativas legadas (assunto do próximo tópico).

**✅ Checkpoint:** você consegue explicar por que `res.json(produto)` não precisa de nenhuma conversão manual.

**Passo 4 — ofereça os dois formatos via *content negotiation*.** *Content negotiation* é a técnica de um mesmo endpoint devolver formatos diferentes de acordo com o que o cliente pede — o cliente sinaliza sua preferência pelo cabeçalho HTTP `Accept`. Para gerar o XML, instale uma biblioteca dedicada a construir documentos XML programaticamente (montar XML na mão, concatenando strings, é propenso a erros de escaping):

```bash
npm install xmlbuilder2
```

Com a biblioteca instalada, ajuste a rota de busca por ID para checar a preferência do cliente e responder no formato correspondente:

```javascript
import { create } from 'xmlbuilder2'

router.get('/produtos/:id', async (req, res) => {
  const produto = await prisma.produto.findUnique({ where: { id: Number(req.params.id) } })
  if (!produto) return res.status(404).json({ erro: 'Produto não encontrado' })

  if (req.accepts('xml')) {
    const produtoPlano = JSON.parse(JSON.stringify(produto))
    const xml = create({ produto: produtoPlano }).end({ prettyPrint: true })
    res.type('application/xml').send(xml)
  } else {
    res.json(produto)
  }
})
```

`req.accepts('xml')` lê o cabeçalho `Accept` da requisição — o mesmo endpoint responde em JSON ou XML dependendo do que o cliente pede, sem duplicar a lógica de busca.

⚠️ O campo `preco` vem do Prisma como um objeto `Decimal` (não um número puro), para preservar a precisão decimal. `res.json()` sabe serializá-lo automaticamente, mas o `xmlbuilder2` não — se você passar o `produto` direto para `create()`, ele tenta percorrer os campos internos do `Decimal` como se fossem parte dos dados e quebra com um erro `[DecimalError] Invalid argument`. Por isso o `JSON.parse(JSON.stringify(produto))`: converte tudo para valores simples (string, number) antes de montar o XML.

**✅ Checkpoint:** `curl -H "Accept: application/xml" http://localhost:3000/produtos/1` retorna XML; `curl -H "Accept: application/json" http://localhost:3000/produtos/1` retorna JSON. Repare que um `curl` sem nenhum header `Accept` explícito também recebe XML — por padrão o curl envia `Accept: */*` ("aceito qualquer formato"), e `req.accepts('xml')` considera isso uma resposta válida em XML.

**Passo 5 — padronize erros no formato REST.** Sem um padrão, cada rota pode devolver erros num formato ligeiramente diferente, obrigando quem consome a API a tratar cada caso à parte. Uma função auxiliar resolve isso:

```javascript
function erroPadrao(res, status, mensagem) {
  res.status(status).json({ erro: mensagem, status })
}

router.post('/produtos', autenticar, async (req, res) => {
  if (!req.body.nome) {
    return erroPadrao(res, 422, 'Campo "nome" é obrigatório')
  }
  // ...
})
```

`422 Unprocessable Entity` → o formato da requisição está correto, mas os dados são semanticamente inválidos (diferente de `400 Bad Request`, para JSON malformado).

**✅ Checkpoint:** `POST /produtos` sem nome retorna 422 com um corpo JSON padronizado.

### Resumo do que você construiu

```
✅ Auditoria e correção dos códigos de status HTTP (200/201/204/404)
✅ Comparação estrutural entre JSON e XML
✅ Content negotiation: mesmo endpoint respondendo em JSON ou XML via Accept header
✅ Função erroPadrao() padronizando o formato de erros da API
```

**Exercícios:**
- *Fácil:* aplique a mesma negociação de conteúdo em `GET /produtos` (lista completa).
- *Médio:* adicione um campo `_links` com a URL do próprio recurso na resposta (HATEOAS básico).
- *Difícil:* pesquise `/v1/produtos` vs header `Accept-Version` como estratégias de versionamento de API, e implemente uma delas.

**Perguntas para fixação:**
1. Por que usar sempre `200` para tudo (mesmo erros) é um problema para quem consome a API?
2. Por que `422` é mais preciso que `400` para "faltou o campo nome"?

## 5. Autenticação JWT

**Objetivo:** proteger a API com autenticação via JSON Web Token (JWT) — o padrão para APIs *stateless* consumidas por SPAs/mobile.

**Pré-requisitos:** API `catalogo-produtos` das aulas anteriores rodando.

**✅ Checkpoint:** `GET /produtos` funciona sem autenticação (ainda).

**Passo 1 — entenda por que sessão não é ideal para APIs.** Sessão depende do servidor guardar estado e do cliente enviar um cookie a cada requisição — funciona bem para aplicações web tradicionais, mas é menos natural para APIs consumidas por apps mobile ou múltiplos frontends. JWT resolve isso: o **token em si** carrega a informação de quem é o usuário, assinado digitalmente, sem o servidor guardar nada.

**✅ Checkpoint:** você entende a diferença central: sessão = estado no servidor; JWT = estado no próprio token.

**A estrutura de um JWT.** Um JWT é uma única string de texto, dividida em três partes separadas por pontos: `cabecalho.payload.assinatura`. O **cabeçalho** (*header*) é um pequeno JSON dizendo qual algoritmo de assinatura foi usado. O **payload** é o JSON com os dados que você decidiu incluir no token — neste tutorial, o `id` e o `email` do usuário. O **assinatura** é o resultado de aplicar uma função criptográfica (usando uma chave secreta que só o servidor conhece) sobre o cabeçalho e o payload juntos. As duas primeiras partes são apenas codificadas em Base64URL, **não criptografadas** — qualquer pessoa pode colar um JWT em um decodificador online e ler o payload perfeitamente. Isso significa duas coisas importantes: nunca coloque dados sensíveis (senha, número de cartão) dentro do payload de um JWT; e a segurança do JWT não vem de esconder o conteúdo, vem da assinatura. Se alguém tentar alterar o payload (trocar o `id` para se passar por outro usuário, por exemplo), a assinatura antiga deixa de bater com o novo conteúdo — e quando o servidor recalcula a assinatura para conferir, a verificação falha. É por isso que o servidor não precisa guardar nada sobre a sessão: ele só precisa da chave secreta para verificar, a cada requisição, se aquele token foi realmente emitido por ele e não foi alterado depois.

**✅ Checkpoint:** você sabe apontar, em um JWT qualquer, onde termina o header, onde termina o payload, e o que é a assinatura.

**Passo 2 — instale as dependências.** Duas bibliotecas cobrem as duas metades do problema: criar/verificar o token, e proteger a senha do usuário antes de guardá-la.

```bash
npm install jsonwebtoken bcryptjs
```

`jsonwebtoken` implementa a criação e a verificação de tokens JWT descritos acima — é ela que calcula a assinatura e que, na hora de verificar, recalcula e compara. `bcryptjs` faz o *hash* de senhas: em vez de guardar a senha do usuário em texto puro no banco (um risco enorme se o banco vazar), você guarda o resultado de uma função de hash de mão única, projetada de propósito para ser lenta e resistente a tentativas de adivinhação por força bruta. É a versão em JavaScript puro do `bcrypt` (sem dependências nativas para compilar, mais simples de instalar).

**✅ Checkpoint:** os pacotes aparecem em `package.json`.

**Passo 3 — adicione o modelo `Usuario` ao schema.** Segue o mesmo padrão dos models anteriores: um campo `email` único (ninguém pode se cadastrar duas vezes com o mesmo email) e um campo `senhaHash`, guardando não a senha em si, mas o resultado do hash que o bcrypt vai calcular no próximo passo.

```prisma
model Usuario {
  id        Int    @id @default(autoincrement())
  nome      String
  email     String @unique
  senhaHash String

  @@map("usuarios")
}
```

Com o model adicionado ao schema, rode a migration para criar a tabela `usuarios` e gere o client de novo:

```bash
npx prisma migrate dev --name add_usuario
```

```bash
npx prisma generate
```

**✅ Checkpoint:** a tabela `usuarios` foi criada.

**Passo 4 — crie o endpoint de registro.** O registro recebe nome, email e senha em texto puro, mas nunca guarda a senha diretamente — ela passa primeiro pelo `bcrypt.hash`. Crie `src/controllers/authController.js`:

```javascript
import bcrypt from 'bcryptjs'
import jwt from 'jsonwebtoken'
import prisma from '../database.js'

const SEGREDO = process.env.JWT_SECRET

export async function registrar(req, res) {
  const { nome, email, senha } = req.body
  const senhaHash = await bcrypt.hash(senha, 10)

  const usuario = await prisma.usuario.create({
    data: { nome, email, senhaHash }
  })

  res.status(201).json({ id: usuario.id, nome: usuario.nome, email: usuario.email })
}
```

`bcrypt.hash(senha, 10)` → `10` é o "cost factor" (rounds de hashing) — quanto maior, mais lento e mais seguro contra ataques de força bruta. `10` é um bom padrão em 2026. Repare que a resposta devolve `id`, `nome` e `email`, mas nunca `senhaHash` — mesmo hasheada, não há motivo para expor esse campo pela API.

Antes de seguir, adicione a chave secreta usada para assinar os tokens ao `.env` — ela deve ser uma string longa e aleatória, e nunca deve ser versionada nem compartilhada:

```
JWT_SECRET=uma-chave-bem-longa-e-aleatoria
```

**✅ Checkpoint:** `POST /auth/registrar` cria o usuário e nunca retorna `senhaHash` na resposta.

**Passo 5 — crie o endpoint de login (gera o token).** O login recebe email e senha, confirma que ambos batem com o que está no banco, e só então emite um token.

```javascript
export async function login(req, res) {
  const { email, senha } = req.body

  const usuario = await prisma.usuario.findUnique({ where: { email } })
  if (!usuario) {
    return res.status(401).json({ erro: 'Credenciais inválidas' })
  }

  const senhaValida = await bcrypt.compare(senha, usuario.senhaHash)
  if (!senhaValida) {
    return res.status(401).json({ erro: 'Credenciais inválidas' })
  }

  const token = jwt.sign(
    { id: usuario.id, email: usuario.email },
    SEGREDO,
    { expiresIn: '2h' }
  )

  res.json({ token })
}
```

⚠️ Sempre retorne a **mesma** mensagem de erro para "email não existe" e "senha errada" — mensagens diferentes permitem que um atacante descubra quais emails estão cadastrados (enumeração de usuários).

`jwt.sign(payload, segredo, opções)` → é aqui que as três partes descritas no início deste tópico são montadas: o `payload` que você passa (`{ id, email }`) vira a segunda parte do token, o `segredo` (a chave do `.env`) é usado para calcular a assinatura, e o resultado final já vem codificado como a string `header.payload.assinatura` pronta para o cliente guardar. `expiresIn: '2h'` embute no próprio payload um campo de expiração — um token roubado para de ser aceito depois de 2 horas, mesmo que o servidor nunca "revogue" nada ativamente.

Registre as duas rotas novas em `src/server.js`:

```javascript
// src/server.js
import { registrar, login } from './controllers/authController.js'
app.post('/auth/registrar', registrar)
app.post('/auth/login', login)
```

**✅ Checkpoint:** `POST /auth/login` com credenciais corretas retorna um token JWT (uma string longa com pontos).

**Passo 6 — crie o middleware de autenticação.** Lembrando da seção de conceitos fundamentais: um middleware é uma função que fica no caminho entre a requisição chegar e a rota ser executada, decidindo se deixa passar (chamando `next()`) ou interrompe a resposta ali mesmo. Até agora você só usou middlewares prontos (`express.json()`, `cors()`) — este é o primeiro que você escreve do zero. A ideia é simples: extrair o token do cabeçalho `Authorization`, verificar sua assinatura, e só chamar `next()` se ele for válido. Crie `src/middlewares/autenticar.js`:

```javascript
import jwt from 'jsonwebtoken'

export function autenticar(req, res, next) {
  const authHeader = req.headers.authorization

  if (!authHeader || !authHeader.startsWith('Bearer ')) {
    return res.status(401).json({ erro: 'Token não fornecido' })
  }

  const token = authHeader.split(' ')[1]

  try {
    const payload = jwt.verify(token, process.env.JWT_SECRET)
    req.usuario = payload
    next()
  } catch (err) {
    return res.status(401).json({ erro: 'Token inválido ou expirado' })
  }
}
```

- Formato padrão do header: `Authorization: Bearer <token>`.
- `jwt.verify` faz o processo inverso de `jwt.sign`: decodifica o payload **e** recalcula a assinatura para conferir se ela bate — se o token foi adulterado ou expirou, a função lança uma exceção, capturada pelo `catch`.
- `req.usuario = payload` → como o Express passa o mesmo objeto `req` adiante para a próxima função na cadeia, guardar o payload ali disponibiliza os dados do usuário logado para o controller que vem depois, sem precisar decodificar o token de novo.

**✅ Checkpoint:** arquivo criado sem erros.

**Passo 7 — proteja as rotas de escrita.** Um middleware só entra em ação em uma rota se você o registrar explicitamente nela — por isso é possível deixar `GET /produtos` pública e proteger só as rotas que alteram dados, passando `autenticar` como um argumento extra antes do controller:

```javascript
// src/server.js
import { autenticar } from './middlewares/autenticar.js'

app.get('/produtos', listar)              // pública
app.post('/produtos', autenticar, criar)  // protegida
app.delete('/produtos/:id', autenticar, deletar)  // protegida
```

Primeiro, confirme que a rota protegida realmente recusa uma requisição sem token:

```bash
# Sem token — deve falhar
curl -X POST http://localhost:3000/produtos -d '{"nome":"X"}' -H "Content-Type: application/json"
```

Agora faça login, copie o token da resposta, e use-o no cabeçalho `Authorization` da mesma requisição:

```bash
# Com token — deve funcionar
TOKEN="cole o token do login aqui"
curl -X POST http://localhost:3000/produtos \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"nome":"Fone Bluetooth","sku":"FON-005","preco":149.90,"estoque":10}'
```

**✅ Checkpoint:** a criação falha com 401 sem token, e funciona normalmente com um token válido.

### Resumo do que você construiu

```
✅ Diferença conceitual entre sessão (stateful) e JWT (stateless)
✅ Estrutura de um JWT: header, payload e assinatura
✅ Registro de usuário com senha hasheada (bcrypt)
✅ Login retornando token assinado com expiração
✅ Mensagem de erro genérica evitando enumeração de usuários
✅ Middleware autenticar() validando o header Authorization: Bearer
✅ Rotas de escrita protegidas, rotas de leitura permanecendo públicas
```

**Exercícios:**
- *Fácil:* crie a rota `GET /auth/me`, retornando os dados do usuário logado usando `req.usuario` do middleware.
- *Médio:* adicione um campo `role` ao usuário e crie um middleware `autorizarAdmin`.
- *Difícil:* gere um segundo token de longa duração (*refresh token*) para renovar o token de acesso sem exigir novo login, e pesquise por que JWT não tem "logout" nativo.

**Perguntas para fixação:**
1. Por que a mesma mensagem de erro é usada tanto para "email não existe" quanto para "senha errada"?
2. Se alguém decodificar o payload de um JWT e alterar o `id` dentro dele, por que isso não é suficiente para se passar por outro usuário?
3. O que `req.usuario = payload`, feito dentro do middleware, permite que as rotas seguintes façam sem precisar decodificar o token de novo?

## 6. Testes de APIs (Postman)

**Objetivo:** testar a API sistematicamente com Postman, incluindo o fluxo completo de autenticação JWT.

**Pré-requisitos:** Postman instalado. API `catalogo-produtos` rodando localmente com autenticação JWT. Registre um usuário de teste antes de começar (`POST /auth/registrar` com, por exemplo, `{"nome":"Loja","email":"loja@catalogo.com","senha":"senha123"}`).

**✅ Checkpoint:** Postman abre e você consegue criar uma nova Collection.

**Passo 1 — crie uma Collection e uma variável de ambiente.** New → Collection → "Catálogo API". Em Environments, crie "Local" com `base_url = http://localhost:3000`.

Usar `{{base_url}}` permite trocar de ambiente sem editar cada requisição.

**✅ Checkpoint:** `{{base_url}}/produtos` resolve corretamente ao enviar a requisição.

**Passo 2 — teste o login e capture o token automaticamente.** Monte a requisição `POST {{base_url}}/auth/login` com o corpo:

```json
{ "email": "loja@catalogo.com", "senha": "senha123" }
```

Na aba Tests dessa mesma requisição, adicione um script que roda automaticamente depois de cada resposta — ele extrai o token do corpo da resposta e o guarda numa variável de ambiente, para ser reaproveitado nas próximas requisições sem copiar e colar manualmente:

```javascript
const resposta = pm.response.json()
pm.environment.set("token", resposta.token)

pm.test("Login retorna token", () => {
    pm.expect(resposta.token).to.be.a("string")
})
```

**✅ Checkpoint:** a variável `token` é preenchida automaticamente após enviar.

**Passo 3 — use o token para criar um produto.** Monte `POST {{base_url}}/produtos`, com o cabeçalho `Authorization: Bearer {{token}}` (reaproveitando a variável salva no passo anterior) e o corpo:

```json
{ "nome": "Teclado Mecânico", "sku": "TEC-010", "preco": 349.90, "estoque": 15 }
```

Na aba Tests, confirme que a criação realmente aconteceu como esperado:

```javascript
pm.test("Status é 201", () => {
    pm.response.to.have.status(201)
})

pm.test("Produto retornado tem id", () => {
    pm.expect(pm.response.json().id).to.exist
})
```

**✅ Checkpoint:** a requisição retorna 201 e ambos os testes passam.

**Passo 4 — teste o caso de erro (validação de preço negativo).** Testar só o caminho de sucesso deixa passar despercebida qualquer falha na validação — envie de propósito um dado inválido:

```json
{ "nome": "Produto Inválido", "sku": "INV-001", "preco": -10, "estoque": 5 }
```

E confirme, na aba Tests, que a API recusa esse dado com o código correto:

```javascript
pm.test("Preço negativo retorna 422", () => {
    pm.response.to.have.status(422)
})
```

Testar o caminho de erro garante que a validação de negócio (preço não pode ser negativo) realmente funciona, não só a "happy path".

**✅ Checkpoint:** o teste passa com 422.

**Passo 5 — encadeie requisições (criar produto e depois excluí-lo).** Assim como o token foi salvo numa variável de ambiente, o `id` de um recurso recém-criado também pode ser salvo e reaproveitado pela próxima requisição da sequência. Na aba Tests da requisição de criação, adicione:

```javascript
pm.environment.set("produto_id", pm.response.json().id)
```

Monte uma nova requisição, `DELETE {{base_url}}/produtos/{{produto_id}}`, com `Authorization: Bearer {{token}}`, e nela confirme o resultado esperado:

```javascript
pm.test("Exclusão retorna 204", () => {
    pm.response.to.have.status(204)
})
```

Encadear criação → exclusão usando o `id` retornado simula um ciclo de vida real do recurso, sem precisar descobrir manualmente qual id usar.

**✅ Checkpoint:** as duas requisições em sequência passam nos testes.

**Passo 6 — rode a Collection inteira automaticamente.** Run → "Catálogo API" → ambiente "Local". O Collection Runner executa todas as requisições em ordem, reaproveitando variáveis de ambiente entre elas.

**✅ Checkpoint:** o relatório final mostra todos os testes passando.

### Resumo do que você construiu

```
✅ Collection com variável de ambiente {{base_url}}
✅ Captura automática do token JWT via script de Tests
✅ Criação de produto autenticada com validação de status 201
✅ Teste do caminho de erro (422 em preço inválido)
✅ Encadeamento criar → excluir reaproveitando o id retornado
✅ Execução completa via Collection Runner
```

**Exercícios:**
- *Fácil:* adicione um teste de listagem, validando que `GET /produtos` retorna um array.
- *Médio:* use um Pre-request Script para gerar um SKU aleatório antes de cada criação, evitando conflito de unicidade.
- *Difícil:* exporte a Collection como JSON e rode-a por linha de comando com `npx newman run catalogo-api.postman_collection.json`, explicando como isso viabiliza integração contínua (CI).

**Perguntas para fixação:**
1. Por que testar a API pelo Postman antes de existir qualquer front-end é útil, e não apenas um passo a mais?
2. O que `pm.environment.set("token", ...)` resolve que copiar e colar o valor manualmente entre requisições não resolveria?

## 7. Web Services (SOAP vs REST, Swagger)

**Objetivo:** entender por que SOAP ainda existe, compará-lo com REST, e documentar a API do catálogo com Swagger/OpenAPI.

**Pré-requisitos:** API `catalogo-produtos` funcionando com autenticação JWT.

**✅ Checkpoint:** `GET /produtos` funciona.

**Passo 1 — entenda o que é SOAP e por que ele ainda existe.** SOAP é um protocolo baseado em XML rígido, com contrato formal (WSDL). Sobrevive em sistemas bancários, governamentais e ERPs legados — não por ser tecnicamente superior, mas porque essas integrações já existem há décadas e migrar tem custo/risco altos. O exemplo abaixo mostra a "forma" de uma chamada SOAP típica, só para reconhecimento visual — o Passo 2 detalha melhor o que cada parte significa:

```xml
<!-- Exemplo de envelope SOAP, só para reconhecimento -->
<soap:Envelope>
  <soap:Body>
    <ConsultarProduto>
      <sku>TEC-010</sku>
    </ConsultarProduto>
  </soap:Body>
</soap:Envelope>
```

**✅ Checkpoint:** você consegue explicar por que um ERP de estoque legado pode ainda expor SOAP hoje.

**Passo 2 — compare SOAP e REST lado a lado.**

| Aspecto | SOAP | REST |
|---|---|---|
| Formato | XML obrigatório | JSON (geralmente), flexível |
| Contrato | WSDL formal e rígido | OpenAPI/Swagger, mais flexível |
| Transporte | Normalmente só HTTP | HTTP com uso pleno de verbos/status |
| Uso típico hoje | Sistemas legados, integrações corporativas | APIs públicas, mobile, SPAs |

**✅ Checkpoint:** você sabe justificar por que o `catalogo-produtos` usa REST.

**Passo 3 — instale e configure o Swagger na API Express.** "Swagger" é o nome popular do ecossistema em torno da especificação **OpenAPI**: um formato padronizado (em JSON ou YAML) para descrever formalmente uma API REST — suas rotas, parâmetros, formatos de resposta — de um jeito que tanto humanos quanto ferramentas conseguem ler. Você vai usar duas bibliotecas com papéis complementares: `swagger-jsdoc` lê comentários especiais (no formato JSDoc) espalhados pelo seu código-fonte e monta, a partir deles, o documento OpenAPI; `swagger-ui-express` pega esse documento e serve uma página web interativa, onde é possível ver e até testar cada rota diretamente pelo navegador.

```bash
npm install swagger-jsdoc swagger-ui-express
```

Comece configurando o `swagger-jsdoc`: ele precisa saber informações básicas da API (título, versão) e onde procurar pelos comentários de documentação no seu código. Crie `src/swagger.js`:

```javascript
// src/swagger.js
import swaggerJsdoc from 'swagger-jsdoc'

export const swaggerSpec = swaggerJsdoc({
  definition: {
    openapi: '3.0.0',
    info: { title: 'Catálogo API', version: '1.0.0' },
  },
  apis: ['./src/routes/*.js'],
})
```

Com a especificação montada, sirva-a através do `swagger-ui-express` — ele expõe uma rota HTML própria, alimentada pelo documento gerado acima. Adicione em `src/server.js`:

```javascript
// src/server.js
import swaggerUi from 'swagger-ui-express'
import { swaggerSpec } from './swagger.js'

app.use('/docs', swaggerUi.serve, swaggerUi.setup(swaggerSpec))
```

`apis: ['./src/routes/*.js']` aponta para onde o Swagger vai procurar comentários de documentação — a partir de agora, vale a pena mover as definições de rota de `server.js` para arquivos próprios em `src/routes/`, um por recurso (`produtoRoutes.js`, `authRoutes.js`), para que o Swagger consiga encontrá-las.

**✅ Checkpoint:** `http://localhost:3000/docs` mostra a Swagger UI.

**Passo 4 — documente a rota de produtos.** A documentação em si vive em comentários especiais, marcados com `@openapi`, escritos diretamente acima da rota que descrevem — é esse comentário que o `swagger-jsdoc` do Passo 3 varre e transforma em especificação:

```javascript
/**
 * @openapi
 * /produtos:
 *   get:
 *     summary: Lista produtos do catálogo
 *     responses:
 *       200:
 *         description: Lista de produtos
 *   post:
 *     summary: Cria um novo produto
 *     security:
 *       - bearerAuth: []
 *     responses:
 *       201:
 *         description: Produto criado
 */
router.get('/produtos', listar)
router.post('/produtos', autenticar, criar)
```

**✅ Checkpoint:** ambas as rotas aparecem documentadas em `/docs`.

**Passo 5 — configure autenticação Bearer e adicione um schema reutilizável.** Duas melhorias na configuração central do Swagger: declarar o esquema de autenticação (para que a UI mostre o botão "Authorize" e envie o token nas requisições de teste) e declarar a estrutura de um `Produto` uma única vez, para referenciá-la em vez de repeti-la em cada rota:

```javascript
export const swaggerSpec = swaggerJsdoc({
  definition: {
    openapi: '3.0.0',
    info: { title: 'Catálogo API', version: '1.0.0' },
    components: {
      securitySchemes: {
        bearerAuth: { type: 'http', scheme: 'bearer', bearerFormat: 'JWT' },
      },
      schemas: {
        Produto: {
          type: 'object',
          properties: {
            id: { type: 'integer' },
            nome: { type: 'string' },
            preco: { type: 'number' },
          },
        },
      },
    },
  },
  apis: ['./src/routes/*.js'],
})
```

Definir `schemas.Produto` uma vez e referenciá-lo (`$ref: '#/components/schemas/Produto'`) em cada rota evita repetir a mesma estrutura de dados em várias descrições.

**✅ Checkpoint:** clicar "Authorize", colar um token válido, e testar `POST /produtos` direto na Swagger UI funciona.

### Resumo do que você construiu

```
✅ Entendimento do que é SOAP e por que sobrevive em sistemas legados
✅ Comparação estruturada SOAP vs REST
✅ Swagger UI servindo documentação interativa em /docs
✅ Rotas GET/POST documentadas via comentários @openapi
✅ Schema reutilizável Produto e autenticação Bearer configurados
```

**Exercícios:**
- *Fácil:* documente as rotas `PATCH`/`DELETE` restantes.
- *Médio:* use `$ref: '#/components/schemas/Produto'` em vez de repetir a estrutura em cada rota documentada.
- *Difícil:* pesquise ferramentas de geração automática de client SDK a partir de uma spec OpenAPI, e gere um client de exemplo para o `catalogo-produtos`.

**Perguntas para fixação:**
1. Por que SOAP exige um WSDL, mas REST não exige nada equivalente para funcionar (mesmo sem documentação nenhuma)?
2. O que a Swagger UI oferece que o próprio código das rotas, sozinho, não oferece a quem vai consumir a API?

## 8. Consumo de APIs externas

**Objetivo:** consumir uma API externa real (cotação de moeda) para exibir preços convertidos, tratando cache e falhas.

**Pré-requisitos:** API `catalogo-produtos` com `Produto` funcionando.

**✅ Checkpoint:** `GET /produtos` retorna a lista normalmente.

**Passo 1 — explore uma API pública de cotação (sem autenticação).** Antes de escrever qualquer código, vale explorar a API externa manualmente, do mesmo jeito que você testou a sua própria API com `curl` desde o Tópico 1:

```bash
curl https://economia.awesomeapi.com.br/json/last/USD-BRL
```

Retorna a cotação atual dólar → real — uma API externa real, fora do seu controle.

**✅ Checkpoint:** o curl retorna um JSON com o campo `bid` (valor da cotação).

**Passo 2 — crie um serviço dedicado para a cotação.** `axios` é uma biblioteca para fazer requisições HTTP a partir do Node.js — o equivalente, no backend, do `fetch` que você já usou no frontend. A diferença prática que importa aqui é a opção `timeout`: sem ela, uma chamada a uma API externa lenta ou travada poderia deixar sua própria API pendurada indefinidamente.

```bash
npm install axios
```

Em vez de chamar a API externa diretamente de dentro do controller, isole essa lógica em um módulo próprio — um **serviço**. Essa separação existe porque uma integração externa tem preocupações diferentes das de um controller (tratar timeout, cache, formato de resposta de terceiros), e mantê-la isolada facilita tanto testar quanto trocar de provedor no futuro sem tocar no resto da API. Crie `src/services/cotacaoService.js`:

```javascript
// src/services/cotacaoService.js
import axios from 'axios'

export async function buscarCotacaoDolar() {
  const { data } = await axios.get('https://economia.awesomeapi.com.br/json/last/USD-BRL', { timeout: 5000 })
  return parseFloat(data.USDBRL.bid)
}
```

Isolar a chamada num serviço próprio facilita trocar de provedor no futuro e centraliza o tratamento de erro dessa integração específica.

**✅ Checkpoint:** `await buscarCotacaoDolar()` retorna um número (ex: 5.42).

**Passo 3 — exiba o preço do produto convertido para dólar.** Com o serviço pronto, a rota só precisa buscar o produto, chamar o serviço, e combinar os dois resultados:

```javascript
router.get('/produtos/:id/preco-usd', async (req, res) => {
  try {
    const produto = await prisma.produto.findUnique({ where: { id: Number(req.params.id) } })
    if (!produto) return res.status(404).json({ erro: 'Produto não encontrado' })

    const cotacao = await buscarCotacaoDolar()
    res.json({ ...produto, precoUsd: (produto.preco / cotacao).toFixed(2) })
  } catch {
    res.status(503).json({ erro: 'Serviço de cotação indisponível' })
  }
})
```

`503 Service Unavailable` é o código correto quando a falha vem de uma dependência externa, não de um erro do próprio cliente da sua API.

**✅ Checkpoint:** `GET /produtos/1/preco-usd` retorna o produto com o campo `precoUsd` calculado.

**Passo 4 — cacheie a cotação para evitar chamadas repetidas.** Cotação de moeda não muda a cada requisição — chamar a API externa toda vez que alguém pedir o preço em dólar de um produto é desperdício, e ainda deixa sua própria API mais lenta e mais dependente da disponibilidade de terceiros. Um cache em memória, com um tempo de expiração curto, resolve isso:

```javascript
let cotacaoCache = { valor: null, expiraEm: 0 }

export async function buscarCotacaoComCache() {
  if (cotacaoCache.valor && Date.now() < cotacaoCache.expiraEm) {
    return cotacaoCache.valor
  }
  const { data } = await axios.get('https://economia.awesomeapi.com.br/json/last/USD-BRL', { timeout: 5000 })
  cotacaoCache = { valor: parseFloat(data.USDBRL.bid), expiraEm: Date.now() + 60_000 }
  return cotacaoCache.valor
}
```

Cotação de moeda não muda a cada requisição — cachear por 60 segundos reduz drasticamente as chamadas à API externa sem prejudicar a precisão para o caso de uso.

**✅ Checkpoint:** fazer duas requisições em menos de 60s gera só uma chamada real à API de cotação.

**Passo 5 — trate o caso de a API externa estar fora do ar (*fallback*).** Mesmo com cache, a primeira chamada (ou a primeira depois do cache expirar) ainda depende da API externa estar no ar. Um *fallback* garante que sua funcionalidade não quebra por completo quando isso não acontece:

```javascript
export async function buscarCotacaoComFallback() {
  try {
    return await buscarCotacaoComCache()
  } catch {
    console.warn('API de cotação indisponível, usando valor de fallback')
    return 5.0 // valor aproximado salvo previamente, só como último recurso
  }
}
```

Um fallback com valor aproximado evita que a funcionalidade inteira quebre por causa de uma dependência externa — mas deve ser usado com cautela e sinalizado (log de warning) para não mascarar o problema real.

**✅ Checkpoint:** simular falha na API externa (ex: desligar a internet) ainda retorna um preço em dólar, usando o fallback.

### Resumo do que você construiu

```
✅ Consumo de API externa de cotação isolado num serviço dedicado
✅ Conversão de preço com tratamento de erro específico (503)
✅ Cache em memória com expiração, reduzindo chamadas repetidas
✅ Fallback com log de aviso para quando a API externa está indisponível
```

**Exercícios:**
- *Fácil:* adicione conversão para EUR além de USD.
- *Médio:* implemente retry, tentando novamente até 2 vezes antes de usar o fallback.
- *Difícil:* pesquise o padrão *circuit breaker* e explique, por escrito, quando ele evitaria sobrecarregar uma API externa instável.

**Perguntas para fixação:**
1. Por que `503` é o código correto para "a API externa está fora do ar", e não `500`?
2. Que problema o cache de 60 segundos resolve que um `try/catch` sozinho não resolveria?

## 9. Internacionalização (i18n)

**Objetivo:** suportar português e inglês na mesma interface do frontend, usando `i18next`/`react-i18next`.

**Pré-requisitos:** frontend React consumindo a API do catálogo.

**✅ Checkpoint:** a lista de produtos aparece na tela.

**Passo 1 — instale `i18next` e `react-i18next`.** `i18next` é o motor de tradução em si: ele guarda os textos de cada idioma e sabe buscar o texto certo dado um idioma ativo. `react-i18next` é a camada de integração que conecta esse motor a componentes React, através de hooks como `useTranslation`.

```bash
npm install i18next react-i18next
```

**✅ Checkpoint:** os pacotes aparecem em `package.json`.

**Passo 2 — crie os arquivos de tradução.** Cada idioma vira um arquivo JSON próprio, com a mesma estrutura de chaves — é assim que o i18next sabe que `produtos.titulo` em português corresponde a `produtos.titulo` em inglês. Comece pelo português, criando `locales/pt.json`:

```json
{
  "produtos": {
    "titulo": "Catálogo de Produtos",
    "preco": "Preço",
    "estoque": "{{quantidade}} em estoque"
  }
}
```

Agora crie o arquivo equivalente em inglês, `locales/en.json`, repetindo exatamente as mesmas chaves com os textos traduzidos:

```json
{
  "produtos": {
    "titulo": "Product Catalog",
    "preco": "Price",
    "estoque": "{{quantidade}} in stock"
  }
}
```

`{{quantidade}}` é um placeholder de interpolação — o valor real é injetado em tempo de tradução, evitando concatenar strings manualmente em cada idioma.

**✅ Checkpoint:** os dois arquivos JSON são válidos (sem erro de sintaxe).

**Passo 3 — configure o i18next.** Com os dois arquivos de tradução prontos, é preciso inicializar o i18next dizendo quais idiomas existem, qual é o idioma inicial, e o que fazer quando uma chave não é encontrada. Crie `i18n.js`:

```javascript
// i18n.js
import i18n from 'i18next'
import { initReactI18next } from 'react-i18next'
import pt from './locales/pt.json'
import en from './locales/en.json'

i18n.use(initReactI18next).init({
  resources: { pt: { translation: pt }, en: { translation: en } },
  lng: 'pt',
  fallbackLng: 'pt',
  interpolation: { escapeValue: false },
})

export default i18n
```

`fallbackLng: 'pt'` garante que, se uma chave não existir no idioma ativo, o português é usado como reserva — evita a UI mostrar a chave crua (`produtos.titulo`) por engano.

**✅ Checkpoint:** importar `./i18n` no `main.jsx` não gera erro no console.

**Passo 4 — use as traduções nos componentes.** Dentro de um componente, o hook `useTranslation()` devolve uma função `t`, que recebe a chave e devolve o texto no idioma ativo no momento:

```jsx
import { useTranslation } from 'react-i18next'

function ListaProdutos({ produtos }) {
  const { t } = useTranslation()

  return (
    <div>
      <h1>{t('produtos.titulo')}</h1>
      {produtos.map((p) => (
        <div key={p.id}>
          <span>{p.nome} — {t('produtos.preco')}: R$ {p.preco}</span>
          <span>{t('produtos.estoque', { quantidade: p.estoque })}</span>
        </div>
      ))}
    </div>
  )
}
```

`t('produtos.titulo')` busca a tradução ativa; `t('produtos.estoque', { quantidade: p.estoque })` passa a variável de interpolação — o mesmo componente funciona em qualquer idioma configurado, sem `if`s de idioma no JSX.

**✅ Checkpoint:** a tela mostra os textos em português por padrão.

**Passo 5 — adicione um seletor de idioma.** Um `<select>` simples, controlado, que chama `i18n.changeLanguage` sempre que o usuário escolhe outro idioma:

```jsx
import { useTranslation } from 'react-i18next'

function SeletorIdioma() {
  const { i18n } = useTranslation()

  return (
    <select value={i18n.language} onChange={(e) => i18n.changeLanguage(e.target.value)}>
      <option value="pt">Português</option>
      <option value="en">English</option>
    </select>
  )
}
```

`i18n.changeLanguage(...)` troca o idioma ativo **globalmente** — todos os componentes usando `useTranslation()` re-renderizam automaticamente com os novos textos, sem recarregar a página.

**✅ Checkpoint:** trocar o seletor para "English" atualiza a tela inteira para os textos em inglês instantaneamente.

### Resumo do que você construiu

```
✅ i18next + react-i18next configurados com dois idiomas
✅ Arquivos de tradução com interpolação de variáveis ({{quantidade}})
✅ fallbackLng evitando chaves cruas aparecerem na UI
✅ Componentes usando t() em vez de texto fixo
✅ Seletor de idioma trocando o app inteiro em tempo real
```

**Exercícios:**
- *Fácil:* use `Intl.NumberFormat` para exibir o preço no formato de moeda local, de acordo com o idioma ativo.
- *Médio:* salve o idioma escolhido em `localStorage` e restaure-o ao recarregar a página.
- *Difícil:* pesquise `t('chave', { count: n })` do i18next e implemente pluralização automática (singular/plural) para o texto de estoque.

**Perguntas para fixação:**
1. Por que `{{quantidade}}` é preferível a montar a frase manualmente com concatenação de string em cada idioma?
2. O que `fallbackLng` evita que aconteceria se uma chave de tradução existisse só em português mas não em inglês?

## 10. Segurança avançada em APIs (CORS, Rate Limiting)

**Objetivo:** configurar CORS restritivo e aplicar rate limiting — proteções essenciais antes de expor uma API para o mundo real.

**Pré-requisitos:** API `catalogo-produtos` com JWT e Swagger funcionando.

**✅ Checkpoint:** `GET /produtos` funciona.

**Passo 1 — reproduza um erro de CORS no navegador.** No Tópico 3 você já viu, por alto, o que é CORS, ao liberar `cors()` sem restrições para o frontend conseguir chamar a API. Agora é hora de revisitar isso com mais profundidade, porque `cors()` sem argumentos — que liberou **qualquer origem** — não é uma configuração aceitável fora de desenvolvimento. Para sentir o problema de novo, abra o frontend React rodando em `http://localhost:5173` chamando a API em `http://localhost:3000` sem nenhuma configuração de CORS no backend. Sem essa autorização, o navegador bloqueia a resposta com um erro no console: "blocked by CORS policy".

CORS (*Cross-Origin Resource Sharing*) é uma proteção do **navegador**, não do servidor — ele impede que JavaScript de um domínio acesse livremente respostas de outro domínio, a menos que o servidor autorize explicitamente.

**✅ Checkpoint:** você reproduz o erro de CORS no console do navegador.

**Passo 2 — configure CORS de forma restritiva.** Em vez de liberar geral, declare exatamente quais origens têm permissão — normalmente, os domínios do(s) seu(s) próprio(s) frontend(s).

```bash
npm install cors
```

Com o pacote já instalado desde o Tópico 3, o que muda agora é a configuração: em vez de chamar `cors()` sem argumentos, passe um objeto restringindo origens e métodos permitidos.

```javascript
import cors from 'cors'

app.use(cors({
  origin: ['http://localhost:5173', 'https://catalogo.etec.br'],
  methods: ['GET', 'POST', 'PATCH', 'DELETE'],
}))
```

`origin: [...]` como uma lista explícita (em vez de `origin: '*'`) garante que só os domínios do seu frontend consigam consumir a API via navegador — qualquer outro site tentando chamar sua API do navegador de um usuário será bloqueado.

**✅ Checkpoint:** o frontend em `localhost:5173` consegue chamar a API sem erro de CORS.

**Passo 3 — entenda por que `origin: '*'` é perigoso com autenticação.** O `*` (coringa) diz ao navegador "qualquer origem pode ler as respostas desta API" — o mesmo comportamento do `cors()` sem argumentos do Tópico 3:

```javascript
// PERIGOSO em produção: qualquer site pode chamar sua API do navegador do usuário
app.use(cors({ origin: '*' }))
```

Com `credentials: true` (necessário para cookies de sessão), `origin: '*'` é **rejeitado pelo próprio navegador** — e mesmo sem cookies, liberar geral facilita ataques que abusam da API a partir de sites maliciosos usando a sessão do usuário logado.

**✅ Checkpoint:** você explica por que `origin: '*'` combinado com autenticação por cookie não funciona nem deveria funcionar.

**Passo 4 — instale e configure rate limiting.** *Rate limiting* é limitar quantas requisições uma mesma origem (normalmente identificada pelo IP) pode fazer em um intervalo de tempo — uma proteção diferente de CORS: CORS controla quem pode *ler* a resposta pelo navegador, rate limiting controla a *quantidade* de requisições, venham de onde vierem (navegador, script, `curl`).

```bash
npm install express-rate-limit
```

`express-rate-limit` é um middleware — mais uma peça que se encaixa no mesmo mecanismo apresentado na seção de conceitos fundamentais e usado desde `express.json()` — que conta requisições por origem e bloqueia quem excede o limite configurado:

```javascript
import rateLimit from 'express-rate-limit'

const limiter = rateLimit({
  windowMs: 15 * 60 * 1000, // 15 minutos
  max: 100, // 100 requisições por IP nesse período
  message: { erro: 'Muitas requisições, tente novamente mais tarde' },
})

app.use('/produtos', limiter)
```

Sem rate limiting, um cliente (malicioso ou com bug) pode fazer milhares de requisições por segundo, sobrecarregando o servidor e afetando todos os outros usuários — `windowMs`/`max` definem quantas requisições cada IP pode fazer num intervalo.

**✅ Checkpoint:** fazer mais de 100 requisições em 15 minutos ao mesmo endpoint retorna a mensagem de limite excedido.

**Passo 5 — configure um limite mais rígido para rotas sensíveis (login).** Nem toda rota deve ter o mesmo limite — uma rota de login é um alvo natural para tentativas automatizadas de adivinhar senha, então merece um limite bem mais apertado que uma rota de leitura comum:

```javascript
const limiterLogin = rateLimit({
  windowMs: 15 * 60 * 1000,
  max: 5, // só 5 tentativas de login por IP a cada 15 min
  message: { erro: 'Muitas tentativas de login, aguarde antes de tentar novamente' },
})

app.use('/auth/login', limiterLogin)
```

Rotas de autenticação merecem um limite **muito mais rígido** que rotas de leitura comum — isso dificulta ataques de força bruta tentando adivinhar senhas por tentativa e erro.

**✅ Checkpoint:** a 6ª tentativa de login em 15 minutos é bloqueada, mesmo com credenciais corretas.

### Resumo do que você construiu

```
✅ Reprodução e entendimento do erro de CORS no navegador
✅ CORS configurado com lista explícita de origens permitidas
✅ Entendimento do risco de origin: '*' combinado com autenticação
✅ Rate limiting geral (100 req/15min) protegendo contra sobrecarga
✅ Rate limiting mais rígido (5 req/15min) protegendo a rota de login contra força bruta
```

**Exercícios:**
- *Fácil:* instale `helmet` e explique o que os headers de segurança adicionados protegem.
- *Médio:* pesquise o que é uma requisição `OPTIONS` automática do navegador (*preflight request*) e por que ela existe.
- *Difícil:* pesquise como limitar por `req.usuario.id` (usuário autenticado) em vez de só por IP, e implemente essa variação para uma rota protegida.

**Perguntas para fixação:**
1. Por que CORS é uma proteção do navegador e não do servidor — o que isso implica sobre chamar a API diretamente via `curl`?
2. Por que a rota de login recebe um limite bem mais baixo (5) que as rotas de leitura comuns (100)?

## 11. Websockets / aplicações em tempo real

**Objetivo:** notificar todos os clientes conectados, em tempo real, quando o estoque de um produto muda — usando Socket.IO.

**Pré-requisitos:** API `catalogo-produtos` com autenticação JWT funcionando.

**✅ Checkpoint:** `PATCH /produtos/:id` funciona.

**Passo 1 — entenda por que HTTP sozinho não basta para tempo real.** Toda a comunicação que você construiu até aqui segue o mesmo formato: o cliente pergunta (uma requisição HTTP), o servidor responde, e a conexão termina — o servidor nunca fala primeiro. Se dois clientes têm a página de um produto aberta e o estoque muda, HTTP não tem como "empurrar" essa mudança para eles — eles teriam que ficar perguntando repetidamente se algo mudou (*polling*), o que desperdiça requisições e ainda tem atraso entre a mudança real e o cliente perceber. Um **WebSocket** é um tipo de conexão diferente: depois de um aperto de mão inicial, ela fica aberta dos dois lados, e tanto cliente quanto servidor podem enviar mensagens a qualquer momento, sem precisar de uma nova requisição para cada uma. **Socket.IO** é uma biblioteca que implementa esse tipo de comunicação sobre WebSocket (com um mecanismo de fallback e reconexão automática por trás), expondo uma API baseada em eventos nomeados — você "emite" um evento com um nome e um dado, e quem está ouvindo aquele nome de evento reage.

**✅ Checkpoint:** você explica por que polling seria pior que WebSocket para esse caso.

**Passo 2 — instale e configure o Socket.IO no servidor.** O Socket.IO não substitui o Express — ele roda **em cima** do mesmo servidor HTTP, então em vez de `app.listen(...)` diretamente, você cria o servidor HTTP explicitamente com `createServer(app)` e anexa tanto o Express quanto o Socket.IO a ele.

```bash
npm install socket.io
```

```javascript
import { createServer } from 'http'
import { Server } from 'socket.io'

const httpServer = createServer(app)
const io = new Server(httpServer, { cors: { origin: '*' } })

io.on('connection', (socket) => {
  console.log('Cliente conectado:', socket.id)
})

httpServer.listen(3000)
```

Socket.IO roda sobre o mesmo servidor HTTP do Express já existente.

**✅ Checkpoint:** o servidor inicia sem erro e o console mostra "Cliente conectado" ao abrir um cliente de teste.

**Passo 3 — crie salas por produto (só quem está vendo aquele produto recebe a atualização).** Emitir um evento para **todos** os clientes conectados, mesmo os que não estão olhando para aquele produto específico, desperdiça tráfego e obriga cada cliente a filtrar o que lhe interessa. O Socket.IO tem um conceito de **sala** (*room*): um agrupamento nomeado de conexões, ao qual você pode emitir seletivamente.

```javascript
io.on('connection', (socket) => {
  socket.on('produto:observar', (produtoId) => {
    socket.join(`produto:${produtoId}`)
  })

  socket.on('produto:parar-observar', (produtoId) => {
    socket.leave(`produto:${produtoId}`)
  })
})
```

Salas por recurso evitam enviar atualizações irrelevantes para clientes vendo outros produtos — mais eficiente que um broadcast global para todos os conectados.

**✅ Checkpoint:** emitir `produto:observar` com um id entra o socket na sala correspondente (verificável no `socket.rooms`).

**Passo 4 — emita um evento quando o estoque muda.** Com as salas prontas, a rota de atualização (`PATCH`) só precisa, depois de salvar no banco, emitir um evento para a sala correspondente àquele produto:

```javascript
router.patch('/produtos/:id', autenticar, async (req, res) => {
  const produto = await prisma.produto.update({
    where: { id: Number(req.params.id) },
    data: req.body,
  })

  io.to(`produto:${produto.id}`).emit('produto:atualizado', produto)

  res.json(produto)
})
```

`io.to(sala).emit(...)` envia o evento só para conexões observando aquele produto específico — clientes vendo outros produtos não recebem nada irrelevante.

**✅ Checkpoint:** atualizar o estoque via API dispara o evento, visível no cliente de teste que está "observando" aquele produto.

**Passo 5 — consuma o evento no frontend React.** Do lado do cliente, `socket.io-client` é a contraparte da biblioteca `socket.io` do servidor — ela implementa o mesmo protocolo de eventos, agora rodando no navegador.

```bash
npm install socket.io-client
```

A lógica de "observar um produto e reagir a atualizações" é reaproveitável entre vários componentes, então vale encapsulá-la num hook customizado:

```javascript
import { useEffect, useState } from 'react'
import { io } from 'socket.io-client'

const socket = io('http://localhost:3000')

function usarEstoqueTempoReal(produtoId, estoqueInicial) {
  const [estoque, setEstoque] = useState(estoqueInicial)

  useEffect(() => {
    socket.emit('produto:observar', produtoId)

    socket.on('produto:atualizado', (produto) => {
      if (produto.id === produtoId) setEstoque(produto.estoque)
    })

    return () => {
      socket.emit('produto:parar-observar', produtoId)
      socket.off('produto:atualizado')
    }
  }, [produtoId])

  return estoque
}
```

O cleanup do `useEffect` (`parar-observar` + `socket.off`) evita vazamento de listeners quando o usuário navega para outra página — importante em apps que abrem/fecham várias páginas de produto durante a sessão.

**✅ Checkpoint:** abrir a página do produto em duas abas e atualizar o estoque numa delas via API reflete em tempo real na outra.

### Resumo do que você construiu

```
✅ Entendimento de por que WebSocket resolve o que polling faz mal
✅ Servidor Socket.IO rodando sobre o mesmo HTTP server do Express
✅ Salas por produto, evitando broadcast desnecessário
✅ Emissão de evento produto:atualizado ao mudar estoque via API
✅ Hook React consumindo o evento com cleanup adequado
```

**Exercícios:**
- *Fácil:* emita um evento diferente quando o estoque ficar abaixo de 5 unidades (notificação de estoque baixo).
- *Médio:* exija um token JWT válido para observar produtos, reaproveitando o middleware de autenticação já existente.
- *Difícil:* pesquise como o Socket.IO lida com quedas de conexão e reconexão automática, e teste esse comportamento desligando/religando a rede.

**Perguntas para fixação:**
1. Por que emitir só para a sala `produto:${id}` é melhor que fazer `io.emit(...)` para todos os conectados?
2. O que o `return () => {...}` dentro do `useEffect` evita, e o que aconteceria sem ele se o usuário navegasse entre várias páginas de produto rapidamente?

## 12. Projeto Final — Início

Aula de início de projeto: hoje você define escopo, não escreve muito código ainda. Ao final, você deve sair com um documento de escopo aprovado e o projeto (backend + frontend) criado.

Todo o conteúdo do semestre (ORM/Prisma, REST, JWT, testes, Swagger, i18n, CORS/rate limiting, WebSockets) converge agora num projeto final individual ou em dupla.

**Passo 1 — escolha o tema da aplicação full-stack.** Requisitos mínimos (podendo reaproveitar o que foi construído no `catalogo-produtos` ao longo do semestre, ou propor um domínio novo):

- API REST com pelo menos 2 entidades relacionadas (Prisma).
- Autenticação JWT protegendo rotas de escrita.
- Documentação Swagger das rotas principais.
- Testes de API (Postman Collection exportável).
- Pelo menos um recurso avançado: WebSocket em tempo real, consumo de API externa, ou i18n.

**✅ Checkpoint:** você tem um tema de aplicação escrito em uma frase, cobrindo os 5 requisitos.

**Passo 2 — escreva o documento de escopo.** Use o template abaixo como ponto de partida — preencha cada seção antes de escrever qualquer código, para que o projeto tenha um norte claro desde o início:

```
# Nome do Sistema: ___________

## Problema que resolve
(1-2 frases)

## Entidades principais e relacionamentos
- ...

## Rotas principais da API
- ...

## Recurso avançado escolhido
- ...

## Frontend
- ...
```

**✅ Checkpoint:** o documento de escopo está preenchido e revisado com o professor.

**Passo 3 — crie o projeto backend.** Repita a mesma sequência do Tópico 1 — inicializar o `package.json`, instalar as dependências e inicializar o Prisma —, desta vez para o novo projeto:

```bash
mkdir projeto-final-api && cd projeto-final-api
npm init -y
npm install express prisma @prisma/client jsonwebtoken bcryptjs
npx prisma init
```

**✅ Checkpoint:** `npx prisma studio` abre sem erro.

**Passo 4 — defina a estrutura de pastas inicial.** Separar rotas, controllers, serviços e middlewares em pastas próprias — o mesmo padrão que o `catalogo-produtos` foi ganhando ao longo do semestre — facilita achar cada peça conforme o projeto cresce:

```
src/
  routes/
  controllers/
  services/
  middlewares/
prisma/
  schema.prisma
```

**✅ Checkpoint:** as pastas existem no projeto, mesmo vazias.

**Passo 5 — planeje os marcos até a apresentação.** Um cronograma simples, com um marco por semana, ajuda a perceber cedo se o projeto está atrasado, em vez de descobrir isso na véspera:

| Marco | Data prevista |
|---|---|
| Schema Prisma + CRUD básico | 1 semana |
| Autenticação JWT funcionando | 2 semanas |
| Recurso avançado + Swagger | 3 semanas |
| Testes + polimento | véspera da apresentação |

**✅ Checkpoint:** você anota suas próprias datas previstas para cada marco.

### Resumo do que você construiu

```
✅ Tema da aplicação final definido, cobrindo os 5 requisitos mínimos
✅ Documento de escopo escrito e revisado
✅ Projeto backend criado com Express + Prisma
✅ Estrutura de pastas inicial definida
✅ Marcos de progresso planejados até a apresentação
```

Nas semanas seguintes, use o conteúdo já visto no semestre para implementar o projeto de forma incremental, marco a marco.

## 13. Projeto Final — Apresentações

Checklist para a apresentação:
- Demonstração ao vivo do CRUD via Postman, incluindo o login JWT e a criação/exclusão de um registro autenticado.
- Documentação Swagger aberta em `/docs`, mostrando cada rota documentada e testável direto na interface.
- Evidência de CORS configurado (frontend consumindo a API sem erro) e rate limiting (repetir requisições até ver o bloqueio).
- Demonstração do recurso avançado escolhido: duas abas mostrando uma atualização em tempo real (WebSocket), ou um preço convertido usando uma API externa real, ou a troca de idioma ao vivo.
- Explicação das escolhas de modelagem do Prisma: por que os relacionamentos foram desenhados daquela forma.
- Discussão de limitações conhecidas e possíveis melhorias futuras.

## Projeto completo — todos os arquivos juntos

A estrutura final do backend e do frontend, com cada arquivo marcado pelo tópico que o introduziu:

```
catalogo-produtos/                      ← backend (Express + Prisma + MySQL)
├── package.json
├── .env
├── prisma7.config.ts
├── prisma/
│   ├── schema.prisma
│   ├── seed.js
│   └── migrations/
├── generated/prisma/                   ← gerado pelo Prisma, não editado à mão
└── src/
    ├── server.js
    ├── database.js
    ├── swagger.js
    ├── controllers/
    │   ├── produtoController.js
    │   └── authController.js
    ├── middlewares/
    │   └── autenticar.js
    └── services/
        └── cotacaoService.js

catalogo-frontend/                      ← frontend (React + Vite)
├── package.json
├── locales/
│   ├── pt.json
│   └── en.json
└── src/
    ├── main.jsx
    ├── App.jsx
    └── i18n.js
```

**`src/server.js`** — o ponto de entrada da API, reunindo as rotas de todos os tópicos. Vem do Tópico 1 (Express básico), com CORS do Tópico 3, autenticação do Tópico 5, Swagger do Tópico 7, rate limiting do Tópico 10 e Socket.IO do Tópico 11.

```javascript
import express from 'express'
import cors from 'cors'
import { createServer } from 'http'
import { Server } from 'socket.io'
import rateLimit from 'express-rate-limit'
import swaggerUi from 'swagger-ui-express'
import { swaggerSpec } from './swagger.js'
import { listar, criar, buscarPorId, deletar, listarCategorias } from './controllers/produtoController.js'
import { registrar, login } from './controllers/authController.js'
import { autenticar } from './middlewares/autenticar.js'

const app = express()
app.use(express.json())
app.use(cors({ origin: ['http://localhost:5173'], methods: ['GET', 'POST', 'PATCH', 'DELETE'] }))
app.use('/docs', swaggerUi.serve, swaggerUi.setup(swaggerSpec))

const limiterLogin = rateLimit({ windowMs: 15 * 60 * 1000, max: 5 })

app.get('/health', (req, res) => res.json({ status: 'ok' }))
app.get('/produtos', listar)
app.get('/produtos/:id', buscarPorId)
app.post('/produtos', autenticar, criar)
app.delete('/produtos/:id', autenticar, deletar)
app.get('/categorias', listarCategorias)
app.post('/auth/registrar', registrar)
app.post('/auth/login', limiterLogin, login)

const httpServer = createServer(app)
const io = new Server(httpServer, { cors: { origin: '*' } })
io.on('connection', (socket) => {
  socket.on('produto:observar', (id) => socket.join(`produto:${id}`))
  socket.on('produto:parar-observar', (id) => socket.leave(`produto:${id}`))
})

httpServer.listen(3000, () => console.log('Servidor rodando em http://localhost:3000'))
```

**`prisma/schema.prisma`** — Tópicos 1, 2 e 5, reunindo os três modelos finais e mostrando de relance a relação model↔tabela discutida no Tópico 1: cada `model` abaixo é uma tabela, cada campo é uma coluna, e os relacionamentos entre `Produto` e `Categoria` viraram a chave estrangeira `categoriaId`.

```prisma
generator client {
  provider = "prisma-client"
  output   = "../generated/prisma"
}

datasource db {
  provider = "mysql"
}

model Produto {
  id          Int        @id @default(autoincrement())
  nome        String
  sku         String     @unique
  preco       Decimal    @db.Decimal(10, 2)
  estoque     Int        @default(0)
  categoriaId Int?
  categoria   Categoria? @relation(fields: [categoriaId], references: [id])
  createdAt   DateTime   @default(now())
  updatedAt   DateTime   @updatedAt

  @@map("produtos")
}

model Categoria {
  id       Int       @id @default(autoincrement())
  nome     String    @unique
  produtos Produto[]

  @@map("categorias")
}

model Usuario {
  id        Int    @id @default(autoincrement())
  nome      String
  email     String @unique
  senhaHash String

  @@map("usuarios")
}
```

Os demais arquivos (`database.js`, `swagger.js`, cada controller, o middleware de autenticação, o serviço de cotação, e todo o frontend React com i18n e o hook de WebSocket) estão exatamente como apresentados em cada tópico correspondente — reveja o tópico indicado para o contexto completo de cada um.

Um usuário navegando pelo catálogo completo, e onde cada peça do MVC entra em ação: abre o frontend React (a **View**), que faz `fetch` na API e recebe do **Controller** a lista de produtos, já lida do **Model** (Prisma); vê preços no idioma/moeda ativos (i18n e consumo de API externa); faz login (JWT armazenado no cliente); com o token em mãos, cria ou edita produtos, com a UI atualizando instantaneamente; abrindo a mesma página de produto em duas abas, uma mudança de estoque em uma reflete em tempo real na outra via WebSocket — tudo isso rodando sobre uma API validada, documentada em Swagger, testável por uma coleção Postman, e protegida por CORS, rate limiting e autenticação JWT.
