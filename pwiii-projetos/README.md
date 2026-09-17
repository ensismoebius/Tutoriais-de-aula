# Catálogo de Produtos

Implementação de referência do projeto construído ao longo de `Tutorial_PWIII.md` (pasta acima). Dois projetos separados, exatamente como descritos no tutorial: `catalogo-produtos/` (API Express + Prisma ORM + MySQL/MariaDB) e `catalogo-frontend/` (frontend React + Vite, consumindo a API). Veja a seção "Projeto completo — todos os arquivos juntos" no final do tutorial para o mesmo conteúdo comentado, lado a lado com a explicação de cada passo.

## Pré-requisitos

Um servidor MySQL ou MariaDB rodando localmente, com um banco chamado `catalogo` já criado:

```bash
mysql -u seu_usuario -p -e "CREATE DATABASE IF NOT EXISTS catalogo;"
```

Se o MariaDB acusar o erro `Cannot load from mysql.proc` ao rodar as migrations mais abaixo, rode `sudo mariadb-upgrade` uma vez (ver o aviso no Tópico 1 do tutorial).

## Rodando a API (`catalogo-produtos/`)

```bash
cd catalogo-produtos
npm install
cp .env.example .env   # depois edite DATABASE_URL e JWT_SECRET com suas credenciais
npx prisma migrate dev
npx prisma generate
npx prisma db seed
npm start
```

Sobe em `http://localhost:3000`. Documentação interativa (Swagger) em `http://localhost:3000/docs`.

## Rodando o frontend (`catalogo-frontend/`)

Em outro terminal, com a API já rodando:

```bash
cd catalogo-frontend
npm install
cp .env.example .env
npm run dev
```

Sobe em `http://localhost:5173`. A URL da API é lida de `VITE_API_URL` (já configurada em `.env` para `http://localhost:3000`).

## O que já está funcionando, de ponta a ponta

- CRUD completo de produtos e categorias via Prisma/MySQL, com relacionamento, `include`, paginação e `connectOrCreate` (Tópicos 1 e 2).
- Frontend React consumindo a API com `fetch`, formulário controlado e atualização otimista da lista (Tópico 3).
- Negociação de conteúdo JSON/XML em `GET /produtos/:id` via header `Accept` (Tópico 4).
- Autenticação JWT (`/auth/registrar`, `/auth/login`) protegendo as rotas de escrita (Tópico 5).
- Documentação OpenAPI/Swagger ao vivo em `/docs`, com autenticação Bearer configurada (Tópico 7).
- Conversão de preço para dólar via API externa real (`awesomeapi.com.br`), com cache e fallback (Tópico 8).
- Internacionalização pt/en com `react-i18next` e seletor de idioma (Tópico 9).
- CORS restrito por origem e rate limiting (geral + login) via `express-rate-limit` (Tópico 10).
- Atualização de estoque em tempo real via Socket.IO, com salas por produto (`PATCH /produtos/:id` emite `produto:atualizado`) (Tópico 11).

- Coleção Postman pronta em `catalogo-produtos/catalogo.postman_collection.json` (login → listar → criar → erro de validação → excluir), a mesma sequência descrita no Tópico 6. Rode com a API no ar:

```bash
cd catalogo-produtos
npx newman run catalogo.postman_collection.json
```
