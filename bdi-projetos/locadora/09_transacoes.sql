-- Tópico Transações (ACID, COMMIT, ROLLBACK).
USE locadora;

SELECT exemplares_disponiveis FROM livro WHERE id = 3;

-- Operação composta correta: baixar o exemplar e registrar o empréstimo juntos.
START TRANSACTION;
UPDATE livro SET exemplares_disponiveis = exemplares_disponiveis - 1 WHERE id = 3;
INSERT INTO emprestimo (associado_id, livro_id, data_emprestimo, data_prevista)
VALUES (2, 3, NOW(), CURDATE() + INTERVAL 14 DAY);
COMMIT;

SELECT exemplares_disponiveis FROM livro WHERE id = 3;  -- decrementado, e ficou assim

-- Simulação de falha: decrementa, decide que não pode seguir, desfaz tudo.
START TRANSACTION;
UPDATE livro SET exemplares_disponiveis = exemplares_disponiveis - 1 WHERE id = 3;
-- verificação de negócio: o valor abaixo seria checado pela aplicação antes do COMMIT
SELECT exemplares_disponiveis FROM livro WHERE id = 3;
ROLLBACK;

SELECT exemplares_disponiveis FROM livro WHERE id = 3;  -- volta ao valor antes do ROLLBACK
