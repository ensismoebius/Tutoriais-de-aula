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
cp .env.example .env   # depois edite DATABASE_URL, JWT_SECRET e GOOGLE_CLIENT_ID com suas credenciais
npx prisma migrate dev
npx prisma generate
npx prisma db seed
npm start
```

Sobe em `http://localhost:3000`. Documentação interativa (Swagger) em `http://localhost:3000/docs`.

`GOOGLE_CLIENT_ID` só é necessário para testar o login com Google (Tópico 12) de ponta a ponta — sem ele, o resto da API funciona normalmente, e `POST /auth/google` responde 401 para qualquer token que tentar verificar. Veja o Tópico 12 do tutorial para como criar um Client ID gratuito no Google Cloud.

## Rodando o frontend (`catalogo-frontend/`)

Em outro terminal, com a API já rodando:

```bash
cd catalogo-frontend
npm install
cp .env.example .env   # depois edite VITE_GOOGLE_CLIENT_ID se for testar o login com Google
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
- Login com Google (`POST /auth/google`) via Google Identity Services, emitindo o mesmo JWT do login por senha; requer um `GOOGLE_CLIENT_ID`/`VITE_GOOGLE_CLIENT_ID` reais para o botão funcionar de ponta a ponta no navegador — sem isso, a rejeição de token inválido e o resto da API continuam funcionando normalmente (Tópico 12).

- Coleção Postman pronta em `catalogo-produtos/catalogo.postman_collection.json` (login → listar → criar → erro de validação → excluir), a mesma sequência descrita no Tópico 6. Rode com a API no ar:

```bash
cd catalogo-produtos
npx newman run catalogo.postman_collection.json
```

- Teste automatizado do login com Google em `catalogo-produtos/src/controllers/authController.google.test.js` — mocka só a verificação de assinatura do Google (`google-auth-library`), exercitando de verdade o upsert no banco, a emissão do JWT e a aceitação desse token pelo middleware `autenticar`. Rode com a API configurada (mesmo `.env`, sem precisar do servidor no ar):

```bash
cd catalogo-produtos
npm test
```
