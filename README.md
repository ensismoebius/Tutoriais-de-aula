# Tutoriais de aula

Material didático usado nas aulas da Etec Jaraguá, organizado em apostilas por disciplina. Cada apostila (`Tutorial_<DISCIPLINA>.md`) é dividida em tópicos numerados, e cada tópico é construído em **passos** — o código vai sendo montado aos poucos, testado a cada passo, em vez de ser apresentado pronto de uma vez.

## Disciplinas

- **`Tutorial_PAMII.md`** — Programação para Aplicativos Móveis II. React Native com Expo (JavaScript), do zero até um app de corrida completo e publicável. Tem uma implementação de referência rodando de verdade em [`app-de-corrida/`](app-de-corrida/) — veja o README daquela pasta para instruções de como rodar o app.
- **`Tutorial_DS.md`** — Desenvolvimento de Sistemas. Java, com foco em POO avançada.
- **`Tutorial_BDI.md`** — Banco de Dados I. Modelagem, do DER ao modelo lógico.
- **`Tutorial_PWII.md`** — Programação para Web II. PHP com Laravel 13 (MySQL/MariaDB), do formulário HTTP básico até um sistema completo com sessões, segurança, padrão DAO, MVC, Livewire e uma API JSON. Tem uma implementação de referência rodando de verdade em [`pwii-projetos/`](pwii-projetos/) — dois projetos Laravel separados (`blog-app/` e `task-manager/`) — veja o README daquela pasta para instruções de como rodar cada um.
- **`Tutorial_PWIII.md`** — Programação para Web III. Backend em Node.js/Express com Prisma ORM (MySQL/MariaDB) e frontend em React (Vite), consumindo a API via REST/JWT/WebSockets. Tem uma implementação de referência rodando de verdade em [`catalogo/`](catalogo/) — veja o README daquela pasta para instruções de como rodar a API e o frontend.

Os arquivos `.PDF` na raiz são material de apoio complementar das aulas.

## Como usar

Basta abrir o `.md` da disciplina desejada — são arquivos Markdown comuns, legíveis tanto no GitHub quanto em qualquer editor de texto. Recomenda-se seguir os tópicos na ordem em que aparecem, já que cada um costuma depender do código construído no anterior.

Para a disciplina de PAMII, o projeto de referência em `app-de-corrida/` acompanha o tutorial passo a passo: cada arquivo do app traz um comentário no topo indicando de qual tópico ele veio. Para rodar o app, veja as instruções em [`app-de-corrida/README.md`](app-de-corrida/README.md).

Para a disciplina de PWII, o projeto de referência em `pwii-projetos/` (dividido em `blog-app/`, cobrindo Formulários/Sessões/Segurança/DAO, e `task-manager/`, cobrindo POO/MVC/Livewire/API) acompanha o tutorial tópico a tópico. Para rodar os dois, veja as instruções em [`pwii-projetos/README.md`](pwii-projetos/README.md).

Para a disciplina de PWIII, o projeto de referência em `catalogo/` (dividido em `catalogo-produtos/`, a API, e `catalogo-frontend/`, o frontend React) acompanha o tutorial tópico a tópico. Para rodar os dois, veja as instruções em [`catalogo/README.md`](catalogo/README.md).
