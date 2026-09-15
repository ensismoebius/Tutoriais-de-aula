-- Tópico Índices e Performance.
-- Nota: isbn já tem um índice implícito por causa do UNIQUE declarado no
-- Tópico 1 — por isso o antes/depois usa autor, uma coluna sem índice.
USE locadora;

EXPLAIN SELECT * FROM livro WHERE autor = 'Machado de Assis';

CREATE INDEX idx_livro_autor ON livro(autor);

EXPLAIN SELECT * FROM livro WHERE autor = 'Machado de Assis';

INSERT INTO livro (titulo, autor, isbn, preco, categoria_id, exemplares_disponiveis)
VALUES ('Novo Livro', 'Autor X', '9788535910699', 49.90, 3, 1);

EXPLAIN SELECT * FROM emprestimo WHERE associado_id = 1 AND data_devolucao IS NULL;

CREATE INDEX idx_emprestimo_associado_devolucao ON emprestimo(associado_id, data_devolucao);

EXPLAIN SELECT * FROM emprestimo WHERE associado_id = 1 AND data_devolucao IS NULL;

EXPLAIN SELECT * FROM livro WHERE preco > 30;
