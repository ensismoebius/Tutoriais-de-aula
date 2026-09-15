-- Tópicos Consultas com Múltiplas Tabelas (JOINs) e JOINs Avançados.
-- Insere um associado sem empréstimo (necessário para os exemplos de LEFT JOIN)
-- antes de rodar as consultas.
USE locadora;

INSERT INTO associado (nome, email) VALUES ('Fábio Teixeira', 'fabio@email.com');

-- INNER JOIN: só quem tem pelo menos um empréstimo.
SELECT a.nome, e.id, e.data_devolucao
FROM associado a
INNER JOIN emprestimo e ON a.id = e.associado_id;

-- LEFT JOIN: todos os associados, com NULL nas colunas de empréstimo quando não há.
SELECT a.nome, e.id, e.data_devolucao
FROM associado a
LEFT JOIN emprestimo e ON a.id = e.associado_id;

-- Anti-join: quem nunca pegou nenhum livro emprestado.
SELECT a.nome, a.email
FROM associado a
LEFT JOIN emprestimo e ON a.id = e.associado_id
WHERE e.id IS NULL;

-- RIGHT JOIN: espelho do LEFT JOIN acima, com a ordem das tabelas invertida.
SELECT a.nome, e.id
FROM emprestimo e
RIGHT JOIN associado a ON e.associado_id = a.id;

-- JOIN de três tabelas: relatório de empréstimos em andamento.
SELECT a.nome AS associado, l.titulo AS livro, e.data_emprestimo, e.data_devolucao
FROM emprestimo e
JOIN associado a ON e.associado_id = a.id
JOIN livro l ON e.livro_id = l.id
WHERE e.data_devolucao IS NULL;

-- SELF JOIN: livros do mesmo autor.
SELECT l1.titulo AS livro1, l2.titulo AS livro2, l1.autor
FROM livro l1
JOIN livro l2 ON l1.autor = l2.autor AND l1.id < l2.id;

-- LEFT JOIN com condição extra dentro do ON (mantém todos os associados,
-- mas só "conecta" empréstimos ainda em aberto).
SELECT a.nome, e.id, e.data_devolucao
FROM associado a
LEFT JOIN emprestimo e ON a.id = e.associado_id AND e.data_devolucao IS NULL;

-- LEFT JOIN + GROUP BY contando também os associados com zero empréstimos.
SELECT a.nome, COUNT(e.id) AS total_emprestimos
FROM associado a
LEFT JOIN emprestimo e ON a.id = e.associado_id
GROUP BY a.id, a.nome
ORDER BY total_emprestimos ASC;

-- SELF JOIN hierárquico: categorias raiz com suas subcategorias.
SELECT c.nome AS categoria, sub.nome AS subcategoria
FROM categoria c
LEFT JOIN categoria sub ON sub.categoria_pai_id = c.id
WHERE c.categoria_pai_id IS NULL;

-- RIGHT JOIN mais natural que reescrever: livros nunca emprestados.
SELECT l.titulo, e.id AS emprestimo_id
FROM emprestimo e
RIGHT JOIN livro l ON e.livro_id = l.id
WHERE e.id IS NULL;

-- JOIN triplo com SELF JOIN: associados que pegaram o mesmo livro.
SELECT DISTINCT a1.nome AS associado1, a2.nome AS associado2, l.titulo
FROM emprestimo e1
JOIN emprestimo e2 ON e1.livro_id = e2.livro_id AND e1.associado_id < e2.associado_id
JOIN associado a1 ON a1.id = e1.associado_id
JOIN associado a2 ON a2.id = e2.associado_id
JOIN livro l ON l.id = e1.livro_id;
