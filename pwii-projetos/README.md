# Projetos de referência — PWII

Implementação de referência do material construído ao longo de `Tutorial_PWII.md` (pasta acima). Dois projetos Laravel separados, exatamente como descritos no tutorial: `blog-app/` (Tópicos 1–5: Formulários/HTTP, Sessões, Segurança, Padrão DAO, Projeto Integrador) e `task-manager/` (Tópicos 6–15: POO, MVC, DAO+MVC, Livewire, API). Veja a seção "Projeto completo — todos os arquivos juntos" no final do tutorial para o mesmo conteúdo comentado, lado a lado com a explicação de cada passo.

Cada projeto tem seu próprio `README.md` com instruções de execução, o que foi verificado e como.

## Rodando os dois

```bash
cd blog-app
composer install && cp .env.example .env && php artisan key:generate && php artisan migrate && php artisan serve
```

```bash
# em outro terminal, com uma porta diferente para não colidir
cd task-manager
composer install && cp .env.example .env && php artisan key:generate && php artisan migrate
php artisan serve --port=8001
```

Cada projeto usa seu próprio banco MySQL (`blog_app` e `task_manager`, respectivamente) — apontados em cada `.env`.

## Como esta implementação foi verificada

Nada aqui foi apenas escrito e assumido como funcional. Toda a construção passou por `composer create-project`, `php artisan migrate` contra um MySQL/MariaDB local real, `php artisan serve` real, e testes reais via `curl`, `tinker` e (para o `task-manager`) `newman` contra uma coleção Postman — incluindo a verificação de códigos de status HTTP específicos (200, 201, 204, 302, 403, 419, 422) nas situações que o tutorial descreve. Os detalhes de cada verificação, comando por comando, estão nos READMEs de cada projeto.

Um bug real foi encontrado e corrigido durante essa verificação: a instalação padrão de `livewire/livewire` (sem fixar versão) trouxe o Livewire 4, cuja estrutura de arquivos gerados diverge do que o currículo e o tutorial documentam (que seguem o Livewire 3, clássico). O `task-manager/composer.json` fixa `"livewire/livewire": "^3.0"` por esse motivo — ver a nota correspondente em `task-manager/README.md` e no Tópico 13 do tutorial.
