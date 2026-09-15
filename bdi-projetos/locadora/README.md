# locadora — projeto cumulativo do Tutorial_BDI.md

Implementação de referência do banco `locadora` construído ao longo de todo o
`Tutorial_BDI.md`, do Tópico 1 (Do DER ao Modelo Lógico) ao Tópico 12
(Triggers). Cada script corresponde a um tópico do tutorial e foi executado de
verdade, em ordem, contra uma instância MariaDB local — não é SQL só lido e
"aprovado visualmente".

## Schema final

- **`associado`** — id, nome, email (único).
- **`categoria`** — id, nome, categoria_pai_id (auto-relacionamento, hierarquia de 1 nível).
- **`livro`** — id, titulo, autor, isbn (único), preco, categoria_id, exemplares_disponiveis.
- **`emprestimo`** — id, associado_id, livro_id, data_emprestimo, data_prevista, data_devolucao (nula = ainda em aberto).
- **`log_emprestimo`** — criada no Tópico de Triggers, para auditoria.

Esse schema é o mesmo do início ao fim: `data_prevista` (em `emprestimo`) e
`preco`/`categoria_id`/`exemplares_disponiveis` (em `livro`) já existem desde
o Tópico 1, e o nome usado para o leitor é `associado` em todo o material —
veja a nota de reconciliação de schema no início do `Tutorial_BDI.md` para o
porquê dessa decisão.

## Como rodar

Os scripts são numerados na ordem em que devem ser executados, e cada um
depende do estado deixado pelo anterior:

```bash
mysql -u andre -p1234 < 01_schema.sql        # cria o banco e as 4 tabelas
mysql -u andre -p1234 < 02_seed.sql          # popula associados, categorias, livros, empréstimos
mysql -u andre -p1234 < 03_dml.sql           # UPDATE/DELETE/transação (Tópico DML)
mysql -u andre -p1234 < 04_dql.sql           # SELECT, WHERE, JOIN de relatório, GROUP BY/HAVING
mysql -u andre -p1234 < 05_joins.sql         # INNER/LEFT/RIGHT/SELF JOIN
mysql -u andre -p1234 < 06_subconsultas.sql  # subconsultas escalares, IN, correlacionadas, no FROM
mysql -u andre -p1234 < 07_views.sql         # CREATE VIEW
mysql -u andre -p1234 < 08_indices.sql       # EXPLAIN antes/depois de índice, índice composto
mysql -u andre -p1234 < 09_transacoes.sql    # START TRANSACTION / COMMIT / ROLLBACK
mysql -u andre -p1234 < 10_procedures.sql    # FUNCTION calcular_multa, PROCEDURE registrar_devolucao
mysql -u andre -p1234 < 11_triggers.sql      # trg_log_emprestimo, trg_validar_exemplares
```

Para rodar tudo de uma vez, do zero:

```bash
for f in 0*.sql 1*.sql; do mysql -u andre -p1234 < "$f"; done
```

## O que cada script demonstra (e foi de fato observado ao rodar)

| Script | O que mostra | Confirmado ao rodar |
|---|---|---|
| `01_schema.sql` | 4 tabelas com FKs do modelo lógico | `SHOW TABLES` lista as 4 |
| `02_seed.sql` | Massa de dados inicial | Contagens batem: 5 associados, 4 categorias, 5 livros, 6 empréstimos |
| `03_dml.sql` | UPDATE idempotente, DELETE testado com SELECT, transação apagando associado+empréstimos | Segundo UPDATE de devolução afeta 0 linhas; associado 5 removido só depois do `COMMIT` |
| `04_dql.sql` | WHERE/LIKE/IN/BETWEEN/ORDER BY/LIMIT, COUNT/GROUP BY/HAVING | Relatório de atrasados e ranking de associados batem com os dados semeados |
| `05_joins.sql` | INNER, LEFT (com anti-join), RIGHT, SELF JOIN, hierarquia de categoria | Associado sem empréstimo (Fábio) some no INNER e aparece com NULL no LEFT |
| `06_subconsultas.sql` | Escalar, IN, NOT IN, correlacionada, tabela derivada | Livros acima da média da própria categoria batem com o cálculo manual |
| `07_views.sql` | `vw_emprestimos_ativos`, `vw_emprestimos_atrasados`, `vw_livros_disponiveis` | Consultadas como tabelas normais, com `WHERE`/`GROUP BY` por cima |
| `08_indices.sql` | `EXPLAIN` mostrando `type = ALL` sem índice e `type = ref` com índice | Confirmado nos dois casos (índice simples em `autor`, composto em `emprestimo`) |
| `09_transacoes.sql` | `exemplares_disponiveis` decrementado após `COMMIT`, restaurado após `ROLLBACK` | Valor volta ao original depois do `ROLLBACK`, testado na prática |
| `10_procedures.sql` | `calcular_multa()`, `registrar_devolucao()` com multa de atraso e multa zero | `CALL` com empréstimo vencido retornou multa > 0; com empréstimo em dia, `0.00` |
| `11_triggers.sql` | `trg_log_emprestimo` (auditoria) e `trg_validar_exemplares` (validação com `SIGNAL`) | Log criado automaticamente; inserção com estoque zerado rejeitada com o erro customizado (testado à parte, fora do lote, porque o próprio objetivo do teste é falhar) |

Duas rejeições de integridade referencial também foram testadas de verdade e
não fazem parte do lote (porque interromperiam a execução): inserir um
`emprestimo` com `associado_id` inexistente (erro 1452) e apagar um
`associado` que ainda tem empréstimos (erro 1451).
