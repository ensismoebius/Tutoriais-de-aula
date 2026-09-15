# Projetos de referência — BDI

Implementação de referência do material construído ao longo de `Tutorial_BDI.md` (pasta acima). MySQL 8.4 LTS / MariaDB. Dois projetos separados, exatamente como descritos no tutorial:

- [`locadora/`](locadora/) — o projeto cumulativo dos Tópicos 1 a 12 (Do DER ao Modelo Lógico até Triggers): o banco de uma locadora de livros, construído incrementalmente — schema, DML, DQL, JOINs, subconsultas, Views, índices, transações, Functions/Procedures e Triggers, tudo sobre o mesmo schema, definido desde o primeiro tópico.
- [`projeto-final/`](projeto-final/) — "Clínica Veterinária PetCare", um exemplo completo e funcional do Projeto Final descrito no tutorial, com um domínio original (não a locadora), satisfazendo os 5 requisitos mínimos: ≥ 4 tabelas, ≥ 2 relacionamentos N:N, uma View, uma Stored Procedure com transação e um Trigger de auditoria.

Cada projeto tem seu próprio `README.md` com instruções de execução e o que foi verificado, com comandos e saída reais.

## Rodando os dois

```bash
# locadora — 11 scripts numerados, executados em ordem
cd locadora
for f in 01_schema.sql 02_seed.sql 03_dml.sql 04_dql.sql 05_joins.sql \
         06_subconsultas.sql 07_views.sql 08_indices.sql 09_transacoes.sql \
         10_procedures.sql 11_triggers.sql; do
  mysql -u andre -p1234 < "$f"
done

# projeto-final — Clínica PetCare
cd ../projeto-final
mysql -u andre -p1234 < 01_schema.sql
mysql -u andre -p1234 < 02_seed.sql
mysql -u andre -p1234 < 03_view.sql
mysql -u andre -p1234 < 04_procedure.sql
mysql -u andre -p1234 clinica_petcare < 05_procedure_teste_falha.sql   # erro esperado, de propósito
mysql -u andre -p1234 < 06_trigger.sql
```

## Como esta implementação foi verificada

Nada aqui foi escrito e assumido como funcional só de olhar o SQL. Todo `CREATE DATABASE`, `CREATE TABLE`, `INSERT`, `UPDATE`, `DELETE`, `CREATE VIEW`, `CREATE FUNCTION`, `CREATE PROCEDURE`, `CREATE TRIGGER` e `CREATE INDEX` dos dois projetos foi de fato executado, em ordem, contra uma instância **MariaDB 12.3.3** local (usuário `andre`), a partir de um banco limpo — e toda consulta de exemplo (`SELECT`, `EXPLAIN`, `CALL`) foi rodada de verdade, com a saída capturada e comparada com o que o tutorial afirma, não apenas lida e aprovada visualmente.

Isso incluiu testar os caminhos de erro de propósito: inserir um `emprestimo` com `associado_id` inexistente (erro 1452, `Cannot add or update a child row`), apagar um `associado` com empréstimos (erro 1451, `Cannot delete or update a parent row`), inserir um empréstimo para um livro sem exemplares disponíveis (erro 1644 vindo do `SIGNAL SQLSTATE '45000'` do Trigger `trg_validar_exemplares`), e chamar `registrar_consulta` na Clínica PetCare com um veterinário sem a especialidade exigida (mesmo padrão de `SIGNAL`, confirmando que a transação interna é abortada sem deixar rastro).

Um problema real do material de origem foi corrigido durante essa verificação, além dos dois já previstos (a tabela de ordem de execução do SQL, corrigida no tópico de DQL Básico, e a substituição do trecho Node.js/Prisma do tópico de Transações por uma explicação em SQL puro): o Tópico de Índices originalmente testava `EXPLAIN` antes/depois de um índice em `livro.isbn` — mas `isbn` já tem um índice implícito desde o Tópico 1, porque é `UNIQUE`. Rodar esse `EXPLAIN` de verdade mostrava `type = ref` mesmo **antes** de qualquer `CREATE INDEX`, contradizendo o que o tutorial estava prestes a afirmar. A demonstração "sem índice → com índice" foi refeita usando `livro.autor` (uma coluna sem nenhum índice), e só então confirmada com a saída real: `type = ALL` antes, `type = ref` depois.

Os bancos `locadora` e `clinica_petcare` usados para essa verificação foram descartados ao final (`DROP DATABASE`) — o artefato permanente é o conjunto de scripts `.sql` nestas duas pastas, não uma instância de banco rodando.
