-- Tópicos DQL Básico e DQL com Funções Agregadas — consultas de leitura,
-- não alteram dados. Todas rodam sobre o estado deixado por 03_dml.sql.
USE locadora;

-- DQL básico: SELECT, WHERE, LIKE, AND/OR, IN, BETWEEN, ORDER BY, LIMIT.
SELECT titulo, autor FROM livro;
SELECT * FROM livro WHERE autor = 'Machado de Assis';
SELECT * FROM associado WHERE email LIKE '%@gmail.com';
SELECT * FROM emprestimo WHERE data_devolucao IS NULL AND associado_id = 1;
SELECT * FROM livro WHERE autor IN ('Machado de Assis', 'José de Alencar');
SELECT * FROM emprestimo
WHERE data_emprestimo >= '2026-07-01' AND data_emprestimo < '2026-08-01';
SELECT titulo, autor FROM livro ORDER BY autor ASC, titulo ASC;
SELECT * FROM emprestimo ORDER BY data_emprestimo DESC LIMIT 3;

-- Relatório: os empréstimos em aberto há mais tempo (JOIN + WHERE + ORDER BY + LIMIT).
SELECT a.nome, l.titulo, e.data_emprestimo
FROM emprestimo e
JOIN associado a ON e.associado_id = a.id
JOIN livro l ON e.livro_id = l.id
WHERE e.data_devolucao IS NULL
ORDER BY e.data_emprestimo ASC
LIMIT 10;

-- Funções agregadas: COUNT, GROUP BY, HAVING, MIN/MAX/AVG.
SELECT COUNT(*) FROM emprestimo;
SELECT COUNT(*) FROM emprestimo WHERE data_devolucao IS NULL;

SELECT a.nome, COUNT(*) AS total_emprestimos
FROM emprestimo e
JOIN associado a ON e.associado_id = a.id
GROUP BY a.id, a.nome
ORDER BY total_emprestimos DESC;

SELECT l.titulo, COUNT(*) AS vezes_emprestado
FROM emprestimo e
JOIN livro l ON e.livro_id = l.id
GROUP BY l.id, l.titulo
ORDER BY vezes_emprestado DESC
LIMIT 1;

SELECT associado_id, COUNT(*) AS total
FROM emprestimo
GROUP BY associado_id
HAVING total > 1;

SELECT
  MIN(data_emprestimo) AS emprestimo_mais_antigo,
  MAX(data_emprestimo) AS emprestimo_mais_recente,
  COUNT(*) AS total
FROM emprestimo;

SELECT a.nome, COUNT(*) AS total_emprestimos, MAX(e.data_emprestimo) AS ultimo_emprestimo
FROM emprestimo e
JOIN associado a ON e.associado_id = a.id
GROUP BY a.id, a.nome
HAVING total_emprestimos >= 1
ORDER BY total_emprestimos DESC
LIMIT 5;
