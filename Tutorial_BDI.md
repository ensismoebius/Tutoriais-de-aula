# Tutorial — Banco de Dados I (BDI)

### 1. Do DER para o Modelo Lógico

O Diagrama Entidade-Relacionamento (DER) é a representação conceitual do banco de dados: entidades, atributos e relacionamentos, sem se preocupar com a implementação. O Modelo Lógico é a tradução desse DER para tabelas, colunas, chaves primárias (PK) e chaves estrangeiras (FK), já pensando no SGBD relacional.

**Regras de conversão principais:**
- Toda entidade forte vira uma tabela.
- Atributos simples viram colunas; atributos multivalorados viram tabelas à parte, relacionadas por FK.
- Relacionamento 1:N — a FK vai para o lado "N".
- Relacionamento N:N — cria-se uma tabela associativa (tabela de junção) contendo as FKs das duas entidades envolvidas.
- Relacionamento 1:1 — a FK pode ficar em qualquer um dos dois lados (normalmente no lado que representa a entidade "dependente").

**Exemplo — DER de "Aluno" e "Curso" (N:N):**
```sql
CREATE TABLE aluno (
    id_aluno INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    data_nascimento DATE
);

CREATE TABLE curso (
    id_curso INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL
);

CREATE TABLE matricula (
    id_aluno INT,
    id_curso INT,
    data_matricula DATE NOT NULL,
    PRIMARY KEY (id_aluno, id_curso),
    FOREIGN KEY (id_aluno) REFERENCES aluno(id_aluno),
    FOREIGN KEY (id_curso) REFERENCES curso(id_curso)
);
```

**Exercício:** Dado um DER com as entidades `Cliente`, `Pedido` e `Produto` (um cliente faz vários pedidos; um pedido contém vários produtos, e um produto pode estar em vários pedidos), desenhe o modelo lógico e escreva o `CREATE TABLE` de cada tabela, incluindo a tabela associativa entre `Pedido` e `Produto`.

### 2. DML (UPDATE, DELETE)

DML (Data Manipulation Language) é o subconjunto do SQL usado para manipular os dados dentro das tabelas: `INSERT`, `UPDATE`, `DELETE` (e `SELECT`, tratado no próximo tópico).

**UPDATE** — altera valores de linhas já existentes:
```sql
UPDATE aluno
SET nome = 'Maria Silva'
WHERE id_aluno = 5;
```

**DELETE** — remove linhas:
```sql
DELETE FROM matricula
WHERE id_aluno = 5 AND id_curso = 2;
```

⚠️ **Cuidado:** `UPDATE` e `DELETE` sem `WHERE` afetam **todas** as linhas da tabela. Sempre confira a cláusula `WHERE` antes de executar, e prefira testar primeiro com um `SELECT` usando o mesmo filtro.

**Exercício:** Escreva um `UPDATE` que reajuste em 10% o preço de todos os produtos de uma categoria específica, e um `DELETE` que remova pedidos com mais de 2 anos e status "cancelado".

### 3. DQL Básico (SELECT, WHERE)

DQL (Data Query Language) é usada para consultar dados. O comando central é o `SELECT`.

```sql
SELECT nome, data_nascimento
FROM aluno
WHERE data_nascimento >= '2005-01-01'
ORDER BY nome ASC;
```

**Elementos principais:**
- `SELECT coluna1, coluna2` — escolhe as colunas (ou `*` para todas).
- `FROM tabela` — de onde vêm os dados.
- `WHERE condição` — filtra linhas (operadores: `=`, `<>`, `>`, `<`, `BETWEEN`, `IN`, `LIKE`, `IS NULL`).
- `ORDER BY coluna [ASC|DESC]` — ordena o resultado.
- `LIMIT n` — restringe a quantidade de linhas retornadas.

**Exercício:** Liste todos os alunos cujo nome comece com "A", nascidos entre 2004 e 2006, ordenados por data de nascimento decrescente.

### 4. DQL com Funções Agregadas (COUNT, SUM, GROUP BY)

Funções agregadas resumem várias linhas em um único valor: `COUNT()`, `SUM()`, `AVG()`, `MIN()`, `MAX()`. Quando combinadas com `GROUP BY`, calculam o resultado por grupo.

