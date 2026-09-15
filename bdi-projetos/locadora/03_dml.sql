-- Tópico DML (UPDATE, DELETE) — demonstrações executáveis sobre o banco já semeado.
USE locadora;

-- Regra de ouro: sempre um SELECT antes de qualquer UPDATE/DELETE.
SELECT * FROM associado WHERE email = 'ana@email.com';

UPDATE associado
SET nome = 'Ana Beatriz Souza'
WHERE email = 'ana@email.com';

SELECT * FROM associado WHERE email = 'ana@email.com';

-- UPDATE idempotente registrando uma devolução: só atualiza empréstimos
-- ainda em aberto. Rodar duas vezes seguidas afeta 0 linhas na segunda vez.
SELECT * FROM emprestimo WHERE id = 2 AND data_devolucao IS NULL;

UPDATE emprestimo
SET data_devolucao = NOW()
WHERE id = 2 AND data_devolucao IS NULL;

-- Rodar de novo: 0 linhas afetadas, pois data_devolucao já não é mais NULL.
UPDATE emprestimo
SET data_devolucao = NOW()
WHERE id = 2 AND data_devolucao IS NULL;

-- DELETE testado com SELECT antes: nenhum empréstimo devolvido antes de 2020,
-- então 0 linhas afetadas — comportamento esperado.
SELECT * FROM emprestimo WHERE data_devolucao IS NOT NULL AND data_devolucao < '2020-01-01';
DELETE FROM emprestimo WHERE data_devolucao IS NOT NULL AND data_devolucao < '2020-01-01';

-- Integridade referencial: apagar um associado com empréstimos falha.
-- (Descomente para observar o erro 1451 "Cannot delete or update a parent row")
-- DELETE FROM associado WHERE id = 1;

-- Para apagar de fato um associado, os filhos precisam sair primeiro,
-- dentro de uma transação (unidade atômica: os dois DELETEs ou nenhum).
START TRANSACTION;
DELETE FROM emprestimo WHERE associado_id = 5;
DELETE FROM associado WHERE id = 5;
COMMIT;

SELECT * FROM associado WHERE id = 5;  -- vazio: associado 5 (Elisa) foi removido
