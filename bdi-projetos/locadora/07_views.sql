-- Tópico Views.
USE locadora;

CREATE OR REPLACE VIEW vw_emprestimos_ativos AS
SELECT a.nome AS associado, l.titulo AS livro, e.data_emprestimo, e.data_prevista
FROM emprestimo e
JOIN associado a ON e.associado_id = a.id
JOIN livro l ON e.livro_id = l.id
WHERE e.data_devolucao IS NULL;

SELECT * FROM vw_emprestimos_ativos WHERE associado = 'Ana Beatriz Souza';

SELECT associado, COUNT(*) AS total
FROM vw_emprestimos_ativos
GROUP BY associado
ORDER BY total DESC;

CREATE OR REPLACE VIEW vw_emprestimos_atrasados AS
SELECT a.nome AS associado, l.titulo AS livro, e.data_prevista,
       DATEDIFF(CURDATE(), e.data_prevista) AS dias_atraso
FROM emprestimo e
JOIN associado a ON e.associado_id = a.id
JOIN livro l ON e.livro_id = l.id
WHERE e.data_devolucao IS NULL AND e.data_prevista < CURDATE();

SELECT * FROM vw_emprestimos_atrasados;

-- Exercício 1 resolvido: livros sem nenhum empréstimo ativo no momento.
CREATE OR REPLACE VIEW vw_livros_disponiveis AS
SELECT l.id, l.titulo, l.exemplares_disponiveis
FROM livro l
WHERE l.id NOT IN (
    SELECT livro_id FROM emprestimo WHERE data_devolucao IS NULL
) OR l.exemplares_disponiveis > 0;

SELECT * FROM vw_livros_disponiveis;
