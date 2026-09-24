# task-manager (Gerenciador de Tarefas)

Implementação de referência dos Tópicos 6 a 15 de `Tutorial_PWII.md` (Introdução a POO até Projeto Final), cobrindo MVC completo, DAO, Livewire e uma API JSON. Laravel 13.26, PHP 8.4, MySQL/MariaDB, Livewire 3.8.

## Pré-requisitos

Um servidor MySQL ou MariaDB local, com um banco chamado `task_manager` já criado:

```bash
mysql -u seu_usuario -p -e "CREATE DATABASE IF NOT EXISTS task_manager CHARACTER SET utf8mb4;"
```

## Rodando o projeto

```bash
composer install
cp .env.example .env       # depois edite DB_USERNAME/DB_PASSWORD com suas credenciais
php artisan key:generate
php artisan migrate
php artisan serve
```

Sobe em `http://localhost:8000`. `/tarefas` é a interface web; `/api/tarefas` é a API JSON.

## Nota de versão do Livewire (bug real encontrado e corrigido durante a verificação)

`composer require livewire/livewire` sem fixar versão instala, no momento em que este projeto foi construído, o **Livewire 4**, que muda a estrutura de arquivos gerada por `php artisan make:livewire` (componentes de arquivo único com prefixo `⚡`, sem a dupla classe+view) e não tem mais o comando `livewire:list`. Como o currículo e o tutorial foram escritos para a estrutura clássica do **Livewire 3**, este projeto fixa a versão:

```json
"livewire/livewire": "^3.0"
```

Isso foi descoberto executando `composer require livewire/livewire` de verdade neste projeto, observando a saída do gerador (`⚡tarefa-lista.blade.php` em vez de `app/Livewire/TarefaLista.php`), e corrigido fixando a versão antes de prosseguir — o texto do tutorial (Tópico 13) documenta esse mesmo achado.

## O que já está funcionando, de ponta a ponta — e como foi verificado

Todo item abaixo foi confirmado rodando o servidor de verdade e testando com `curl`/tinker/newman contra um MySQL local real.

- **POO**: `Tarefa::calcularPrioridade()` usa `match($this->tipo)` para simular polimorfismo dentro de um único Model Eloquent — testado no tinker com uma tarefa `urgente` (retornou 10) e uma `rotina` (retornou 1) — Tópicos 6/7.
- **MVC completo**: `TarefaController` com os 7 métodos RESTful (`index/create/store/show/edit/update/destroy`), confirmados via `php artisan route:list --name=tarefas` (7 rotas web + 5 rotas de API) — Tópicos 8/9.
- **Invokable Controller**: `MarcarTarefaConcluidaController` (`--invokable`), registrado em `PATCH tarefas/{tarefa}/concluir`. Testado com uma requisição `PATCH` real contra uma tarefa recém-criada no tinker — `concluida` foi de `false` para `true`, confirmado consultando o Model direto no banco depois — Tópico 9.
- **Singleton Controller**: `ConfiguracaoController` (`--singleton`), registrado em `Route::singleton('configuracoes', ...)` — confirmado via `php artisan route:list --name=configuracoes` mostrando exatamente 3 rotas (`show`/`edit`/`update`), nenhuma com `{configuracoes}` na URL. `PUT /configuracoes` real (com token CSRF extraído da própria página) mudou `tarefas_por_pagina` de 10 para 3, e uma nova consulta a `/configuracoes` confirmou o valor persistido antes de ser restaurado ao padrão — Tópico 9.
- **Views/Blade**: layout `layouts.app` com `@yield`, componente `<x-tarefa-card />`, View Composer injetando `$totalUrgentes` e directive customizada `@urgente`/`@endurgente` — todos confirmados renderizados na página `/tarefas` real (a contagem de urgentes e a tag "URGENTE" apareceram no HTML devolvido pelo servidor) — Tópicos 10/11.
- **Helpers**: `formatar_prazo()` e `tempo_restante()` em `app/helpers.php`, registradas via `composer.json` (`"files"`) — testadas no tinker sem `use` nenhum — Tópico 11.
- **Padrão DAO + MVC**: `TarefaController` e a API injetam `TarefaDAOInterface`, nunca `Tarefa::` diretamente (exceto dentro do próprio `TarefaDAO`). A troca por uma implementação em memória foi executada no tinker sem alterar o Controller — Tópico 12.
- **Livewire**: os componentes `TarefaLista` (marcar como concluída, `wire:click`) e `TarefaForm` (criar tarefa, `wire:model` + `#[Validate]`) foram exercitados via requisição HTTP real contra o endpoint `/livewire/update` do Laravel (o mesmo que o JavaScript do Livewire chama por trás dos panos) — a ação `concluir(1)` devolveu o HTML já atualizado com a classe `concluida`, e uma consulta direta ao MySQL confirmou `concluida = 1` na linha — Tópico 13.
- **API REST**: `POST /api/tarefas` sem título devolveu `422` com JSON de erros estruturado; com dados válidos, `201` com o recurso formatado por `TarefaResource` (incluindo o campo calculado `urgente`); `PUT` devolveu `200`; `DELETE` devolveu `204` — Tópico 14.
- **Factories**: `TarefaFactory` (states `urgente()`/`concluida()`) gera dados sempre válidos para `tipo` (só `urgente`/`rotina`, o domínio real de `calcularPrioridade()`) — confirmado no tinker com `Tarefa::factory(20)->create()` (contagem foi de 5 para 25) e com `Tarefa::factory()->urgente()->create()->isUrgente()` retornando `true`. `php artisan migrate:fresh --seed` populou o banco real com 20 tarefas (15 genéricas + 3 urgentes + 2 concluídas via `DatabaseSeeder`). `TarefaFactoryTest` (3 testes, 6 asserções) passa usando só a factory, sem nenhum campo obrigatório digitado manualmente — Model `Tarefa` seguido do trait `HasFactory` (o Model não tinha esse trait antes; `Tarefa::factory()` lançava `BadMethodCallException` até ele ser adicionado).

## Coleção Postman

`task-manager.postman_collection.json` cobre a sequência sugerida no Tópico 15 (listar → criar inválido/422 → criar válido/201 → atualizar/200 → excluir/204). Rodada de verdade com `newman` durante a verificação deste projeto:

```bash
npx newman run task-manager.postman_collection.json --env-var baseUrl=http://127.0.0.1:8000
```

Resultado real da execução: 5 requisições, 7 asserções, 0 falhas.

## Estrutura, por tópico

Veja a seção "Projeto completo" em `Tutorial_PWII.md` para a árvore de arquivos completa, comentada arquivo a arquivo.

## Decisão de escopo: autenticação

O checklist de consolidação do Tópico 15 lista "autenticação protegendo rotas de escrita" como item esperado no projeto final de cada aluno. Este projeto de referência **não duplica** o sistema de login — ele já está integralmente coberto, construído e verificado no `blog-app` (Tópico 2). Reproduzi-lo aqui seria repetir código já demonstrado, sem ensinar nada novo; o foco do `task-manager` é POO/MVC/DAO/Livewire/API, que é o que os Tópicos 6–15 efetivamente introduzem.
