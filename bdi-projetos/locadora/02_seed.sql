-- Tópicos 1-4 (DER, DML, DQL Básico, DQL Agregadas) — massa de dados de teste.
-- Populada de uma vez para servir a todos os tópicos que fazem apenas leitura
-- (DQL, JOINs, subconsultas, Views, índices). Preços, categorias e datas
-- previstas realistas, já que índices/transações/triggers, mais adiante,
-- dependem destes mesmos valores.
USE locadora;

-- Categorias raiz primeiro (id 1, 2, 3), depois a subcategoria de Romance (id 4).
INSERT INTO categoria (nome, categoria_pai_id) VALUES
    ('Literatura Brasileira', NULL),  -- id 1
    ('Romance', NULL),                -- id 2
    ('Ficção Científica', NULL);      -- id 3

INSERT INTO categoria (nome, categoria_pai_id) VALUES
    ('Romance Machadiano', 2);        -- id 4, subcategoria de Romance

INSERT INTO associado (nome, email) VALUES
    ('Ana Souza',    'ana@email.com'),
    ('Bruno Lima',   'bruno@gmail.com'),
    ('Carla Dias',   'carla@gmail.com'),
    ('Daniel Rocha', 'daniel@outlook.com'),
    ('Elisa Prado',  'elisa@gmail.com');

INSERT INTO livro (titulo, autor, isbn, preco, categoria_id, exemplares_disponiveis) VALUES
    ('Dom Casmurro',      'Machado de Assis',  '9788535910663', 39.90, 4, 2),
    ('Memórias Póstumas', 'Machado de Assis',  '9788535910664', 42.50, 4, 1),
    ('Iracema',           'José de Alencar',   '9788535910665', 29.90, 1, 3),
    ('A Hora da Estrela', 'Clarice Lispector', '9788535910666', 34.90, 1, 1),
    ('Fundação',          'Isaac Asimov',      '9788535910667', 54.90, 3, 2);

-- Dois empréstimos em aberto, dois já devolvidos, e um em aberto e vencido
-- (usado pelos relatórios de atraso dos tópicos seguintes).
INSERT INTO emprestimo (associado_id, livro_id, data_emprestimo, data_prevista, data_devolucao) VALUES
    (1, 1, '2026-07-05 09:00:00', '2026-07-19', NULL),
    (2, 3, '2026-07-12 14:30:00', '2026-07-26', NULL),
    (3, 2, '2026-07-01 10:00:00', '2026-07-15', '2026-07-14 11:00:00'),
    (1, 4, '2026-06-15 08:00:00', '2026-06-29', '2026-06-28 09:00:00'),
    (1, 5, '2026-07-31 16:00:00', '2026-08-14', NULL),
    (4, 1, '2026-06-01 08:00:00', '2026-06-15', NULL);

SELECT COUNT(*) AS total_associados FROM associado;
SELECT COUNT(*) AS total_categorias FROM categoria;
SELECT COUNT(*) AS total_livros FROM livro;
SELECT COUNT(*) AS total_emprestimos FROM emprestimo;
