-- Tópico Subconsultas.
USE locadora;

-- Subconsulta escalar: livros acima do preço médio geral.
SELECT titulo, preco
FROM livro
WHERE preco > (SELECT AVG(preco) FROM livro);

-- Subconsulta com IN: associados que já pegaram emprestado.
SELECT nome, email
FROM associado
WHERE id IN (SELECT DISTINCT associado_id FROM emprestimo);

-- Subconsulta com NOT IN: anti-join alternativo ao LEFT JOIN + IS NULL.
SELECT nome, email
FROM associado
WHERE id NOT IN (SELECT associado_id FROM emprestimo WHERE associado_id IS NOT NULL);

-- Subconsulta correlacionada: livros acima da média de preço da própria categoria.
SELECT l1.titulo, l1.categoria_id, l1.preco
FROM livro l1
WHERE l1.preco > (
    SELECT AVG(l2.preco)
    FROM livro l2
    WHERE l2.categoria_id = l1.categoria_id
);

-- Subconsulta no FROM (tabela derivada): categorias com preço médio acima de R$ 35.
SELECT categoria_id, media_categoria
FROM (
    SELECT categoria_id, AVG(preco) AS media_categoria
    FROM livro
    GROUP BY categoria_id
) AS medias_por_categoria
WHERE media_categoria > 35;