```sql
SELECT id_curso, COUNT(*) AS total_alunos
FROM matricula
GROUP BY id_curso;

SELECT id_curso, AVG(nota) AS media
FROM avaliacao
GROUP BY id_curso
HAVING AVG(nota) >= 7;
```

- `WHERE` filtra linhas **antes** de agrupar.
- `HAVING` filtra grupos **depois** do `GROUP BY`.

**Exercício:** Escreva uma consulta que mostre, por curso, o total de alunos matriculados e a média de idade, mostrando apenas os cursos com mais de 10 alunos.

### 5. Consultas com Múltiplas Tabelas (JOINs)

`JOIN` combina linhas de duas ou mais tabelas com base em uma condição de relacionamento (normalmente PK = FK).

```sql
SELECT a.nome, c.nome AS curso
FROM aluno a
INNER JOIN matricula m ON a.id_aluno = m.id_aluno
INNER JOIN curso c ON m.id_curso = c.id_curso;
```

`INNER JOIN` retorna apenas as linhas que possuem correspondência nas duas tabelas.

**Exercício:** Liste o nome de cada aluno junto com o nome de todos os cursos em que está matriculado.

### 6. JOINs Avançados (LEFT/RIGHT, SELF)

- `LEFT JOIN` — retorna todas as linhas da tabela da esquerda, mesmo sem correspondência na direita (colunas nulas quando não houver).
- `RIGHT JOIN` — o inverso do `LEFT JOIN`.
- `SELF JOIN` — junção de uma tabela com ela mesma, útil para relações hierárquicas (ex.: funcionário/gerente).

```sql
-- alunos sem nenhuma matrícula
SELECT a.nome
FROM aluno a
LEFT JOIN matricula m ON a.id_aluno = m.id_aluno
WHERE m.id_aluno IS NULL;

-- self join: funcionário e seu gerente
SELECT f.nome AS funcionario, g.nome AS gerente
FROM funcionario f
LEFT JOIN funcionario g ON f.id_gerente = g.id_funcionario;
```

**Exercício:** Usando `LEFT JOIN`, liste todos os cursos e a quantidade de alunos matriculados em cada um, incluindo cursos com zero alunos.

### 7. Subconsultas

Uma subconsulta (subquery) é um `SELECT` dentro de outro comando SQL. Pode aparecer em `WHERE`, `FROM` ou `SELECT`.

```sql
-- alunos com nota acima da média geral
SELECT nome
FROM aluno
WHERE id_aluno IN (
    SELECT id_aluno FROM avaliacao
    WHERE nota > (SELECT AVG(nota) FROM avaliacao)
);
```

Subconsultas correlacionadas referenciam colunas da consulta externa e são reexecutadas para cada linha — são poderosas, mas podem custar mais desempenho que um `JOIN` equivalente.

**Exercício:** Escreva uma subconsulta que retorne os cursos que têm mais alunos matriculados que a média de alunos por curso.

### 8. Views

Uma `VIEW` é uma consulta salva que se comporta como uma tabela virtual — não armazena dados, apenas a definição do `SELECT`.

```sql
CREATE VIEW vw_alunos_curso AS
SELECT a.nome AS aluno, c.nome AS curso
FROM aluno a
JOIN matricula m ON a.id_aluno = m.id_aluno
JOIN curso c ON m.id_curso = c.id_curso;

SELECT * FROM vw_alunos_curso WHERE curso = 'Desenvolvimento de Sistemas';
```

**Vantagens:** simplifica consultas repetidas, pode restringir acesso a colunas sensíveis, isola o SQL complexo da aplicação.

**Exercício:** Crie uma view que mostre, para cada curso, a quantidade de alunos e a média de idade.

### 9. Índices e Performance

Um índice é uma estrutura auxiliar (geralmente uma árvore B) que acelera buscas em uma coluna, evitando varredura completa da tabela (*full table scan*).

```sql
CREATE INDEX idx_aluno_nome ON aluno(nome);
```

- Use índices em colunas muito filtradas (`WHERE`) ou usadas em `JOIN`.
- Índices aceleram leitura, mas custam em escrita (`INSERT`/`UPDATE`/`DELETE` precisam atualizar o índice também).
- `EXPLAIN` (ou `EXPLAIN ANALYZE`) mostra o plano de execução de uma consulta e se um índice está sendo usado.

