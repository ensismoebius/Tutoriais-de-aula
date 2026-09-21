# Tutoriais de aula

Material didático usado nas aulas da Etec Jaraguá, organizado em apostilas por disciplina. Cada apostila (`Tutorial_<DISCIPLINA>.md`) é dividida em tópicos numerados, e cada tópico é construído em **passos** — o código vai sendo montado aos poucos, testado a cada passo, em vez de ser apresentado pronto de uma vez.

## Disciplinas

- **`Tutorial_PAMII.md`** — Programação para Aplicativos Móveis II. React Native com Expo (JavaScript), do zero até um app de corrida completo e publicável. Tem uma implementação de referência rodando de verdade em [`pamii-projetos/`](pamii-projetos/) — veja o README daquela pasta para instruções de como rodar o app.
- **`Tutorial_DS.md`** — Desenvolvimento de Sistemas. Java 21 LTS com JavaFX 21 (desktop), do jogo JavaFX com POO avançada até persistência com DAO/SQLite, coleções, generics/exceções, arquivos/streams, MVC, padrões de projeto, debug/logging e testes (JUnit + TestFX) — seguido de um Projeto Final e um mini-projeto separado de Robocode (robôs de combate em Java, da API básica a `AdvancedRobot`). Tem uma implementação de referência rodando de verdade em [`ds-projetos/`](ds-projetos/) — três projetos (`javafx-game/`, `projeto-final/` e `robocode-robo/`) — veja o README daquela pasta para instruções de como rodar cada um.
- **`Tutorial_BDI.md`** — Banco de Dados I. MySQL 8.4 LTS, do DER ao modelo lógico até Views, Índices, Transações, Stored Procedures/Functions e Triggers, construído sobre um único banco cumulativo (`locadora`, uma locadora de livros) — seguido de um Projeto Final com domínio livre. Tem uma implementação de referência rodando de verdade em [`bdi-projetos/`](bdi-projetos/) — dois projetos (`locadora/`, o script cumulativo do tutorial, e `projeto-final/`, uma clínica veterinária cobrindo os 5 requisitos do projeto final) — veja o README daquela pasta para instruções de como rodar cada um.
- **`Tutorial_PWII.md`** — Programação para Web II. PHP com Laravel 13 (MySQL/MariaDB), do formulário HTTP básico até um sistema completo com sessões, segurança, padrão DAO, MVC, Livewire e uma API JSON. Tem uma implementação de referência rodando de verdade em [`pwii-projetos/`](pwii-projetos/) — dois projetos Laravel separados (`blog-app/` e `task-manager/`) — veja o README daquela pasta para instruções de como rodar cada um.
- **`Tutorial_PWIII.md`** — Programação para Web III. Backend em Node.js/Express com Prisma ORM (MySQL/MariaDB) e frontend em React (Vite), consumindo a API via REST/JWT/WebSockets, com login social via Google (OAuth 2.0 / OpenID Connect). Tem uma implementação de referência rodando de verdade em [`pwiii-projetos/`](pwiii-projetos/) — veja o README daquela pasta para instruções de como rodar a API e o frontend.

Os arquivos `.PDF` na raiz são material de apoio complementar das aulas.

## Como usar

Basta abrir o `.md` da disciplina desejada — são arquivos Markdown comuns, legíveis tanto no GitHub quanto em qualquer editor de texto. Recomenda-se seguir os tópicos na ordem em que aparecem, já que cada um costuma depender do código construído no anterior.

Para a disciplina de PAMII, o projeto de referência em `pamii-projetos/` acompanha o tutorial passo a passo: cada arquivo do app traz um comentário no topo indicando de qual tópico ele veio. Para rodar o app, veja as instruções em [`pamii-projetos/README.md`](pamii-projetos/README.md).

Para a disciplina de PWII, o projeto de referência em `pwii-projetos/` (dividido em `blog-app/`, cobrindo Formulários/Sessões/Segurança/DAO, e `task-manager/`, cobrindo POO/MVC/Livewire/API) acompanha o tutorial tópico a tópico. Para rodar os dois, veja as instruções em [`pwii-projetos/README.md`](pwii-projetos/README.md).

Para a disciplina de PWIII, o projeto de referência em `pwiii-projetos/` (dividido em `catalogo-produtos/`, a API, e `catalogo-frontend/`, o frontend React) acompanha o tutorial tópico a tópico. Para rodar os dois, veja as instruções em [`pwiii-projetos/README.md`](pwiii-projetos/README.md).

Para a disciplina de DS, o projeto de referência em `ds-projetos/` (dividido em `javafx-game/`, cobrindo POO/DAO/Coleções/MVC/Padrões/Testes, `projeto-final/`, um exemplo completo do projeto final, e `robocode-robo/`, o robô de combate com batalhas reais registradas) acompanha o tutorial tópico a tópico. Para rodar os três, veja as instruções em [`ds-projetos/README.md`](ds-projetos/README.md).

Para a disciplina de BDI, o projeto de referência em `bdi-projetos/` (dividido em `locadora/`, os scripts SQL numerados do projeto cumulativo do tutorial, e `projeto-final/`, a Clínica Veterinária PetCare, um exemplo completo do projeto final) acompanha o tutorial tópico a tópico. Para rodar os dois, veja as instruções em [`bdi-projetos/README.md`](bdi-projetos/README.md).