**Exercício:** Dada uma tabela `pedido` com 1 milhão de linhas, filtrada frequentemente por `data_pedido`, crie um índice adequado e use `EXPLAIN` para comparar o plano antes e depois.

### 10. Transações (ACID, COMMIT, ROLLBACK)

Uma transação agrupa um ou mais comandos SQL que devem ser executados como uma única unidade atômica.

**Propriedades ACID:**
- **Atomicidade** — tudo ou nada.
- **Consistência** — o banco sai de um estado válido para outro.
- **Isolamento** — transações concorrentes não interferem entre si.
- **Durabilidade** — após o `COMMIT`, os dados persistem mesmo em caso de falha.

```sql
START TRANSACTION;

UPDATE conta SET saldo = saldo - 100 WHERE id_conta = 1;
UPDATE conta SET saldo = saldo + 100 WHERE id_conta = 2;

COMMIT;
-- ou, em caso de erro:
ROLLBACK;
```

**Exercício:** Escreva uma transação de transferência bancária que só confirme a operação se ambas as contas existirem e a conta de origem tiver saldo suficiente; caso contrário, faça `ROLLBACK`.

### 11. Stored Procedures e Funções

- **Stored Procedure**: bloco de código SQL armazenado no banco, chamado com `CALL`, pode não retornar valor (ou retornar via parâmetros `OUT`).
- **Function**: sempre retorna um valor e pode ser usada dentro de um `SELECT`.

```sql
DELIMITER //
CREATE PROCEDURE matricular_aluno(IN p_aluno INT, IN p_curso INT)
BEGIN
    INSERT INTO matricula (id_aluno, id_curso, data_matricula)
    VALUES (p_aluno, p_curso, CURDATE());
END //
DELIMITER ;

CALL matricular_aluno(5, 2);

CREATE FUNCTION idade(p_nascimento DATE) RETURNS INT
DETERMINISTIC
RETURN TIMESTAMPDIFF(YEAR, p_nascimento, CURDATE());

SELECT nome, idade(data_nascimento) FROM aluno;
```

**Exercício:** Crie uma procedure `cancelar_matricula(id_aluno, id_curso)` que remova a matrícula e registre a data de cancelamento em uma tabela de log.

### 12. Triggers

Um `TRIGGER` é um bloco de código executado automaticamente antes ou depois de um `INSERT`, `UPDATE` ou `DELETE` em uma tabela.

```sql
DELIMITER //
CREATE TRIGGER trg_log_delete_aluno
AFTER DELETE ON aluno
FOR EACH ROW
BEGIN
    INSERT INTO log_exclusao (tabela, id_removido, data_remocao)
    VALUES ('aluno', OLD.id_aluno, NOW());
END //
DELIMITER ;
```

- `OLD` — valores da linha antes da alteração (disponível em `UPDATE`/`DELETE`).
- `NEW` — valores da linha depois da alteração (disponível em `INSERT`/`UPDATE`).
- Use com moderação: triggers escondem lógica de negócio do código da aplicação e podem dificultar a depuração.

**Exercício:** Crie um trigger que impeça, via `SIGNAL`, a matrícula de um aluno em mais de 6 cursos simultaneamente.

### 13. Projeto Final — Início

Momento de aplicar todo o conteúdo do curso em um projeto de banco de dados completo:
1. Modelar o DER do domínio escolhido.
2. Derivar o modelo lógico e criar as tabelas (com PKs, FKs e constraints).
3. Popular as tabelas com dados de teste.
4. Implementar consultas (`JOIN`, subconsultas, agregações) que respondam a perguntas reais do domínio.
5. Criar pelo menos uma view, uma procedure/function e um trigger que façam sentido para o projeto.
6. Cuidar de performance (índices) nas consultas mais usadas.

### 14. Projeto Final — Apresentações

Checklist para a apresentação:
- Diagrama DER e modelo lógico documentados.
- Script SQL completo de criação do banco (`CREATE TABLE`, `CONSTRAINT`s).
- Pelo menos 3 consultas relevantes demonstradas ao vivo.
- Justificativa das escolhas de índice, view, procedure e trigger.
- Discussão de limitações e possíveis melhorias futuras.
